package com.aioplayer.event;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.aioplayer.dao.AudioSong;
import com.aioplayer.dao.PlayAudioMusic;

import java.io.FileDescriptor;
import java.util.List;

/**
 * Created by akankshadhanda on 13/07/17.
 */

public class AudioMusicEvent {
    public static final int PLAY=0;
    public static final int PAUSE=1;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public static final int NEXT=2;
    public static final int PREVIOUS=3;
    public static final int FASTFORWARD=4;



    public List<PlayAudioMusic> getPlayAudioMusics() {
        return playAudioMusics;
    }

    public void setPlayAudioMusics(List<PlayAudioMusic> playAudioMusics) {
        this.playAudioMusics = playAudioMusics;
    }

    public static final int REWIND=5;
    public static final int REPLAY=6;
    public static final int MOVE=7;
    public static final int ADD=8;
    public static final int SHUFFLE=9;
    public static final int LOOPONE=10;
    public static final int NORMALPLAY=11;
    public static final int CASTCONNECTED=11;
    public static final int CASTDISCONNECTED=12;
    private int command;
    private List<PlayAudioMusic> playAudioMusics;


    public AudioMusicEvent(int command,List<PlayAudioMusic> playAudioMusics)
    {
        this.command=command;
         this.playAudioMusics=playAudioMusics;
    }





}
