package com.aioplayer.retrofit;

import com.aioplayer.dao.FeedRestResponse;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by akankshadhanda on 28/07/17.
 */

public interface IRssFeedApi {
    @GET("/api/v1/us/podcasts/top-podcasts/100/explicit.json")
    Call<FeedRestResponse> listTopPodCast();
}
