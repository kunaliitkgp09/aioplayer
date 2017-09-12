package com.aioplayer.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by akankshadhanda on 05/08/17.
 */
@DatabaseTable(tableName = "podcastitemdonwload")
public class PodCastItemDownload {
    @DatabaseField(columnName = "title")
    private String title;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPodcastUrl() {
        return podcastUrl;
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
    }

    public boolean isDownloadCompeleted() {
        return downloadCompeleted;
    }

    public void setDownloadCompeleted(boolean downloadCompeleted) {
        this.downloadCompeleted = downloadCompeleted;
    }

    @DatabaseField(columnName = "feedurl",id = true)
    private String feedUrl;
    @DatabaseField(columnName = "description")
    private String description;
    @DatabaseField(columnName = "type")
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PodCastItemDownload that = (PodCastItemDownload) o;

        if (downloadCompeleted != that.downloadCompeleted) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (feedUrl != null ? !feedUrl.equals(that.feedUrl) : that.feedUrl != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null)
            return false;
        return podcastUrl != null ? podcastUrl.equals(that.podcastUrl) : that.podcastUrl == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (feedUrl != null ? feedUrl.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (podcastUrl != null ? podcastUrl.hashCode() : 0);
        result = 31 * result + (downloadCompeleted ? 1 : 0);
        return result;
    }

    public PodCastItemDownload() {
    }

    @DatabaseField(columnName = "duration")
    private String duration;
    @DatabaseField(columnName = "podcasturl")
    private String podcastUrl;
    @DatabaseField(columnName = "downloadcompeleted")
   private boolean downloadCompeleted =false;

    public PodCastItemDownload(PodCastItem podCastItem)
    {
      this.title=podCastItem.getTitle();
        this.feedUrl=podCastItem.getFeedUrl();
        this.description=podCastItem.getDescription();
        this.type=podCastItem.getType();
        this.duration=podCastItem.getDuration();
        this.podcastUrl=podCastItem.getPodcastUrl();
    }
}
