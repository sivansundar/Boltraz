package com.boltraz.Model;


public class ClassAnnouncementsModel {


    private String title;
    private String desc;
    private String postID;
    private String author;
    private String imgUrl;


    public ClassAnnouncementsModel() {

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


    public ClassAnnouncementsModel(String title, String desc, String postID, String author, String imgUrl) {
        this.title = title;
        this.desc = desc;
        this.postID = postID;
        this.author = author;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
