package com.lucky.loadmorerecyclerview.model;

public class ContentsModel {
    private String title;
    private String contents;
    private String imageUrl;

    public ContentsModel(String title, String contents, String imageUrl) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
