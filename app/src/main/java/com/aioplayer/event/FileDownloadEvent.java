package com.aioplayer.event;

/**
 * Created by akankshadhanda on 02/08/17.
 */

public class FileDownloadEvent {
    private String url;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
