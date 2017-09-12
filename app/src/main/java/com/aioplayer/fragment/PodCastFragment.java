package com.aioplayer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.adapter.PodCastAdapter;
import com.aioplayer.dao.FeedRestResponse;
import com.aioplayer.dao.PodCastItem;
import com.aioplayer.dao.PodCastItemDownload;
import com.aioplayer.dao.RssFeedItem;
import com.aioplayer.manager.DbManager;
import com.aioplayer.retrofit.IRssFeedApi;
import com.aioplayer.util.NanoHTTPD;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
 * Created by akankshadhanda on 28/07/17.
 */

public class PodCastFragment extends BaseFragment {


    private ListView podcastListView;
    private ProgressBar progressBar;
    private List<RssFeedItem> feedItems;
    private MaterialSearchBar searchView;
    private boolean closrSuggestion;
    private PodCastAdapter podCastAdapter;
    Handler handler = new Handler();
    private DbManager dbHelper;
    private String pastInputString;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.podcast_fragment,container,false);
        podcastListView =(ListView)view.findViewById(R.id.listview);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        dbHelper = new DbManager(getMainActivity());

        podcastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                podcastListView.setClickable(false);
                fetchPodcastFeed(podCastAdapter.getItem(i).getId(),podCastAdapter.getItem(i).getUrl());
                dbHelper.saveRssObject(podCastAdapter.getItem(i));
            }
        });

        searchView=(MaterialSearchBar)view.findViewById(R.id.floating_search_view);
        searchView.setTextColor(android.R.color.black);
        searchView.setTextHintColor(android.R.color.darker_gray);
        fetchTop100PodCast();
        return view;
    }
