package com.rk.bts_busdriversapp.model;

import java.io.Serializable;

/*
    -Implements Serializable in order to pass Bus objects via Intents
 */

public class Bus implements Serializable {

    private String registrationPlate;
    private String line;

    private String lon;
    private String lat;

    private boolean isActive;

    public Bus() {
    }

    public Bus(String registrationPlate, String line, String lon, String lat, boolean isActive) {
        this.registrationPlate = registrationPlate;
        this.line = line;
        this.lon = lon;
        this.lat = lat;
        this.isActive = isActive;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
