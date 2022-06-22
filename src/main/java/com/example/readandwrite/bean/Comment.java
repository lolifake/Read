package com.example.readandwrite.bean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {
    private String content;
    private User user;
    private BbArticle article;

    public void setContent(String content) {
        this.content = content;
    }

    public void setArticle(BbArticle article) {
        this.article = article;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public BbArticle getArticle() {
        return article;
    }
}
