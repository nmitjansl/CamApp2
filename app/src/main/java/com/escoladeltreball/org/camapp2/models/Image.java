package com.escoladeltreball.org.camapp2.models;

/**
 * Created by iam47662285 on 2/8/18.
 */

public class Image {

    private String email;
    private String direccio;
    private String likes;

    public Image() {
    }

    public Image(String email, String direccio) {
        this.email = email;
        this.direccio = direccio;
        likes = String.valueOf(0);
    }

    public String getUid() {
        return email;
    }

    public void setUid(String uid) {
        this.email = uid;
    }

    public String getDireccio() {
        return direccio;
    }

    public void setDireccio(String direccio) {
        this.direccio = direccio;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}
