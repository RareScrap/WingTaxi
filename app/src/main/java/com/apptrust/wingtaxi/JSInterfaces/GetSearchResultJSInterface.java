package com.apptrust.wingtaxi.JSInterfaces;

import android.webkit.JavascriptInterface;

/**
 * Интерфейс, обеспечивающий получения результатов поиска с Яндекс карт
 * @author RareScrap
 */
public class GetSearchResultJSInterface {
    public interface GetSearchResult {
        void onGetSearchResult(String address, float longitude, float latitude);
        void onGetResultCount(int resultCount);
    }

    /** Объект реализации интерфейса {@link GetSearchResult}. Приходит из вне */
    private GetSearchResult getSearchResult;

    /**
     * Конструктор, инициализирующий свои поля
     * @param mapReady Объект реализации интерфейса {@link GetSearchResult}. Приходит из вне
     */
    public GetSearchResultJSInterface(GetSearchResult mapReady) {
        this.getSearchResult = mapReady;
    }

    /**
     * Метод, вызывающийся из JS.Возвращает приложение один из результатов поиска.
     * @param address Строковое представление адреса/организации
     * @param longitude Долгота результата поиска
     * @param latitude широта результата поиска
     */
    @JavascriptInterface
    public void addSearchResult(String address, float longitude, float latitude) {
        getSearchResult.onGetSearchResult(address, longitude, latitude);
    }

    /**
     * Метод, вызывающийся из JS. Возвращает количество доступных результатов поиска.
     */
    @JavascriptInterface
    public void setResultCount(int resultCount) {
        getSearchResult.onGetResultCount(resultCount);
    }
}
