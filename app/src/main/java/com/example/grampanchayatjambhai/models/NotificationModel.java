package com.example.grampanchayatjambhai.models;

public class NotificationModel {
    private String notificationId;
    private String userId;
    private String title;
    private String body;
    private String type;
    private String targetId;
    private boolean read;
    private long createdAt;

    public NotificationModel() {
        // Required for Firestore deserialization
    }

    public NotificationModel(String notificationId, String userId, String title, String body, String type, String targetId, boolean read, long createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.targetId = targetId;
        this.read = read;
        this.createdAt = createdAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}