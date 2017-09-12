package com.aioplayer.evernote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.aioplayer.dao.FeedRestResponse;
import com.aioplayer.dao.RssFeedItem;
import com.aioplayer.manager.DbManager;
import com.aioplayer.retrofit.IRssFeedApi;
import com.evernote.android.job.Job;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by akankshadhanda on 17/08/17.
 */

public class FetchPodCastJob extends Job {
    public static final String TAG="fetchpodcastjob";
    private Context context;
    private DbManager dbManager;


    public FetchPodCastJob(Context context)
    {
        this.context=context;
        this.dbManager= new DbManager(context);
    }
    @Override
    protected Result onRunJob(Params params) {
        fetchTop100PodCast();
        return Result.SUCCESS;
    }
    public void fetchTop100PodCast()
    {
        OkHttpClient okHttpClient= new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rss.itunes.apple.com").addConverterFactory(JacksonConverterFactory.create()).client(okHttpClient)
                .build();
        IRssFeedApi feedApi=retrofit.create(IRssFeedApi.class);
        feedApi.listTopPodCast().enqueue(new Callback<FeedRestResponse>() {
            @Override
            public void onResponse(Call<FeedRestResponse> call, retrofit2.Response<FeedRestResponse> response) {
                FeedRestResponse feedRestResponse = response.body();
                List<RssFeedItem> feedItems = feedRestResponse.getFeed().getResults();
                dbManager.saveRssObjects(feedItems);

            }

            @Override
            public void onFailure(Call<FeedRestResponse> call, Throwable t) {

            }
        });
    }

}
