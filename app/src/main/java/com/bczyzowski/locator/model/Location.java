package com.bczyzowski.locator.model;


import org.joda.time.LocalDateTime;

public class Location {

    private double latitude, longitude;
    private float accuracy;
    private LocalDateTime time;


    public Location(double latitude, double longitude, float accuracy, LocalDateTime time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                '}';
    }
}
