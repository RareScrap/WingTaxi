package com.apptrust.wingtaxi.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import com.apptrust.wingtaxi.JSInterfaces.GetSearchResultJSInterface;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

import java.util.ArrayList;

/**
 * Отображает форму добавления адреса вручную в виде диалогового окна
 * Created by RareScrap on 15.08.2017.
 */
public class AddAdresDialogFragment extends DialogFragment
    implements GetSearchResultJSInterface.GetSearchResult {
    private AppCompatButton backButton;
    private AppCompatButton nextButton;
    private EditText streetEditText;
    private EditText houseNumberEditText;
    /** Если true - фрагмент открыт из страницы настройка заказа. False, если
     * фрагмент открыт из яндекс карт */
    private boolean isAddmode;
    private WebView webView;

    private ArrayList<Adres> results = new ArrayList<>();

    /**
     * Фабричный конструктор
     * @param isAddMode Если true - фрагмент открыт из страницы настройка заказа. False, если
     *                  фрагмент открыт из яндекс карт
     * @return Экземпляр {@link AddAdresDialogFragment}
     */
    public static AddAdresDialogFragment newInstance(boolean isAddMode) {
        AddAdresDialogFragment fragment = new AddAdresDialogFragment();
        fragment.isAddmode = isAddMode;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View addAdresDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_set_address, null);
        builder.setView(addAdresDialogView); // Добавление GUI

        // Получение ссылки на элементы UI
        streetEditText = (EditText) addAdresDialogView.findViewById(R.id.street_edit_text);
        houseNumberEditText = (EditText) addAdresDialogView.findViewById(R.id.house_number_edit_text);
        backButton = (AppCompatButton) addAdresDialogView.findViewById(R.id.backButton);
        nextButton = (AppCompatButton) addAdresDialogView.findViewById(R.id.nextButton);
        webView = (WebView) addAdresDialogView.findViewById(R.id.web_view);

        // Установка слушателей на кнопки
        backButton.setOnClickListener(backButtonClickListener);
        nextButton.setOnClickListener(nextButtonClickListener);

        // Создание диалога
        Dialog dialog = builder.create();

        // Автоматически открывать клавиатуру для ввода на первое текстовое поле
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);

        // Инициализация JS-интерфейсов
        GetSearchResultJSInterface getSearchResultJSInterface = new GetSearchResultJSInterface(this);
        webView.addJavascriptInterface(getSearchResultJSInterface, "sendSearchResult");

        // Последние приготолеия
        webView.clearCache(true);
        webView.loadUrl("http://romhacking.pw/NEW_MAP10/map.html");

        // Возвращение диалогового окна
        return dialog;
    }

    View.OnClickListener backButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddAdresDialogFragment.this.dismiss();
        }
    };

    /**
     * Слушатель кнопки "далее", расположенной на вьюхе. Вызывает {@link OrderFragment}.
     */
    View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isAddmode) {
                // TODO: Добавить адрес в список
                AddAdresDialogFragment.this.dismiss();
            } else {
                FragmentTransaction fTrans = getFragmentManager().beginTransaction();

                // Иницилазация нового фрагмета
                OrderFragment orderFragment = OrderFragment.newInstance(new ArrayList<Adres>());
                fTrans.addToBackStack("OrderFragment");
                fTrans.replace(R.id.fragment_container, orderFragment);
                fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fTrans.commit();

                // Очистка ненужных более View
                // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
                ((ViewGroup) getActivity().findViewById(R.id.fragment_container)).removeAllViews();
            }
        }
    };

    @Override
    public void onGetSearchResult(String address, float longitude, float latitude) {
        results.add(new Adres(longitude, latitude, address));
    }

    @Override
    public void onGetResultCount(int resultCount) {

    }
}
