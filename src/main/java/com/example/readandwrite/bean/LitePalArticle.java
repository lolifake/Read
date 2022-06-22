package com.example.readandwrite.bean;

import org.litepal.crud.LitePalSupport;

public class LitePalArticle extends LitePalSupport {
    private int id;
    private String title;//文章标题
    private String content;//文章内容
    private String author;//文章作者（发布者）

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
