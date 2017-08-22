package com.apptrust.wingtaxi.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.apptrust.wingtaxi.JSInterfaces.UpdateDataJSInterface;
import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment implements
        UpdateDataJSInterface.JSRequestUpdateData {
    /** Адреса, которые будут переданы карте маршрутов */
    private ArrayList<Adres> adresses;

    private TextView minTextView;
    private TextView adressesTextView;
    private TextView calculationTextView;
    private TextView timeTextView;
    private TextView priceTextView;

    private WebView webView;

    private Button ok_button;


    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(ArrayList<Adres> adresses) {
        SummaryFragment fragment = new SummaryFragment();
        fragment.adresses = adresses;
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

        // Инициализация JS-интерфейсов
        UpdateDataJSInterface updateDataJSInterface = new UpdateDataJSInterface(this);
        // TODO: Заменить название интерфейса в JS
        webView.addJavascriptInterface(updateDataJSInterface, "updateDataJSInterface");

        // Последние приготолеия
        webView.clearCache(true);
        webView.loadUrl("http://romhacking.pw/route_map2/map.html");

        // Вернуть View фрагмента
        return returnedView;
    }

    @Override
    public void onJSRequestUpdateAdres(double longitude, double latitude, String address) {
        // ПОТОМ
    }

    @Override
    public void onJSRequestUpdateRouteLength(float length) {
        final float newLength = length;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                priceTextView.setText(getResources().getString(R.string.cash, newLength));
            }
        });
    }
}
