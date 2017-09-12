package com.aioplayer.dao;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by akankshadhanda on 28/07/17.
 */

public class FeedResponse {
    private String title;
    private String id;
    private String copyright;
    private String country;
    private String icon;
    private String updated;
    private HashMap<String,String> author;
    private LinkedList<HashMap<String,String>> links;
    private LinkedList<RssFeedItem> results;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public HashMap<String, String> getAuthor() {
        return author;
    }

    public void setAuthor(HashMap<String, String> author) {
        this.author = author;
    }

    public LinkedList<HashMap<String, String>> getLinks() {
        return links;
    }

    public void setLinks(LinkedList<HashMap<String, String>> links) {
        this.links = links;
    }

    public LinkedList<RssFeedItem> getResults() {
        return results;
    }

    public void setResults(LinkedList<RssFeedItem> results) {
        this.results = results;
    }
}
