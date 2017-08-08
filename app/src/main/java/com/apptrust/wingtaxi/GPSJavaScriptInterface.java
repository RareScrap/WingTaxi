package com.apptrust.wingtaxi;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

/**
 * Created by rares on 08.08.2017.
 */

public class GPSJavaScriptInterface {
    private Activity activity;

    public GPSJavaScriptInterface(Activity activiy) {
        this.activity = activiy;
    }

    @JavascriptInterface
    public String getUserLocation(){
        return "PIDOR!";
    }
}
