package com.apptrust.wingtaxi;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class SplashActivity extends AppCompatActivity
        implements DialogInterface.OnClickListener, LocationListener {

    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.gps_off);
            adb.setMessage(R.string.gps_off_message);

            adb.setPositiveButton(R.string.turn_on_gps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(i, 1);
                }
            });
            adb.setNeutralButton(R.string.continue_without_gps, this);

            adb.create().show();
        } else startDetecting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (lm == null)
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            nextToMain();
        else startDetecting();
    }

    public void startDetecting() {
        if (lm == null)
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
    }

    public void nextToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent); // Показать активити логина
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        nextToMain();
    }

    @Override
    public void onLocationChanged(Location location) {
        nextToMain();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}
