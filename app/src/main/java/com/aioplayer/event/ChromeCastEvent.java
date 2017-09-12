package com.aioplayer.event;

/**
 * Created by akankshadhanda on 17/07/17.
 */

public class ChromeCastEvent {
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static final int CONNECTED=0;
    public static final int DISCONNECTED=1;
    private int state;
    public ChromeCastEvent(int state)
    {
        this.state=state;
    }
}
