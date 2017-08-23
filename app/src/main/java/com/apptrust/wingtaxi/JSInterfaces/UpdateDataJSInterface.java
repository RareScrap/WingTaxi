package com.apptrust.wingtaxi.JSInterfaces;

import android.webkit.JavascriptInterface;

/**
 * Извещает объект, реализующий интерфейсы {@link JSRequestUpdateData} о
 * том, что необходимо обновить данные приложения.
 */
public class UpdateDataJSInterface {
    public interface JSRequestUpdateData {
        void onJSRequestUpdateAdres(double longitude, double latitude, String address);
        void onJSRequestUpdateRouteLength(float length);
        void onJSRequestUpdateTripTime(int h, int m);
    }

    /** Объект реализации интерфейса. Приходит из вне */
    public JSRequestUpdateData jsDataUpdater;

    /**
     * Конструктор, инициализирующий свои поля
     * @param jsRequestChanges Объект, реализующий интерфейсы {@link JSRequestUpdateData}
     */
    public UpdateDataJSInterface(JSRequestUpdateData jsRequestChanges) {
        this.jsDataUpdater = jsRequestChanges;
    }

    @JavascriptInterface
    public void updateAddress(double longitude, double latitude, String address) {
        jsDataUpdater.onJSRequestUpdateAdres(longitude, latitude, address);
    }

    @JavascriptInterface
    public void updateRouteLength(float length) {
        jsDataUpdater.onJSRequestUpdateRouteLength(length);
    }

    @JavascriptInterface
    public void updateTripTime(int h, int m) {
        jsDataUpdater.onJSRequestUpdateTripTime(h, m);
    }
}
