package com.aioplayer.event;

/**
 * Created by akankshadhanda on 30/07/17.
 */

public class ShowAudioController {
    private long startTime;
    private long maxtime;

    public ShowAudioController(long startTime,long maxtime)
    {
        this.startTime=startTime;
        this.maxtime=maxtime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getMaxtime() {
        return maxtime;
    }

    public void setMaxtime(long maxtime) {
        this.maxtime = maxtime;
    }


}
