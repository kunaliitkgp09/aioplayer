package com.aioplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextClassification;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aioplayer.R;
import com.aioplayer.dao.PhonePhoto;
import com.aioplayer.dao.RssFeedItem;
import com.aioplayer.fragment.BaseFragment;
import com.aioplayer.manager.DbManager;
import com.bumptech.glide.Glide;

import java.util.List;


/**
 * Created by akankshadhanda on 29/07/17.
 */

public class PodCastAdapter extends BaseAdapter {

    private Context context;
    private List<RssFeedItem> feedItemList;
    private List<RssFeedItem> subscribedFeed;
    public PodCastAdapter(Context context,List<RssFeedItem> rssFeedItemList,List<RssFeedItem> subscribedFeed)
    {
        this.context=context;
        this.feedItemList=rssFeedItemList;
        this.subscribedFeed=subscribedFeed;
    }

    @Override
    public int getCount() {
        return feedItemList.size();
    }

    @Override
    public RssFeedItem getItem(int i) {
        return feedItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.podcast_item, null);
        final DbManager dbManager = new DbManager(context);

        final RssFeedItem rssFeedItem =getItem(i);
        final Button subscribeButton=(Button) view.findViewById(R.id.subcribebutton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subscribedFeed.contains(rssFeedItem)) {
                    dbManager.removeRssObject(rssFeedItem);
                    subscribeButton.setText("subscribe");
                    subscribedFeed.remove(rssFeedItem);
                }
                else {
                    dbManager.saveRssObject(rssFeedItem);
                    subscribeButton.setText("unsubscribe");
                    subscribedFeed.add(rssFeedItem);
            }
        }});
        if(subscribedFeed.contains(rssFeedItem)) {
                subscribeButton.setText("unsubscribe");
            }
        else {
            subscribeButton.setText("subscribe");
        }
        ImageView albumArt =(ImageView) view.findViewById(R.id.imageview);
        TextView title =(TextView) view.findViewById(R.id.title);
        title.setText(rssFeedItem.getName());
        TextView subTitle=(TextView)view.findViewById(R.id.subtitle);
        subTitle.setText(rssFeedItem.getArtistName());
        Glide.with(context)
                .load(rssFeedItem.getArtworkUrl100()).placeholder(R.drawable.cast_ic_notification_play)
                .into(albumArt);
        return view;
    }


}
