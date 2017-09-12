package com.aioplayer.dao;

import android.graphics.Bitmap;

/**
 * Created by akankshadhanda on 12/08/17.
 */

public class PlayListHeader {
    private String title;
    private Bitmap imgaeUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImgaeUrl() {
        return imgaeUrl;
    }

    public void setImgaeUrl(Bitmap imgaeUrl) {
        this.imgaeUrl = imgaeUrl;
    }
}
