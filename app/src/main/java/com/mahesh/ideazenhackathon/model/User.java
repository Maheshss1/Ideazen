package com.mahesh.ideazenhackathon.model;

import android.net.Uri;

public class User {
    private String uid;
    private String displayPic;
    private String name;
    private String email;
    private String phone;

    public User() {
    }

    public User(String uuid, String displayPic, String name, String email, String phone) {
        this.uid = uuid;
        this.displayPic = displayPic;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getUuid() {
        return uid;
    }

    public void setUuid(String uuid) {
        this.uid = uuid;
    }

    public String getDisplayPic() {
        return displayPic;
    }

    public void setDisplayPic(String displayPic) {
        this.displayPic = displayPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", displayPic=" + displayPic +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
