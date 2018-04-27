package com.example.kunal.blogapp;

public class Posts {

    String postURL;
    String date;
    String name;
    String uid;
    String time;
    String profileURL;
    String title;
    String description;

    public Posts() {
    }

    public Posts(String postURL, String date, String name, String uid, String profileURL, String title, String description) {
        this.postURL = postURL;
        this.date = date;
        this.name = name;
        this.uid = uid;
        this.profileURL = profileURL;
        this.title = title;
        this.description = description;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setPostURL(String postURL) {
        this.postURL = postURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
