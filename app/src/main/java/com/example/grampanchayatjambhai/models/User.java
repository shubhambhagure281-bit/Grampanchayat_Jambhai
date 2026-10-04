package com.example.grampanchayatjambhai.models;

public class User {
    private String uid;
    private String name;
    private String mobile;
    private String email;
    private String address;
    private String profileImage;
    private String role; // Default "user"
    private long createdAt;

    public User() {
        // Required for Firestore deserialization
    }

    public User(String uid, String name, String mobile, String email, String address, String profileImage, String role, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.profileImage = profileImage;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}