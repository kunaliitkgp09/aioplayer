package com.aioplayer.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by akankshadhanda on 14/08/17.
 */

public class PlayAudioMusic {
    private String title;
    private String subtitle;
    private String albumName;
    private long albumId;
    private String albumUrl;
    private Bitmap albumArt;
    private String value;

    public PlayAudioMusic()
    {

    }

    public PlayAudioMusic(AudioSong audioSong)
    {
     this.title=audioSong.getThisTitle();
        this.subtitle=audioSong.getAlbumName();
        this.albumName=audioSong.getAlbumName();
        this.albumId=audioSong.getThisalbumId();
        this.albumUrl=audioSong.getThisdata();
        this.albumArt=audioSong.getAlbumArt();
        this.value=audioSong.getThisdata();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PlayAudioMusic(PodCastItem podCastItem)

    {
        this.title=podCastItem.getTitle();
        this.subtitle=podCastItem.getDescription();
        this.albumName=podCastItem.getType();
        this.value=podCastItem.getFeedUrl();
    }

    public PlayAudioMusic(PodCastItemDownload podCastItemDownload)
    {
        if(podCastItemDownload!=null) {
            this.title = podCastItemDownload.getTitle();
            this.subtitle = podCastItemDownload.getDescription();
            this.albumName = podCastItemDownload.getType();
            this.value = podCastItemDownload.getPodcastUrl();
        }
    }


}
