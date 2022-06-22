package com.example.readandwrite.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class BbArticle extends BmobObject {
    private String title; //文章标题
    private String content; //文章内容
    private String author; //文章作者（发布者）
    private BmobRelation collects;//收藏该文章的用户

    public BmobRelation getCollects() {
        return collects;
    }

    public void setCollects(BmobRelation collects) {
        this.collects = collects;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

