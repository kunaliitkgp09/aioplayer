package com.aioplayer.service;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.aioplayer.MainActivity;
import com.aioplayer.R;
import com.aioplayer.application.SongApplication;
import com.aioplayer.dao.PodCastItemDownload;
import com.aioplayer.event.FileDownloadEvent;
import com.aioplayer.manager.DbManager;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


import okhttp3.OkHttpClient;

/**
 * Created by akankshadhanda on 02/08/17.
 */

public class FileDownloadService extends Service {
    private Bus eventBus;
    private Fetch fetch;
    private DbManager dbManager;
    private HashSet<String> urls = new HashSet<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.eventBus=((SongApplication) getApplication()).getEventBus();
        this.eventBus.register(this);
        this.fetch=Fetch.newInstance(this);
        this.dbManager= new DbManager(this);
        new Fetch.Settings(getApplicationContext())
                .setAllowedNetwork(Fetch.NETWORK_ALL)
                .enableLogging(true)
                .setConcurrentDownloadsLimit(3)
                .apply();


    }
    @Subscribe
    public void startDownload(final FileDownloadEvent fileDownloadEvent)
    {

            String[] fileParts = fileDownloadEvent.getUrl().replaceAll("//", "").split("/");
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .build();
            AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
            File file = new File(getFilesDir().getAbsolutePath(), fileParts[fileParts.length - 1]);

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        if(fileDownloadEvent.getType().equals("download")) {
            urls.add(fileDownloadEvent.getUrl());
            updateNotification();
            AndroidNetworking.download(fileDownloadEvent.getUrl(), getFilesDir().getAbsolutePath(), fileParts[fileParts.length - 1])
                    .setTag(fileDownloadEvent.getUrl())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            // do anything with progress
                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            // do anything after completion

                            PodCastItemDownload podCastItemDownload = dbManager.findPodCastItemwithId(fileDownloadEvent.getUrl());
                            podCastItemDownload.setDownloadCompeleted(true);
                            dbManager.updatePodCastItem(podCastItemDownload);
                            Toast.makeText(FileDownloadService.this, podCastItemDownload.getTitle()+" download compeleted", Toast.LENGTH_LONG).show();
                            urls.remove(fileDownloadEvent.getUrl());
                            updateNotification();

                        }

                        @Override
                        public void onError(ANError error) {
                            PodCastItemDownload podCastItemDownload = dbManager.findPodCastItemwithId(fileDownloadEvent.getUrl());
                            dbManager.removeObject(podCastItemDownload);
                            urls.remove(fileDownloadEvent.getUrl());
                            updateNotification();
                            // handle error
                            Toast.makeText(FileDownloadService.this, podCastItemDownload.getTitle()+" download failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else
        {
            urls.remove(fileDownloadEvent.getUrl());
            updateNotification();
            AndroidNetworking.cancel(fileDownloadEvent.getUrl());
            file.delete();
        }

    }
    private Notification getMyActivityNotification(String text){
        // The PendingIntent to launch our activity if the user selects
        // this notification
        CharSequence title = urls.size()+" file downloading";
        if(urls.size()==0)
        {
            stopForeground(false);
            return null;
        }
        else {
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, new Intent(this, MainActivity.class), 0);

            return new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.material_drawer_circle_mask)
                    .setContentIntent(contentIntent).getNotification();
        }
    }

    private void updateNotification() {
        String text = "Download in progress";
        Notification notification = getMyActivityNotification(text);
        if(notification!=null) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(95445, notification);
        }
    }
}
