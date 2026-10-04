package com.example.grampanchayatjambhai.models;

public class GovernmentScheme {
    private String id;
    private String name;
    private String category;
    private String department;
    private String shortDescription;
    private String eligibility;
    private String documents;
    private String applicationProcess;
    private String officialWebsite;
    private int iconResId;

    public GovernmentScheme() {
    }

    public GovernmentScheme(String id, String name, String category, String department, String shortDescription, String eligibility, String documents, String applicationProcess, String officialWebsite, int iconResId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.department = department;
        this.shortDescription = shortDescription;
        this.eligibility = eligibility;
        this.documents = documents;
        this.applicationProcess = applicationProcess;
        this.officialWebsite = officialWebsite;
        this.iconResId = iconResId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public String getApplicationProcess() {
        return applicationProcess;
    }

    public void setApplicationProcess(String applicationProcess) {
        this.applicationProcess = applicationProcess;
    }

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(String officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}