public void fetchPodcastFeed(final String id,String podcastUrl)
{
    List<PodCastItem> podCastItems=dbHelper.fetchPodCastItemsNormal(podcastUrl);
    if(podCastItems==null) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Searcher searcher = new Searcher();
                Request request = new Request();
                request.setMedia(Media.PODCAST);
                request.setEntity(Entity.PODCAST);
                request.setLimit(1);
                request.setTerm(id);

                Response response = searcher.search(request);
                List<Result> results = response.getResults();
                Result podcastResult = results.get(0);
                parseFeedUrl(podcastResult.getFeedUrl());
            }
        }).start();
    }

}

    public void searchPodcastFeed(final String search)
    {
        if(search.equals(""))
        {

            podCastAdapter = new PodCastAdapter(getMainActivity(),feedItems,dbHelper.getAllRssObjects());
            podcastListView.setAdapter(podCastAdapter);
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Searcher searcher = new Searcher();
                    Request request = new Request();
                    request.setMedia(Media.PODCAST);
                    request.setEntity(Entity.PODCAST);
                    request.setTerm(search);
                    Response response = searcher.search(request);
                    List<Result> results = response.getResults();
                    final List<RssFeedItem> rssFeedItems = new ArrayList<>();
                    for (Result result : results) {
                        RssFeedItem rssFeedItem = new RssFeedItem(result);
                        rssFeedItems.add(rssFeedItem);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            podCastAdapter = new PodCastAdapter(getMainActivity(), rssFeedItems,dbHelper.getAllRssObjects());
                            podcastListView.setAdapter(podCastAdapter);
                        }
                    });

                }
            }).start();

        }

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

           podCastItems.add(podCastItem);
           dbHelper.savePodCastItemNormalObject(podCastItem);

       }

        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                podcastListView.setClickable(true);

                getMainActivity().showNextView(new PodCastItemFragment(podCastItems), null);
            }
        });
    } catch (ParserConfigurationException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (SAXException e) {
        e.printStackTrace();
    }
}
    @Override
    public String getTAG() {
        return "podcastfragment";
    }

    private void fetchTop100PodCast()
    {   List<RssFeedItem> rssFeedItems=dbHelper.getAllRssObjects();
        if(rssFeedItems.size()<100) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://rss.itunes.apple.com").addConverterFactory(JacksonConverterFactory.create()).client(okHttpClient)
                    .build();
            final IRssFeedApi feedApi = retrofit.create(IRssFeedApi.class);
            feedApi.listTopPodCast().enqueue(new Callback<FeedRestResponse>() {
                @Override
                public void onResponse(Call<FeedRestResponse> call, retrofit2.Response<FeedRestResponse> response) {
                    FeedRestResponse feedRestResponse = response.body();
                    if(feedRestResponse!=null&&feedRestResponse.getFeed()!=null) {
                        feedItems = feedRestResponse.getFeed().getResults();
                        List<RssFeedItem> rssFeedItems = dbHelper.getAllRssObjects();
                        for (RssFeedItem rssFeedItem : rssFeedItems) {
                            if (!feedItems.contains(rssFeedItem)) {
                                feedItems.add(rssFeedItem);
                            }
                        }
                        dbHelper.saveRssObjects(feedItems);
                        podCastAdapter = new PodCastAdapter(getMainActivity(), feedItems, dbHelper.getAllRssObjects());
                        podcastListView.setVisibility(View.VISIBLE);
                        podcastListView.setAdapter(podCastAdapter);
                        progressBar.setVisibility(View.GONE);
                        final LinkedList<String> suggestions = new LinkedList<>();
                        for (RssFeedItem podCastItem : feedItems) {
                            suggestions.add(podCastItem.getName().toLowerCase());
                        }
                        searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                            @Override
                            public void onSearchStateChanged(boolean enabled) {

                            }

                            @Override
                            public void onSearchConfirmed(CharSequence text) {
                                searchPodcastFeed(searchView.getText());
                            }

                            @Override
                            public void onButtonClicked(int buttonCode) {

                            }
                        });


                        searchView.addTextChangeListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (!closrSuggestion) {
                                    List<String> filteredSuggestion = new ArrayList<String>();
                                    for (String suggestion : suggestions) {
                                        if (suggestion.contains(charSequence.toString())) {
                                            filteredSuggestion.add(suggestion);
                                        }

                                    }
                                    searchView.setLastSuggestions(filteredSuggestion);
                                    searchView.enableSearch();

                                }
                                closrSuggestion = false;
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        searchView.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                            @Override
                            public void OnItemClickListener(int position, View v) {
                                searchView.disableSearch();
                                searchView.hideSuggestionsList();
                                closrSuggestion = true;
                                for (RssFeedItem rssFeedItem : feedItems) {
                                    if (rssFeedItem.getName().equalsIgnoreCase(searchView.getLastSuggestions().get(position).toString())) {
                                        fetchPodcastFeed(rssFeedItem.getId(), rssFeedItem.getUrl());
                                    }
                                }

                            }

                            @Override
                            public void OnItemDeleteListener(int position, View v) {

                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<FeedRestResponse> call, Throwable t) {
                    System.out.print(call);
                }
            });
        }
        else
        {
         if(feedItems==null)
         {
             feedItems = new ArrayList<>();
         }

            for (RssFeedItem rssFeedItem : rssFeedItems) {
                if (!feedItems.contains(rssFeedItem)) {
                    feedItems.add(rssFeedItem);
                }
            }
            podCastAdapter = new PodCastAdapter(getMainActivity(), feedItems, dbHelper.getAllRssObjects());
            podcastListView.setVisibility(View.VISIBLE);
            podcastListView.setAdapter(podCastAdapter);
            progressBar.setVisibility(View.GONE);
            final LinkedList<String> suggestions = new LinkedList<>();
            for (RssFeedItem podCastItem : feedItems) {
                suggestions.add(podCastItem.getName().toLowerCase());
            }
            searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    searchPodcastFeed(searchView.getText());
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });


            searchView.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (!closrSuggestion&&!charSequence.toString().equals("")) {
                        List<String> filteredSuggestion = new ArrayList<String>();
                        for (String suggestion : suggestions) {
                            if (suggestion.contains(charSequence)) {
                                filteredSuggestion.add(suggestion);
                            }
                        }
                        searchView.setLastSuggestions(filteredSuggestion);
                        searchView.enableSearch();
                    }
                    else
                    {
                        if(!charSequence.toString().equals(pastInputString)) {
                            searchView.hideSuggestionsList();
                            searchView.disableSearch();
                        }
                    }
                    pastInputString=charSequence.toString();
                    closrSuggestion = false;
                }

                @Override
                public void afterTextChanged(Editable editable) {


                }
            });

            searchView.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                @Override
                public void OnItemClickListener(int position, View v) {
                    searchView.disableSearch();
                    searchView.hideSuggestionsList();
                    closrSuggestion = true;
                    for (RssFeedItem rssFeedItem : feedItems) {
                        if (rssFeedItem.getName().equalsIgnoreCase(searchView.getLastSuggestions().get(position).toString())) {
                            fetchPodcastFeed(rssFeedItem.getId(),rssFeedItem.getUrl());
                        }
                    }

                }

                @Override
                public void OnItemDeleteListener(int position, View v) {

                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(searchView!=null)
        {
            searchView.hideSuggestionsList();
            searchView.disableSearch();

        }
    }
}
