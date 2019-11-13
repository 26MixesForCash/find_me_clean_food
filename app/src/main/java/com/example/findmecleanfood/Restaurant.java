package com.example.findmecleanfood;

import java.io.Serializable;

// Restaurant class

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postCode;
    private String ratingValue;
    private String ratingDate;
    private String distance;
    private String latitude;
    private String longitude;

    public Restaurant (String id, String name, String addressLine1, String addressLine2,
                       String addressLine3, String postCode, String ratingValue,
                       String ratingDate, String distance, String latitude, String longitude) {

        this.id=id;
        this.name=name;
        this.addressLine1=addressLine1;
        this.addressLine2=addressLine2;
        this.addressLine3=addressLine3;
        this.postCode=postCode;
        this.ratingValue=ratingValue;
        this.ratingDate=ratingDate;
        this.distance=distance;
        this.latitude=latitude;
        this.longitude=longitude;
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

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(String ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
