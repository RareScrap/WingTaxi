package com.apptrust.wingtaxi.JSInterfaces;

import android.webkit.JavascriptInterface;

/**
 * Created by RareScrap on 22.08.2017.
 */

public class SendDataJSInterface {
    public interface JSRequestData {
        /* К сожалению, JS не понимает ни типа float[][], ни double[][]. Можно передавать массив
        через JSON в виде строки и парсить через JS. Применимо только если знаешь JS. */
        //double[][] onJSRequestRoutePoints();
        int onJSRequestRoutePointsNumber();
        double onJSRequestRoutePointCoord(int pointIndex, int coordIndex);
    }

    /** Объект реализации интерфейса. Приходит из вне */
    public JSRequestData jsDataProvider;

    /**
     * Конструктор, инициализирующий свои поля
     * @param jsDataProvider Объект, реализующий интерфейсы {@link JSRequestData}
     */
    public SendDataJSInterface(JSRequestData jsDataProvider) {
        this.jsDataProvider = jsDataProvider;
    }

    /*@JavascriptInterface
    public double[][] getRoutePoints() {
        return jsDataProvider.onJSRequestRoutePoints();
    }*/

    @JavascriptInterface
    public int getRoutePointsNumber() {
        return jsDataProvider.onJSRequestRoutePointsNumber();
    }

    @JavascriptInterface
    public double getRoutePointCoord(int pointIndex, int coordIndex) {
        return jsDataProvider.onJSRequestRoutePointCoord(pointIndex, coordIndex);
    }
}
