package com.aioplayer.evernote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.aioplayer.dao.FeedRestResponse;
import com.aioplayer.dao.PodCastItem;
import com.aioplayer.dao.PodCastItemDownload;
import com.aioplayer.dao.RssFeedItem;
import com.aioplayer.fragment.PodCastItemFragment;
import com.aioplayer.manager.DbManager;
import com.aioplayer.retrofit.IRssFeedApi;
import com.evernote.android.job.Job;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import be.ceau.itunessearch.Searcher;
import be.ceau.itunessearch.enums.Entity;
import be.ceau.itunessearch.enums.Media;
import be.ceau.itunessearch.models.Request;
import be.ceau.itunessearch.models.Response;
import be.ceau.itunessearch.models.Result;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by akankshadhanda on 23/08/17.
 */

public class UpdateSubscribedPodCast extends Job {
    public static final String TAG="updatesubcribedpodcast";
    private DbManager dbManager;
    private Context context;
    private ExecutorService executorService= Executors.newFixedThreadPool(2);
    public UpdateSubscribedPodCast(Context context)
    {
        this.dbManager= new DbManager(context);
        this.context=context;

    }

    public void fetchSubcribedPodCast()
    {
        List<RssFeedItem> subscribedItems =dbManager.getAllRssObjects();
        for(RssFeedItem rssFeedItem:subscribedItems)
        {
            fetchPodcastFeed(rssFeedItem.getId());
        }
    }

    public void fetchPodcastFeed(final String id)
    {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Searcher searcher =  new Searcher();
                Request request = new Request();
                request.setMedia(Media.PODCAST);
                request.setEntity(Entity.PODCAST);
                request.setLimit(1);
                request.setTerm(id);
                Response response=searcher.search(request);
                List<be.ceau.itunessearch.models.Result> results=response.getResults();
                be.ceau.itunessearch.models.Result podcastResult=results.get(0);
                parseFeedUrl(podcastResult.getFeedUrl());
            }
        });

    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        fetchSubcribedPodCast();
        return Result.SUCCESS;
    }

    public void parseFeedUrl(String urlPath) {

        try {
            URL url = new URL(urlPath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(url.openStream());
            final NodeList nodes = doc.getElementsByTagName("item");
            final Node imageItunes = doc.getElementsByTagName("itunes:image").item(0);
            NamedNodeMap namedNodeMap=imageItunes.getAttributes();
            String artistUrl=namedNodeMap.getNamedItem("href").getTextContent();
            int itemLength=nodes.getLength();
            final LinkedList<PodCastItem> podCastItems = new LinkedList<>();

            for(int itemIndex=0;itemIndex<itemLength;itemIndex++) {
                Node nodeItem = (Node) nodes.item(itemIndex);
                PodCastItem podCastItem=new PodCastItem();
                podCastItem.setPodcastUrl(artistUrl);
                Element element = (Element) nodeItem;
                NodeList nodeList = nodeItem.getChildNodes();
                int length = nodeList.getLength();
                for (int count = 0; count< length; count++) {
                    final Node node = nodeList.item(count);
                    if (node != null) {
                        final String nodeName = node.getNodeName();

                        if (nodeName.contains("title")) {
                            podCastItem.setTitle(node.getTextContent());

                        }
                        if (nodeName.contains("description")) {
                            podCastItem.setDescription(node.getTextContent());
                        }
                        if (nodeName.contains("enclosure")) {
                            namedNodeMap = node.getAttributes();
                            podCastItem.setFeedUrl(namedNodeMap.getNamedItem("url").getTextContent());
                            podCastItem.setType(namedNodeMap.getNamedItem("type").getTextContent());

                        }
                        if (nodeName.contains("pubDate")) {
                            podCastItem.setPubDate(node.getTextContent());

                        }
                        if (nodeName.contains("itunes:duration")) {
                            podCastItem.setDuration(node.getTextContent());

                        }


                    }

                }

                dbManager.savePodCastItemNormalObject(podCastItem);
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
