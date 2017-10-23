package com.apptrust.wingtaxi.utils;

import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.internal.PlaceEntity;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by rares on 15.08.2017.
 */

public class Adres {
    public double longitude;
    public double latitude;
    public String textAdres;
    //public String organisationName;
    public String placeID;

    public Adres(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Adres(double longitude, double latitude, String textAdres) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.textAdres = textAdres;
    }

    public static Adres initCoordsByPlaceID(Adres adres, Context context) {
        double lat = 0;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            lat = geocoder.getFromLocationName(adres.textAdres, 1).get(0).getLatitude();
            adres.longitude = geocoder.getFromLocationName(adres.textAdres, 1).get(0).getLatitude();
            adres.latitude = geocoder.getFromLocationName(adres.textAdres, 1).get(0).getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("asd", String.valueOf(lat));
        int a = 0;

        return adres;
    }

    public Adres(String textAdres, String placeID) {
        this.textAdres = textAdres;
        this.placeID = placeID;




        /*GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();


        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            LatLng queriedLocation = myPlace.getLatLng();
                            latitude = queriedLocation.latitude;
                            longitude = queriedLocation.longitude;
                        }
                        places.release();
                    }
                });*/
    }

    /*public Adres(double longitude, double latitude, String textAdres, String organisationName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.textAdres = textAdres;
        this.organisationName = organisationName;
    }*/

    @Override
    public String toString() {
        return textAdres;
    }
}
