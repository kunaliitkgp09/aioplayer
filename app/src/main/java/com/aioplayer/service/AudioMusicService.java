package com.aioplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;
import com.aioplayer.event.AudioMusicEvent;
import com.aioplayer.event.MusicProgressViewUpdate;
import com.aioplayer.event.ShowAudioController;
import com.aioplayer.utils.AppState;
import com.aioplayer.utils.Constants;
import com.bumptech.glide.load.resource.bitmap.ImageVideoBitmapDecoder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;



import static com.aioplayer.utils.Constants.ACTION.NEXT_ACTION;
import static com.aioplayer.utils.Constants.ACTION.STARTFOREGROUND_ACTION;

/**
 * Created by akankshadhanda on 13/07/17.
 */

public class AudioMusicService extends Service {
    private MediaPlayer mediaPlayer;
    private Handler mHandler;
    private Bus bus;
    private int currentIndex=0;
    private List<PlayAudioMusic> playAudioMusics;
    private PlayAudioMusic currentPlayAudioMusics;
    private boolean shufffleBoolean=false;
    private boolean fromNext=false;
    private boolean pastMediaPlayerState=false;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            MusicProgressViewUpdate musicProgressViewUpdate = new MusicProgressViewUpdate();
            if (mediaPlayer.getDuration() > 0) {
                musicProgressViewUpdate.setProgressValue((100 * mediaPlayer.getCurrentPosition()) / mediaPlayer.getDuration());
            }
            bus.post(new ShowAudioController(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
            bus.post(musicProgressViewUpdate);
            mHandler.postDelayed(this, 1000);

        }


    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SongApplication songApplication=(SongApplication)getApplication();
        bus=songApplication.getEventBus();
        bus.register(this);
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

       if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            bus.post(new AudioMusicEvent(AudioMusicEvent.PREVIOUS,null));
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            bus.post(new AudioMusicEvent(AudioMusicEvent.PAUSE,null));
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            bus.post(new AudioMusicEvent(AudioMusicEvent.NEXT,null));
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {

            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Subscribe
    public void receiveEvent(AudioMusicEvent musicEvent) {
       fromNext=false;
        if (musicEvent.getCommand() == AudioMusicEvent.PLAY) {
            currentIndex=0;
            playAudioMusics=musicEvent.getPlayAudioMusics();
            runAudio();



        }
        else if(musicEvent.getCommand() == AudioMusicEvent.PAUSE)
        {
            if(mediaPlayer!=null&&mediaPlayer.isPlaying())
            {
                mediaPlayer.pause();
                mHandler.removeCallbacks(runnable);
                createNotification(false);
            }
            else
            {
                if(mediaPlayer!=null) {
                    mediaPlayer.start();
                    mHandler.postDelayed(runnable, 1000);
                    createNotification(true);
                }
            }
        }
        else if(musicEvent.getCommand()== AudioMusicEvent.FASTFORWARD)
        {
            mediaPlayer.seekTo(Math.min(mediaPlayer.getCurrentPosition()+10000,mediaPlayer.getDuration()));

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.REWIND)
        {
            mediaPlayer.seekTo(Math.max(mediaPlayer.getCurrentPosition()-10000,0));
        }
        else if(musicEvent.getCommand()== AudioMusicEvent.ADD)
        {
            if(playAudioMusics==null)
            {
                musicEvent.setCommand(AudioMusicEvent.PLAY);
                receiveEvent(musicEvent);
            }
            else
            {
                playAudioMusics.addAll(musicEvent.getPlayAudioMusics());
            }
        }
        else if(musicEvent.getCommand()== AudioMusicEvent.REPLAY)
        {
            if(mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
            }
            else
            {
                mediaPlayer.setLooping(true);
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.MOVE)
        {
            if(mediaPlayer!=null) {
                mediaPlayer.pause();
                mediaPlayer.seekTo((int)((mediaPlayer.getDuration()*Double.parseDouble(musicEvent.getPlayAudioMusics().get(0).getValue()))/100));
               mediaPlayer.start();
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.NEXT)
        {
            if(mediaPlayer!=null) {
                currentIndex++;
                fromNext=true;
                runAudio();
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.PREVIOUS)
        {
            if(mediaPlayer!=null) {
                currentIndex--;
                runAudio();
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.LOOPONE)
        {
            if(mediaPlayer!=null) {
                mediaPlayer.setLooping(true);
                shufffleBoolean=false;
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.SHUFFLE)
        {
            if(mediaPlayer!=null) {
                mediaPlayer.setLooping(false);
                shufffleBoolean=true;
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.NORMALPLAY)

        {
            if(mediaPlayer!=null) {
                mediaPlayer.setLooping(false);
                shufffleBoolean=false;
            }

        }
        else if(musicEvent.getCommand()== AudioMusicEvent.CASTCONNECTED)

        {
            if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pastMediaPlayerState=false;
            }
            else
            {
                pastMediaPlayerState=true;
            }

        }

        else if(musicEvent.getCommand()== AudioMusicEvent.CASTDISCONNECTED)

        {
            if(mediaPlayer!=null&&pastMediaPlayerState) {
                mediaPlayer.start();

            }
            else if(mediaPlayer!=null&&!pastMediaPlayerState)
            {
                mediaPlayer.pause();
            }
            createNotification(pastMediaPlayerState);

        }

    }


    public void runAudio() {

        try {

            if (currentIndex < 0) {
                currentIndex = 0;
            }
            else if (currentIndex < playAudioMusics.size()) {
                currentPlayAudioMusics = playAudioMusics.get(currentIndex);
            }

            if (currentIndex<playAudioMusics.size()) {


                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(false);
                    mediaPlayer.release();

                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(playAudioMusics.get(currentIndex).getValue());
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        mHandler = new Handler();
                        bus.post(new ShowAudioController(0, mediaPlayer.getDuration()));
//Make sure you update Seekbar on UI thread

                        mHandler.post(runnable);
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        if(!shufffleBoolean){
                            currentIndex++;
                        }
                        else
                        {
                            currentIndex=(int)(Math.random()*playAudioMusics.size());
                        }
                        runAudio();

                    }
                });
                createNotification(true);


            }
            else if(fromNext)
            {
                fromNext=false;
                currentIndex=playAudioMusics.size()-1;
                runAudio();
            }
        }
               catch(IOException e){
                e.printStackTrace();
            }


    }

    public void createNotification(boolean pause)
    {
        int requestID = (int) System.currentTimeMillis();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_builder);

        Intent previousIntent = new Intent(this, AudioMusicService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, requestID,
                previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, AudioMusicService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, requestID,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, AudioMusicService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, requestID,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
           Bitmap albumBitmap=currentPlayAudioMusics.getAlbumArt();
            if(albumBitmap!=null) {
                mRemoteViews.setImageViewBitmap(R.id.albumart, albumBitmap);
            }
            else
            {
               String albumUrl=currentPlayAudioMusics.getAlbumUrl();
                if(albumUrl!=null)
                mRemoteViews.setImageViewUri(R.id.albumart, Uri.parse(albumUrl));
            }
        String subtitle=currentPlayAudioMusics.getSubtitle();
        if(subtitle!=null) {
            mRemoteViews.setTextViewText(R.id.subtitle, subtitle);
        }
        String title=currentPlayAudioMusics.getTitle();
        if(title!=null)
            mRemoteViews.setTextViewText(R.id.title,title);
        if(pause) {
            ;
            mRemoteViews.setImageViewBitmap(R.id.pauseplay,drawableToBitmap(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_pause).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP)));
        }
        else
        {
            mRemoteViews.setImageViewBitmap(R.id.pauseplay, drawableToBitmap(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_play_arrow).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP)));
        }
        if(playAudioMusics.size()>0) {
            mRemoteViews.setImageViewBitmap(R.id.nexticon, drawableToBitmap(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_skip_next).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP)));
            mRemoteViews.setImageViewBitmap(R.id.backicon, drawableToBitmap(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_skip_previous).color(Color.BLACK).sizeDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP).paddingDp(IconicsDrawable.ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP)));
        }
        mRemoteViews.setOnClickPendingIntent(R.id.backicon,ppreviousIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.pauseplay,pplayIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.nexticon,pnextIntent);
        Notification notification = new NotificationCompat.Builder(this)
                .setCustomContentView(mRemoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
        AppState appState=AppState.getInstance(this);
        appState.saveBoolean("isPlaying",true);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



}
