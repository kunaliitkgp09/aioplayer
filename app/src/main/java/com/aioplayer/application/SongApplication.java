package com.aioplayer.application;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.aioplayer.evernote.AIOjobcreator;
import com.aioplayer.evernote.FetchPodCastJob;
import com.aioplayer.evernote.UpdateSubscribedPodCast;
import com.aioplayer.utils.AppState;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.squareup.otto.Bus;

/**
 * Created by akankshadhanda on 13/07/17.
 */

public class SongApplication extends MultiDexApplication{
    private Bus eventBus;

    public Bus getEventBus() {
        return eventBus;
    }

    public void setEventBus(Bus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        eventBus=new Bus();
        JobManager.create(this).addJobCreator(new AIOjobcreator(this));
       AppState appState= AppState.getInstance(this);
        if(!appState.getBoolean("jobscheduled")) {
            new JobRequest.Builder(FetchPodCastJob.TAG)
                    .setPeriodic(86400000, 43200000).setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .build()
                    .schedule();
            new JobRequest.Builder(FetchPodCastJob.TAG)
                    .setExact(System.currentTimeMillis() + 2000).setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .build()
                    .schedule();
            new JobRequest.Builder(UpdateSubscribedPodCast.TAG)
                    .setPeriodic(86400000, 43200000).setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .build()
                    .schedule();

            appState.saveBoolean("jobscheduled",true);
        }

    }
}
