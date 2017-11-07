package com.apptrust.wingtaxi;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class SplashActivity extends AppCompatActivity
        implements DialogInterface.OnClickListener, LocationListener {
    public static boolean skip = false;

    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            if  (getIntent().getExtras().getBoolean("EXIT"))
                finish();
            return;
        }

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            boolean perm;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                perm = this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                perm = false;
            }


            if (perm) {
                lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        123);
            }
            return;
        } else {
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
        }

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (LoginActivity.mapReady) {
                    nextToMain();
                } else {
                    timer.cancel();
                    timer.purge();
                    Message message = mHandler.obtainMessage(1);
                    message.sendToTarget();
                }
            }

            // TODO: Обработать ситуацию, когда карта так и не была готова
        };
        timer.schedule(timerTask, 10000, 1000);
    }
    final Timer timer = new Timer();

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    nextToMain();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Мы не смогли найти вас", Toast.LENGTH_SHORT).show();
                    nextToMain();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void nextToMain() {
        timer.cancel();
        timer.purge();
        skip = true;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NO_HISTORY | FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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


    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(SplashActivity.this, "Проверьте подключение к сети", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    };

}
