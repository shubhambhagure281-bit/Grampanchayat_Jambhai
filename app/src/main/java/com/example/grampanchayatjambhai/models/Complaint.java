package com.example.grampanchayatjambhai.models;

public class Complaint {
    private String complaintId;
    private String userId;
    private String userName;
    private String userMobile;
    private String type;
    private String description;
    private String photoUrl;
    private String status; // default "प्रलंबित"
    private long createdAt;
    private long updatedAt;

    public Complaint() {
        // Required for Firestore deserialization
    }

    public Complaint(String complaintId, String userId, String userName, String userMobile, String type, String description, String photoUrl, String status, long createdAt, long updatedAt) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.userName = userName;
        this.userMobile = userMobile;
        this.type = type;
        this.description = description;
        this.photoUrl = photoUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}