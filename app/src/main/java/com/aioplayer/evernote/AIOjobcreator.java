package com.aioplayer.evernote;

import android.content.Context;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by akankshadhanda on 17/08/17.
 */

public class AIOjobcreator implements JobCreator {
    private Context context;
    public AIOjobcreator(Context context)
    {
        this.context=context;
    }
    @Override
    public Job create(String tag) {
        switch (tag) {
            case FetchPodCastJob.TAG:
                return new FetchPodCastJob(context);
            case UpdateSubscribedPodCast.TAG:
                return new UpdateSubscribedPodCast(context);
            default:
                return null;
        }
    }
}
