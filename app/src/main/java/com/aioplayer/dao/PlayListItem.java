package com.aioplayer.dao;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by akankshadhanda on 07/08/17.
 */
@DatabaseTable(tableName = "playlistitem")
public class PlayListItem {
    @DatabaseField(columnName = "playlistname",id=true)
    private String playListName;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @DatabaseField(columnName = "imageurl")

    private String imageurl;
    @ForeignCollectionField()
    private Collection<AudioSong> audioSongs = new ArrayList<>();



    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public Collection<AudioSong> getAudioSongs() {
        return audioSongs;
    }

    public void setAudioSongs(Collection<AudioSong> audioSongs) {
        this.audioSongs = audioSongs;
    }




}
