package com.apptrust.wingtaxi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.apptrust.wingtaxi.JSInterfaces.MapReadyJSInterface;
import com.apptrust.wingtaxi.JSInterfaces.UpdateDataJSInterface;
import com.apptrust.wingtaxi.JSInterfaces.GPSRequireJSInterface;
import com.apptrust.wingtaxi.LoginActivity;
import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

/**
 * Основной фрагмент приложения. Представляет собой {@link WebView} с яндекс картой, лайаутом
 * кнопки подтверждения адреса и строки адреса. Этот фрагмент вызывается в самом начале приложения
 * и может быть вызван в любом другом месте, когда требуется получить от пользователя новое место
 * на карте - например, в {@link OrderFragment} при выборе дополнительного адреса
 */
public class MainFragment extends Fragment implements
        UpdateDataJSInterface.JSRequestUpdateData,
        MapReadyJSInterface.MapReady {
    /** Котейнер Yandex карты */
    public WebView webView;
    /** Текстовое представление адреса, который пользователь выбирает на карте*/
    public TextView textView;
    /** Кнопка подтверждения выбора начального адреса */
    private Button mButton;
    /** Адрес, который выбрал пользователь*/
    private Adres selectedAddress;

    /** Ссылка на фрагмент {@link OrderFragment}. Если null, то {@link #buttonClickListener}
     * создаст новый фрагмент {@link OrderFragment}. Если != null, то {@link #selectedAddress}
     * Добавляется в {@link OrderFragment#adreses}, вместо создания нового фрагмента*/
    private OrderFragment orderFragmentLink;

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параментров
     * @return Новый объект фрагмента {@link MainFragment}.
     */
    public static MainFragment newInstance(OrderFragment orderFragmentLink) {
        MainFragment fragment = new MainFragment();
        fragment.orderFragmentLink = orderFragmentLink;
        return fragment;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * Инициализация UI фрагмента. Также инииализирует {@link #webView} и вставляет в него
     * JS интерфейсы.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnedView = inflater.inflate(R.layout.fragment_main, container, false);

        // Получение ссылок на элементы UI
        textView = (TextView) returnedView.findViewById(R.id.textView);
        mButton = (Button) returnedView.findViewById(R.id.button);
        mButton.setOnClickListener(buttonClickListener);

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        if (orderFragmentLink == null) {
            ab.setTitle(R.string.app_name); // Вывести в титульую строку название блюда
            ab.setSubtitle(R.string.main_fragment_title);
        } else {
            ab.setTitle(R.string.main_fragment_title_add_address_mode); // Вывести в титульую строку название блюда
            ab.setSubtitle(""); // Стереть подстроку
        }

        // Инициализация WebView
        webView = (WebView) returnedView.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);

        // TODO: Удалить при чистке проекта
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        // Инициализация JS-интерфейсов
        GPSRequireJSInterface gpsRequireJSInterface = new GPSRequireJSInterface(getActivity());
        UpdateDataJSInterface updateDataJSInterface = new UpdateDataJSInterface(this);
        MapReadyJSInterface mapReadyJSInterface = new MapReadyJSInterface(this);
        // TODO: Заменить название интерфейса в JS
        webView.addJavascriptInterface(gpsRequireJSInterface, "gpsJavaScriptInterface");
        webView.addJavascriptInterface(updateDataJSInterface, "updateDataJSInterface");
        webView.addJavascriptInterface(mapReadyJSInterface, "mapReadyJSInterface");

        // Последние приготолеия
        webView.clearCache(true);
        webView.loadUrl("http://romhacking.pw/test_map/map.html");

        // Вернуть View фрагмента
        return returnedView;
    }

    /**
     * Слушатель для кнопки подтверждения выбора начального адреса
     */
    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        /**
         * Откывает фрагмента {@link OrderFragment} при клике
         * @param v {@link View} кнопки, по которой был сделан клик
         */
        @Override
        public void onClick(View v) {
            if (orderFragmentLink != null) {
                // Добавляем адрес в список
                orderFragmentLink.adreses.add(selectedAddress);

                // Вовзращаемся назад
                getFragmentManager().popBackStackImmediate();
            } else {
                FragmentTransaction fTrans = getFragmentManager().beginTransaction();

                // Иницилазация нового фрагмета
                OrderFragment orderFragment = OrderFragment.newInstance(selectedAddress);
                fTrans.addToBackStack(null);
                fTrans.replace(R.id.fragment_container, orderFragment);
                fTrans.commit();

                // Очистка ненужных более View
                // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
                ( (ViewGroup) getActivity().findViewById(R.id.fragment_container) ).removeAllViews();
            }
        }
    };

    @Override
    public void onMapReady() {
        // Для отладки диалога загрузки в LoginFragment
        //SystemClock.sleep(15000);

        // Сообщаем LoginActivity, что карта голова к запуску
        LoginActivity.mapReady = true;
    }

    @Override
    public void onJSRequestUpdateAdres(double longitude, double latitude, final String address) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(address);
            }
        });
        this.selectedAddress = new Adres(longitude, latitude, address);
    }

    @Override
    public void onJSRequestUpdateRouteLength(float length) {
        // Нас не интересует маршрут. Ничего не делаем
    }
}
