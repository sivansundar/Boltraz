package com.boltraz.Model;


public class ClassAnnouncementsModel {


    private String title;
    private String desc;
    private String postID;
    private String author;
    private String imgUrl;
    private String time;
    public String date;



    public ClassAnnouncementsModel() {

    }

    public ClassAnnouncementsModel(String title, String desc, String postID, String author, String imgUrl, String time, String date) {
        this.title = title;
        this.desc = desc;
        this.postID = postID;
        this.author = author;
        this.imgUrl = imgUrl;
        this.time = time;
        this.date = date;


    }

    public String getTime() {
        return time;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getauthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}