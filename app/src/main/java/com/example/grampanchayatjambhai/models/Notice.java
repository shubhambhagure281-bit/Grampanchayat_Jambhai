package com.example.grampanchayatjambhai.models;

import android.text.TextUtils;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class Notice {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private boolean important;
    private long createdAt;

    public Notice() {
        // Required for Firestore deserialization
    }

    public Notice(String id, String title, String description, String imageUrl, boolean important, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.important = important;
        this.createdAt = createdAt;
    }

    public static Notice parseNotice(DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return null;
        try {
            Notice notice = doc.toObject(Notice.class);
            if (notice != null) {
                if (TextUtils.isEmpty(notice.getId())) {
                    notice.setId(doc.getId());
                }
                return notice;
            }
        } catch (Exception ignored) {
            // Safe fallback to manual field parsing if web saved Timestamp or String
        }

        try {
            Notice notice = new Notice();
            notice.setId(doc.getId());

            String t = doc.getString("title");
            notice.setTitle(t != null ? t : "ग्रामपंचायत सूचना");

            String d = doc.getString("description");
            notice.setDescription(d != null ? d : "");

            String img = doc.getString("imageUrl");
            if (TextUtils.isEmpty(img)) {
                img = doc.getString("image");
            }
            notice.setImageUrl(img != null ? img : "");

            Object impObj = doc.get("important");
            if (impObj instanceof Boolean) {
                notice.setImportant((Boolean) impObj);
            } else if (impObj instanceof String) {
                notice.setImportant("true".equalsIgnoreCase((String) impObj));
            } else {
                notice.setImportant(false);
            }

            Object createdObj = doc.get("createdAt");
            if (createdObj instanceof Long) {
                notice.setCreatedAt((Long) createdObj);
            } else if (createdObj instanceof Timestamp) {
                notice.setCreatedAt(((Timestamp) createdObj).getSeconds() * 1000);
            } else if (createdObj instanceof Double) {
                notice.setCreatedAt(((Double) createdObj).longValue());
            } else if (createdObj instanceof String) {
                try {
                    notice.setCreatedAt(Long.parseLong((String) createdObj));
                } catch (Exception e) {
                    notice.setCreatedAt(System.currentTimeMillis());
                }
            } else {
                notice.setCreatedAt(System.currentTimeMillis());
            }

            return notice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}