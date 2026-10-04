package com.example.grampanchayatjambhai.models;

public class Member {
    private String id;
    private String name;
    private String position;
    private String photoUrl;
    private String phone;

    public Member() {
        // Required for Firestore deserialization
    }

    public Member(String id, String name, String position, String photoUrl, String phone) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.photoUrl = photoUrl;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}