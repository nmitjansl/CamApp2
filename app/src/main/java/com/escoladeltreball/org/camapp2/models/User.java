package com.escoladeltreball.org.camapp2.models;

/**
 * Created by iam47992649 on 2/1/18.
 */

public class User {
    private String name;
    private String email;
    private String uid;
    private String pass;

    public User() {

    }

    public User(String uid, String name, String email,  String pass) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.pass = pass;
    }
//GETTERS & SETTERS//

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
