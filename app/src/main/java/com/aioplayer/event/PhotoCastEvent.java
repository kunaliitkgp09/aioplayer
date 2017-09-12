package com.aioplayer.event;

/**
 * Created by akankshadhanda on 27/07/17.
 */

public class PhotoCastEvent {
    private String url ;
    public PhotoCastEvent(String url)
    {
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
