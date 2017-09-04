package com.apptrust.wingtaxi.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apptrust.wingtaxi.JSInterfaces.SendDataJSInterface;
import com.apptrust.wingtaxi.JSInterfaces.UpdateDataJSInterface;
import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;
import com.apptrust.wingtaxi.utils.DataProvider;
import com.apptrust.wingtaxi.utils.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment implements
        UpdateDataJSInterface.JSRequestUpdateData,
        SendDataJSInterface.JSRequestData {
    /** Адреса, которые будут переданы карте маршрутов */
    private ArrayList<Adres> adresses;

    private TextView minTextView;
    private TextView adressesTextView;
    private TextView calculationTextView;
    private TextView timeTextView;
    private TextView priceTextView;

    private WebView webView;

    private Button ok_button;

    private float price;
    private String preferedTime;
    private Handler toastHandler;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(ArrayList<Adres> adresses, String time) {
        SummaryFragment fragment = new SummaryFragment();
        fragment.adresses = adresses;
        fragment.preferedTime = time;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnedView = inflater.inflate(R.layout.fragment_summary, container, false);

        // Получение ссылок на элементы UI
        minTextView = (TextView) returnedView.findViewById(R.id.min);
        adressesTextView = (TextView) returnedView.findViewById(R.id.adresses);
        calculationTextView = (TextView) returnedView.findViewById(R.id.calculation);
        timeTextView = (TextView) returnedView.findViewById(R.id.time);
        priceTextView = (TextView) returnedView.findViewById(R.id.price);
        webView = (WebView) returnedView.findViewById(R.id.webView);
        ok_button = (Button) returnedView.findViewById(R.id.ok_button);

        ok_button.setOnClickListener(okClickListener);

        // Назначение теста элементам UI
        minTextView.setText(getResources().getString(
                R.string.min_tariff,
                MainActivity.dataProvider.minTariffKm,
                MainActivity.dataProvider.minTariffPrice,
                MainActivity.dataProvider.additionalPricePerKm
        ));

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(R.string.check_order); // Вывести в титульую строку название блюда
        ab.setSubtitle(getString(R.string.check_order_hint)); // Стереть подстроку

        // Текстовое представление всех адресов поездки
        String str = getString(R.string.following_addresses) + " ";
        for (int i = 0; i < adresses.size()-1; i++)
            str += adresses.get(i).textAdres + ",";
        str += adresses.get(adresses.size()-1).textAdres + ".";
        adressesTextView.setText(str);

        // Инициализация WebView
        webView = (WebView) returnedView.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }
        });

        // Инициализация JS-интерфейсов
        UpdateDataJSInterface updateDataJSInterface = new UpdateDataJSInterface(this);
        SendDataJSInterface sendDataJSInterface = new SendDataJSInterface(this);
        // TODO: Заменить название интерфейса в JS
        webView.addJavascriptInterface(updateDataJSInterface, "updateDataJSInterface");
        webView.addJavascriptInterface(sendDataJSInterface, "getDataJSInterface");

        // Последние приготолеия
        webView.clearCache(true);
        webView.loadUrl("http://romhacking.pw/route_map3/map.html");

        toastHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Toast.makeText(getContext(), "Заказ успешно отправлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Не удалось отправить заказ", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Вернуть View фрагмента
        return returnedView;
    }

    @Override
    public void onJSRequestUpdateAdres(double longitude, double latitude, String address) {
        // ПОТОМ
    }

    @Override
    public void onJSRequestUpdateRouteLength(final float length) {
        // TODO: Перенести вычисление стоимости на сервер
        float calclAdditionalKm = 0;
        if (length - MainActivity.dataProvider.minTariffKm*1000 > 0)
            calclAdditionalKm = length/1000 - MainActivity.dataProvider.minTariffKm;

        final float additionalKm = calclAdditionalKm;
        final float additionalPay = additionalKm * MainActivity.dataProvider.additionalPricePerKm;

        final float totalPrice = MainActivity.dataProvider.minTariffPrice + additionalPay;
        price = totalPrice;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                calculationTextView.setText(getResources().getString(
                        R.string.calculation,
                        length/1000,
                        MainActivity.dataProvider.minTariffKm,
                        additionalKm,
                        additionalPay
                ));
                priceTextView.setText(getResources().getString(R.string.price, totalPrice));
            }
        });
    }

    @Override
    public void onJSRequestUpdateTripTime(final int h, final int m) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (h > 0 && m > 0) {
                    timeTextView.setText(getResources().getString(R.string.time_hm, h, m));
                } else {
                    if (h > 0 && m == 0) {
                        timeTextView.setText(getResources().getString(R.string.time_h, h));
                    } else {
                        timeTextView.setText(getResources().getString(R.string.time_m, m));
                    }
                }
            }
        });
    }

    @Override
    public int onJSRequestRoutePointsNumber() {
        return adresses.size();
    }

    @Override
    public double onJSRequestRoutePointCoord(int pointIndex, int coordIndex) {
        if (pointIndex < 0 || pointIndex > adresses.size()) {
            Log.e("RoutePointCoord", "Error pointIndex");
            return -1;
        }

        switch (coordIndex) {
            case 0: {
                return adresses.get(pointIndex).longitude;
            }
            case 1: {
                return adresses.get(pointIndex).latitude;
            }
            default: {
                Log.e("RoutePointCoord", "Error coordIndex");
                return -1;

            }
        }
    }

    View.OnClickListener okClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            Calendar rightNow = Calendar.getInstance();
            int h = rightNow.get(Calendar.HOUR_OF_DAY);
            int m = rightNow.get(Calendar.MINUTE);
            Order order = new Order(adresses, h, m);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Log.i("GSON", gson.toJson(order));

            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyy-HH.mm.ss");
            String fileName = format.format(rightNow.getTime()) + ".json";

            try {
                //File file = new File(path, rightNow.toString() + ".json");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        getContext().openFileOutput(
                                fileName,
                                Context.MODE_PRIVATE
                        )
                );
                outputStreamWriter.write(gson.toJson(order));
                outputStreamWriter.close();
            }
             catch (IOException e) {
                e.printStackTrace();
            }


            // Запрос на отправку данных
            try {
                SendDataTask sendDataTask = new SendDataTask();
                sendDataTask.execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class SendDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject json = new JSONObject();

            JSONArray arr = new JSONArray();
            for (int i = 0; i < adresses.size(); i++) {
                arr.put(adresses.get(i).textAdres);
            }

            SharedPreferences sharedPref = getActivity().getSharedPreferences("phone", Context.MODE_PRIVATE);
            String phone = sharedPref.getString("phone", "AZAZA");

            try {
                json.put("addresses", arr);
                json.put("phoneNumber", phone);
                json.put("password", "nahui_idi_.!.");
                json.put("price", (int) price);
                json.put("date", preferedTime);
            } catch (JSONException e) {}



            String response = "";
            try {
                URL url = new URL("http://romhacking.pw:8081/makeorder");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Secret", "c8df37bef1275f33");


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(json.toString());

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Message message = toastHandler.obtainMessage(1);
                    message.sendToTarget();

                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    Message message = toastHandler.obtainMessage(0);
                    message.sendToTarget();
                    response="";

                }
            } catch (Exception e) {
                Message message = toastHandler.obtainMessage(0);
                message.sendToTarget();
                e.printStackTrace();
            }

            
            
            
            
        



            return null;
        }
    }
}
