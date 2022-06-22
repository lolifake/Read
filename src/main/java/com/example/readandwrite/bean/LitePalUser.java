package com.example.readandwrite.bean;

import org.litepal.crud.LitePalSupport;

public class LitePalUser extends LitePalSupport {
    private int id;
    private String username;
    private String imagePath;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImagePath() {
        return imagePath;
    }
}
