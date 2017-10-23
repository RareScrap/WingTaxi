package com.apptrust.wingtaxi.JSInterfaces;

import android.webkit.JavascriptInterface;

/**
 * Извещает объект, реализующий интерфейс {@link MapReady} о
 * том, что карта на странице была загружена
 */
public class MapReadyJSInterface {
    public interface MapReady {
        void onMapReady();
    }

    /** Объект реализации интерфейса {@link MapReady}. Приходит из вне */
    private MapReady mapReady;

    /**
     * Конструктор, инициализирующий свои поля
     * @param mapReady Объект реализации интерфейса {@link MapReady}. Приходит из вне
     */
    public MapReadyJSInterface(MapReady mapReady) {
        this.mapReady = mapReady;
    }

    /**
     * Метод, вызывающийся из JS. Уведомляет о том, что карта загружена и готова к использованию.
     */
    @JavascriptInterface
    public void mapReady() {
        mapReady.onMapReady();
    }
}
