package com.example.readandwrite.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobUser {
    private BmobRelation collectArticle;//用户收藏的文章
    private String imagePath;//用户头像

    public BmobRelation getCollectArticle() {
        return collectArticle;
    }

    public void setCollectArticle(BmobRelation collectArticle) {
        this.collectArticle = collectArticle;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}

