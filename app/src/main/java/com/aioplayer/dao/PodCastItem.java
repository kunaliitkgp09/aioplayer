package com.aioplayer.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by akankshadhanda on 30/07/17.
 */
@DatabaseTable(tableName = "podcastitem")
public class PodCastItem {
    @DatabaseField(columnName = "title")
    private String title;
    @DatabaseField(columnName = "feedurl",id=true)
    private String feedUrl;
    @DatabaseField(columnName = "description")
    private String description;
    @DatabaseField(columnName = "type")
    private String type;
    @DatabaseField(columnName = "duration")
    private String duration;
    @DatabaseField(columnName = "podcastUrl")
    private String podcastUrl;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    private String pubDate ;

    public String getType() {
        return type;
    }

    public String getPodcastUrl() {
        return podcastUrl;
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
