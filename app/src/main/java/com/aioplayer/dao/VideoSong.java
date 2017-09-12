package com.aioplayer.dao;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

/**
 * Created by akankshadhanda on 13/07/17.
 */

public class VideoSong {
    private long thisId;
    private String thisTitle;
    private String thisArtist;
    private String thisdata;
    private Bitmap thumbNail;

    public VideoSong(long thisId, String thisTitle, String thisArtist, String thisdata, int duration) {
        this.thisId = thisId;
        this.thisTitle = thisTitle;
        this.thisArtist = thisArtist;
        this.thisdata = thisdata;
        this.duration = duration;


    }

    public Bitmap getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(Bitmap thumbNail) {
        this.thumbNail = thumbNail;
    }

    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public VideoSong()
    {

    }


    public long getThisId() {
        return thisId;
    }

    public void setThisId(long thisId) {
        this.thisId = thisId;
    }

    public String getThisTitle() {
        return thisTitle;
    }

    public void setThisTitle(String thisTitle) {
        this.thisTitle = thisTitle;
    }

    public String getThisArtist() {
        return thisArtist;
    }

    public void setThisArtist(String thisArtist) {
        this.thisArtist = thisArtist;
    }

    public String getThisdata() {
        return thisdata;
    }

    public void setThisdata(String thisdata) {
        this.thisdata = thisdata;
    }



}
