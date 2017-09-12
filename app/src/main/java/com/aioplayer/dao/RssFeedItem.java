package com.aioplayer.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import be.ceau.itunessearch.models.Result;

/**
 * Created by akankshadhanda on 28/07/17.
 */
@DatabaseTable(tableName = "rssfeeditems")
public class RssFeedItem {
    @DatabaseField(columnName = "artistid")
    private String artistId;
    @DatabaseField(columnName = "artistname")
    private String artistName;
    @DatabaseField(columnName = "artisturl")
    private String artistUrl;
    @DatabaseField(columnName = "artisturl100")
    private String artworkUrl100;
    @DatabaseField(columnName = "contentadvisoryrating")
    private String contentAdvisoryRating;
    @DatabaseField(columnName = "copyright")
    private String copyright;


    @DatabaseField(columnName = "genrenames",persisted = false)
    @JsonIgnore
    private HashMap<String,String> genres;
    @DatabaseField(id = true)
    private String id;
    @DatabaseField(columnName = "kind")
    private String kind;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(columnName = "primarygenrename")
    private String primaryGenreName;
    @DatabaseField(columnName = "releasedate")
    private String releaseDate;
    @DatabaseField(columnName = "trackcensoredname")
    private String trackCensoredName;
    @DatabaseField(columnName = "trackexplicitness")
    private String trackExplicitness;

    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "version")
    private String version;



    public RssFeedItem()
    {

    }

    public RssFeedItem(Result result)
    {
        this.artistId=String.valueOf(result.getArtistId());
        this.artistName=result.getArtistName();
        this.artistUrl=result.getArtistViewUrl();
        this.artworkUrl100=result.getArtworkUrl100();
        this.contentAdvisoryRating=result.getContentAdvisoryRating();
        this.copyright=result.getCopyright();
        this.genres= new HashMap<>();
        this.id=String.valueOf((int)result.getCollectionId().longValue());
        this.kind=result.getKind();
        this.name=result.getCollectionName();
        this.primaryGenreName=result.getPrimaryGenreName();
        this.releaseDate=result.getReleaseDate();
        this.trackCensoredName=result.getTrackCensoredName();
        this.trackExplicitness=result.getTrackExplicitness();
        this.url=result.getFeedUrl();
        this.version=result.getVersion();
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public void setArtistUrl(String artistUrl) {
        this.artistUrl = artistUrl;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    public String getContentAdvisoryRating() {
        return contentAdvisoryRating;
    }

    public void setContentAdvisoryRating(String contentAdvisoryRating) {
        this.contentAdvisoryRating = contentAdvisoryRating;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }



    public String getId() {
        return id;
    }

    public HashMap<String, String> getGenres() {
        return genres;
    }

    public void setGenres(HashMap<String, String> genres) {
        this.genres = genres;
    }



    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RssFeedItem that = (RssFeedItem) o;

        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    public String getKind() {
        return kind;
    }




    public void setKind(String kind) {
        this.kind = kind;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    public void setPrimaryGenreName(String primaryGenreName) {
        this.primaryGenreName = primaryGenreName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTrackCensoredName() {
        return trackCensoredName;
    }

    public void setTrackCensoredName(String trackCensoredName) {
        this.trackCensoredName = trackCensoredName;
    }

    public String getTrackExplicitness() {
        return trackExplicitness;
    }

    public void setTrackExplicitness(String trackExplicitness) {
        this.trackExplicitness = trackExplicitness;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }




}
