package com.aioplayer.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayOutputStream;

/**
 * Created by akankshadhanda on 12/07/17.
 */
@DatabaseTable(tableName = "audiosongs")
public class AudioSong {
    @DatabaseField(columnName = "thisid",id=true)
    private long thisId;
    @DatabaseField(columnName = "thistitle")
    private String thisTitle;
    @DatabaseField(columnName = "thisalbumid")
    private long thisalbumId;
    @DatabaseField(columnName = "thisdata")
    private String thisdata;
    @DatabaseField(columnName = "duration")
    private String duration;

    public void setAlbumArt(byte[] albumArt) {
        this.albumArt = albumArt;
    }

    public PlayListItem getPlayListItem() {
        return playListItem;
    }

    public void setPlayListItem(PlayListItem playListItem) {
        this.playListItem = playListItem;
    }

    @DatabaseField(columnName = "albumname")

    private String albumName;

    @DatabaseField(foreign = true)
    protected PlayListItem playListItem;


    @DatabaseField(columnName = "albumart",dataType = DataType.BYTE_ARRAY)
    private byte[] albumArt;

    public Bitmap getAlbumArt() {
        if(albumArt!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
            return bitmap;
        }
           return null;

    }

    public void setAlbumArt(Bitmap albumArt) {
        if(albumArt!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            albumArt.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            this.albumArt = byteArray;
        }
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }
    @DatabaseField(columnName = "albumkey")
    private String AlbumKey;
    @DatabaseField(columnName = "albumpath")
    private String albumPath;

public AudioSong()
{

}
    public AudioSong(PodCastItem podCastItem)
    {
        this.thisdata = podCastItem.getFeedUrl();
        this.duration=podCastItem.getDuration();
        this.albumName=podCastItem.getType();
        this.thisTitle=podCastItem.getTitle();

    }
    public AudioSong(long thisId, String thisTitle, long thisalbumId, String thisdata, String albumKey, String albumPath, String duration,String albumName) {
        this.thisId = thisId;
        this.thisTitle = thisTitle;
        this.thisalbumId = thisalbumId;
        this.thisdata = thisdata;
        this.AlbumKey = albumKey;
        this.albumPath=albumPath;
        this.duration=duration;
        this.albumName=albumName;
    }

    public long getThisId() {
        return thisId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setThisId(long thisId) {
        this.thisId = thisId;
    }

    public String getThisTitle() {
        return thisTitle;
    }

    public void setThisTitle(String thisTitle) {
        this.thisTitle = thisTitle;
    }

    public long getThisalbumId() {
        return thisalbumId;
    }

    public void setThisalbumId(long thisalbumId) {
        this.thisalbumId = thisalbumId;
    }

    public String getThisdata() {
        return thisdata;
    }

    public void setThisdata(String thisdata) {
        this.thisdata = thisdata;
    }

    public String getAlbumKey() {
        return AlbumKey;
    }

    public void setAlbumKey(String albumKey) {
        AlbumKey = albumKey;
    }
}
