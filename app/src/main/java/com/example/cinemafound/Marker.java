package com.example.cinemafound;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Marker implements Serializable {
    private int id;
    private String name;
    private float rate;
    private float latitude;
    private float longitude;
    private Bitmap photo;

    public Marker(int id, String name, float rate, float latitude, float longitude, Bitmap photo){
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getRate() {
        return rate;
    }

    public String getName() {
        return name;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
