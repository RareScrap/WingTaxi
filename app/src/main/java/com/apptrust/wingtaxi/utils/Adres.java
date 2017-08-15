package com.apptrust.wingtaxi.utils;

/**
 * Created by rares on 15.08.2017.
 */

public class Adres {
    public double longitude;
    public double latitude;
    public String textAdres;

    public Adres(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Adres(double longitude, double latitude, String textAdres) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.textAdres = textAdres;
    }

    @Override
    public String toString() {
        return textAdres;
    }
}
