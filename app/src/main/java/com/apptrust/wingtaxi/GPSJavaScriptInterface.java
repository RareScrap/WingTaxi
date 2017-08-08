package com.apptrust.wingtaxi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import im.delight.android.location.SimpleLocation;

/**
 * Created by rares on 08.08.2017.
 */

public class GPSJavaScriptInterface {
    // TODO: Или лучше AppCompatActivity?
    private Activity activity;
    public static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    public GPSJavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }

    @JavascriptInterface
    public String getUserLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(activity);

                // Назначить сообщение AlertDialog
                builder.setMessage(R.string.gps_permission_explanation);

                // Добавить кнопку OK в диалоговое окно
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Запросить разрешение
                                ActivityCompat.requestPermissions(activity, new String[]{ // Нужен еще параметр целевой активити, но скорее всего эта форма метода для текущего активити.
                                                Manifest.permission.ACCESS_FINE_LOCATION},
                                        ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE); //TODO: В доках написано что должен быть еще один первый арг - целеое активити. Разобраться почему работает без него
                            }
                        }
                );

                // Отображение диалогового окна
                builder.create().show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            SimpleLocation location = new SimpleLocation(activity);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Toast.makeText(activity, String.valueOf(longitude) + "_" + String.valueOf(latitude), Toast.LENGTH_SHORT).show();
                return String.valueOf(longitude) + "_" + String.valueOf(latitude);
            } else {
                return "PIDOR!";
            }
        }

        /* Нужно для примерного определения положения. Пока нахуй не надо
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/
        return "PIDOR!";
    }
}
