package com.aioplayer.dao;

/**
 * Created by akankshadhanda on 27/07/17.
 */

public class PhonePhoto {

    private int id;
    private String albumName;
    private String photoUri;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName( String name ) {
        this.albumName = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri( String photoUri ) {
        this.photoUri = photoUri;
    }
}