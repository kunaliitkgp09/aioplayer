package com.aioplayer.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.Toast;

import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PodCastItem;
import com.aioplayer.dao.VideoSong;
import com.aioplayer.event.PhotoCastEvent;
import com.aioplayer.event.PlayMediaContent;
import com.aioplayer.server.FileServer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.casty.MediaData;

/**
 * Created by akankshadhanda on 26/07/17.
 */

public class MediaCastService extends Service {
    private FileServer contentfileServer;
    private FileServer thumbnailfileServer;
    private Bus eventBus;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus=((SongApplication)getApplication()).getEventBus();
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Subscribe
    public void createVideoCastContent(VideoSong videoSong)
    {

        createWebServer(videoSong.getThisdata(),null,videoSong.getThumbNail());
        createCastVideoContent(videoSong);

    }

    @Subscribe
    public void createAudioCastContent(AudioSong audioSong)
    {

        createWebServer(audioSong.getThisdata(),null,audioSong.getAlbumArt());
        createCastAudioContent(audioSong);

    }
    @Subscribe
    public void createPhotoCastContentEvent(PhotoCastEvent photoCastEvent)
    {

        createWebServer(photoCastEvent.getUrl(),photoCastEvent.getUrl(),null);
        createPhotoCastContent(photoCastEvent);

    }

    @Subscribe
    public void createPodCastContentEvent(PodCastItem podCastItem)
    {

//        createWebServer(podCastItem.getPodcastUrl(),podCastItem.getPodcastUrl(),null);
        createPodCastContent(podCastItem);

    }

    public void createWebServer(String data,String thumbNail,Bitmap thumbNailMap)
    {
        if (contentfileServer!= null
                ) {
            contentfileServer.stop();
        }

        contentfileServer=new FileServer(this,data,8089);
        try {

            contentfileServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (thumbnailfileServer!= null
                ) {
            thumbnailfileServer.stop();
        }
        if(thumbNailMap!=null) {
            thumbnailfileServer = new FileServer(this, thumbNailMap, 8090);
        }
        else
        {
            thumbnailfileServer = new FileServer(this, thumbNail, 8090);
        }
        try {

            thumbnailfileServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getType(String uriPath)
    {
        if(uriPath.endsWith(".m4a"))
        {
            return "audio/m4a";
        }
        else if(uriPath.endsWith(".mp4"))
        {
            return "videos/mp4";
        }
        else if(uriPath.endsWith(".mp3"))
        {
            return "audio/mp3";
        }
       else if(uriPath.endsWith(".vp8"))
        {
            return "videos/webm";
        }
        else if(uriPath.endsWith(".aac")) {
            return  "audio/x-aac";
        }
        else if(uriPath.endsWith(".wav")) {
            return "audio/x-wav";
        }
        else if(uriPath.endsWith(".gif")) {
            return "image/gif";
        }
        else if(uriPath.endsWith(".bmp")) {
            return  "image/bmp";
        }
        else if(uriPath.endsWith(".jpeg")||uriPath.endsWith(".jpg")) {
            return "image/jpeg";
        }
       else if(uriPath.endsWith(".png")) {
            return "image/png";
        }
        else if(uriPath.endsWith(".webp")) {
            return  "image/webp";
        }
        else
        {
            return "";
        }
    }

    public void createCastVideoContent(VideoSong videoSong)
    {
        WifiManager wm = (WifiManager) this.getSystemService(WIFI_SERVICE);
//        mainActivity.getCasty().configure("99A47AC8");
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(this,ip,Toast.LENGTH_LONG).show();
        File file= new File(videoSong.getThisdata());
        MediaData mediaData = new MediaData.Builder("http://"+ip+":8089/"+file.getName())
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED).setSubtitle(videoSong.getThisArtist()).setTitle(videoSong.getThisTitle())
                //required
                .setContentType(getType(videoSong.getThisdata())).build();
        PlayMediaContent playMediaContent = new PlayMediaContent();
        playMediaContent.setMediaData(mediaData);
       eventBus.post(playMediaContent);



    }


    public void createPodCastContent(PodCastItem podCastItem)
    {
        WifiManager wm = (WifiManager) this.getSystemService(WIFI_SERVICE);
//        mainActivity.getCasty().configure("99A47AC8");
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(this,ip,Toast.LENGTH_LONG).show();

        MediaData mediaData = new MediaData.Builder(podCastItem.getFeedUrl()).addPhotoUrl(podCastItem.getPodcastUrl())
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED).setSubtitle(podCastItem.getDescription()).setTitle(podCastItem.getTitle())
                //required
                .setContentType(getType(podCastItem.getFeedUrl())).build();
        PlayMediaContent playMediaContent = new PlayMediaContent();
        playMediaContent.setMediaData(mediaData);
        eventBus.post(playMediaContent);



    }

    public void createPhotoCastContent(PhotoCastEvent photoCastEvent)
    {
        WifiManager wm = (WifiManager) this.getSystemService(WIFI_SERVICE);
//        mainActivity.getCasty().configure("99A47AC8");
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(this,ip,Toast.LENGTH_LONG).show();
        File file= new File(photoCastEvent.getUrl());
        MediaData mediaData = new MediaData.Builder("http://"+ip+":8089/"+file.getName())
                .setStreamType(MediaData.STREAM_TYPE_NONE).setSubtitle(photoCastEvent.getUrl()).setTitle(photoCastEvent.getUrl())
                //required
                .setContentType(getType(photoCastEvent.getUrl())).build();
        PlayMediaContent playMediaContent = new PlayMediaContent();
        playMediaContent.setMediaData(mediaData);
        eventBus.post(playMediaContent);



    }
    public void createCastAudioContent(AudioSong audioSong)
    {
        WifiManager wm = (WifiManager) this.getSystemService(WIFI_SERVICE);
//        mainActivity.getCasty().configure("99A47AC8");
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(this,ip,Toast.LENGTH_LONG).show();
        File file= new File(audioSong.getThisdata());
        MediaData mediaData = new MediaData.Builder("http://"+ip+":8089/"+file.getName())
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED).setSubtitle(audioSong.getAlbumName()).setTitle(audioSong.getThisTitle())
                //required
                .setContentType(getType(audioSong.getThisdata())).build();
        PlayMediaContent playMediaContent = new PlayMediaContent();
        playMediaContent.setMediaData(mediaData);
        eventBus.post(playMediaContent);



    }


}
