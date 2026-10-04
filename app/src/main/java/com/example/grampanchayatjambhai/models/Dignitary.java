package com.example.grampanchayatjambhai.models;

public class Dignitary {
    private String id;
    private String name;
    private String designation;
    private String designationMr;
    private String level; // STATE, DISTRICT, TALUKA, GRAM_PANCHAYAT
    private String district;
    private String taluka;
    private String gramPanchayat;
    private String photoUrl;
    private int photoResId;
    private String sourceUrl;
    private String sourceName;
    private long lastVerifiedAt;
    private String status;

    public Dignitary() {
    }

    public Dignitary(String id, String name, String designation, String level, String district, String taluka, String gramPanchayat, String photoUrl, int photoResId, String sourceUrl, String sourceName, long lastVerifiedAt, String status) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.designationMr = designation;
        this.level = level;
        this.district = district;
        this.taluka = taluka;
        this.gramPanchayat = gramPanchayat;
        this.photoUrl = photoUrl;
        this.photoResId = photoResId;
        this.sourceUrl = sourceUrl;
        this.sourceName = sourceName;
        this.lastVerifiedAt = lastVerifiedAt;
        this.status = status;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDesignationMr() {
        return designationMr;
    }

    public void setDesignationMr(String designationMr) {
        this.designationMr = designationMr;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public String getGramPanchayat() {
        return gramPanchayat;
    }

    public void setGramPanchayat(String gramPanchayat) {
        this.gramPanchayat = gramPanchayat;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getPhotoResId() {
        return photoResId;
    }

    public void setPhotoResId(int photoResId) {
        this.photoResId = photoResId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public long getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(long lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}