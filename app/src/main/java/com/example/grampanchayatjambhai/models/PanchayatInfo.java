package com.example.grampanchayatjambhai.models;

public class PanchayatInfo {
    private String panchayatName;
    private String village;
    private String taluka;
    private String district;
    private String establishedYear;
    private String officeHours;
    private String officeAddress;
    private String contactPhone;
    private String contactEmail;

    public PanchayatInfo() {
        // Required for Firestore deserialization
    }

    public PanchayatInfo(String panchayatName, String village, String taluka, String district, String establishedYear, String officeHours, String officeAddress, String contactPhone, String contactEmail) {
        this.panchayatName = panchayatName;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.establishedYear = establishedYear;
        this.officeHours = officeHours;
        this.officeAddress = officeAddress;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public String getPanchayatName() {
        return panchayatName;
    }

    public void setPanchayatName(String panchayatName) {
        this.panchayatName = panchayatName;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(String establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}