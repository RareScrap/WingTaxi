package com.apptrust.wingtaxi.JSInterfaces;

import android.webkit.JavascriptInterface;

/**
 * Извещает объект, реализующий интерфейсы {@link JSRequestUpdateData} о
 * том, что необходимо обновить данные приложения.
 */
public class UpdateDataJSInterface {
    public interface JSRequestUpdateData {
        void onJSRequestUpdateAdres(String newAdres);
    }

    /** Объект реализации интерфейса. Приходит из вне */
    public JSRequestUpdateData jsRequestChanges;

    /**
     * Конструктор, инициализирующий свои поля
     * @param jsRequestChanges Объект, реализующий интерфейсы {@link JSRequestUpdateData}
     */
    public UpdateDataJSInterface(JSRequestUpdateData jsRequestChanges) {
        this.jsRequestChanges = jsRequestChanges;
    }

    // TODO: Изменить название метода
    @JavascriptInterface
    public void updateAdresView(String newAdres) {
        jsRequestChanges.onJSRequestUpdateAdres(newAdres);
    }
}
