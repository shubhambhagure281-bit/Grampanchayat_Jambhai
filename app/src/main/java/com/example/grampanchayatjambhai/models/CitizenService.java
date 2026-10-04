package com.example.grampanchayatjambhai.models;

public class CitizenService {
    private String id;
    private String title;
    private String description;
    private int iconRes;
    private String category; // CERTIFICATE or COMPLAINT

    public CitizenService() {
    }

    public CitizenService(String id, String title, String description, int iconRes, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
        this.category = category;
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

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}