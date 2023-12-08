package com.example.myapplication;

import java.net.URL;

public class NASAimage {

    long id;
    String dateText;
    String title;
    String explanation;
    String filename;
    String url;
    String hdurl;

    public NASAimage(long id, String title, String dateText, String filename, String explanation, String hdurl){
        this.id = id;
        this.title = title;
        this.dateText = dateText;
        this.filename = filename;
        this.explanation = explanation;
        this.hdurl = hdurl;
    }

    public long getId() { return id;}

    public void setId(int id) { this.id = id; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getDate() {
        return dateText;
    }

    public void setDate(String date) {
        this.dateText = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHDurl() {
        return hdurl;
    }

    public void setHDurl(String hdurl) {
        this.hdurl = hdurl;
    }
}
