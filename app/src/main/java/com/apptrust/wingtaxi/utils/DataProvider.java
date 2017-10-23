package com.apptrust.wingtaxi.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Класс, занимающийся поставкой данных (JSON)
 * @author RareScrap
 */
public class DataProvider {
    /** Вызывается, когда данные скачаны и распарсены */
    public interface DataReady {
        void onDataReady();
        void onDownloadError();
    }

    /** Максимальное время ожидания данных */
    private static int CONNECTION_TIMEOUT = 5000;

    /** Адрес, откуда будет скачан JSON с данными */
    public URL jsonURL;
    /** Хранилище для загруженных данных в формате JSON */
    public JSONObject downloadedJSON = null;

    /** Минимальное расстояние минималки */
    public int minTariffKm;
    /** Цена минималки */
    public float minTariffPrice;
    /** Цена за каждый километр сверх минималки */
    public float additionalPricePerKm;

    /** Объект реализации интерфейса {@link DataReady}. Приходит из вне */
    public DataReady dataReady;

    /**
     * Конструктор, инициализирующий свои поля
     * @param dataReady Реализация интерфейса, метод которого вызывается, когда данные распарсены
     *                  и готовы к работе
     * @param url Адрес, откуда будет скачан JSON с данными
     */
    public DataProvider(DataReady dataReady, URL url) {
        this.dataReady = dataReady;
        this.jsonURL = url;
    }

    /**
     * Запускает загрузку данных в виде JSON
     */
    public void startDownloadData() {
        // Запрос на получение данных
        try {
            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute(jsonURL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Внутренний класс {@link AsyncTask} для загрузки данных в формате JSON.
     * @author RareScrap
     */
    private class GetDataTask extends AsyncTask<URL, Void, JSONObject> {
        /**
         * Получение данных из сети
         * @param params URL для получения JSON файла
         * @return JSON файл с категориями меню и блюдами в них
         */
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection(); // Для выдачи запроса достаточно открыть объект подключения
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                int response = connection.getResponseCode(); // Получить код ответа от веб-сервера

                //response = 404; // Это тест при недоступности сети

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }else {} // TODO: Реализовать поведение при недоступности сети
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // Закрыть HttpURLConnection
            }

            return null;
        }

        /**
         * Обработка ответа JSON и обновление ListView/GridView.
         * @param jsonObject JSON файл полученный после завершения работы doInBackground()
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                downloadedJSON = jsonObject; // Сохранение загруженного файла
                parseJSON(jsonObject);
            } else { // Информировать в случае, если данные не дошли
                dataReady.onDownloadError();
            }
        }
    }

    private void parseJSON(JSONObject jsonObject) {
        // Стирание старых данных
        minTariffKm = 0;
        minTariffPrice = 0;
        additionalPricePerKm = 0;

        try {
            minTariffKm = jsonObject.getInt("min_tariff_km");
            minTariffPrice = (float) jsonObject.getDouble("min_tariff_price");
            additionalPricePerKm = (float) jsonObject.getDouble("additional_price_per_km");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        // Инормировать, что данные готовы к использованию
        dataReady.onDataReady();
    }
}
