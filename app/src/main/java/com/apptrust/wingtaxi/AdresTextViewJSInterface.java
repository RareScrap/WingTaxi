package com.apptrust.wingtaxi;

import android.webkit.JavascriptInterface;
import android.widget.TextView;

/**
 * Created by rares on 09.08.2017.
 */

public class AdresTextViewJSInterface {
    private TextView adres;

    public AdresTextViewJSInterface(TextView textView) {
        this.adres = textView;
    }

    @JavascriptInterface
    public void updateAdresView(String adres) {
        this.adres.setText(adres);
    }
}
