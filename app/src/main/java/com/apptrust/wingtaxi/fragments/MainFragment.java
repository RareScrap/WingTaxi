package com.apptrust.wingtaxi.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptrust.wingtaxi.JSInterfaces.MapReadyJSInterface;
import com.apptrust.wingtaxi.JSInterfaces.UpdateDataJSInterface;
import com.apptrust.wingtaxi.JSInterfaces.GPSRequireJSInterface;
import com.apptrust.wingtaxi.LoginActivity;
import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

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
    private AppCompatButton mButton;
    /** Кнопка определения местоположения */
    private AppCompatButton GPSButton;
    /** Кнопка, вызывающая диалоговое окно {@link AddAdresDialogFragment} дял выбора
     * адреса вручную */
    private AppCompatButton setAddressButton;
    /** Адрес, который выбрал пользователь */
    private Adres selectedAddress;

    /** Ссылка на фрагмент {@link OrderFragment}. Если null, то {@link #buttonClickListener}
     * создаст новый фрагмент {@link OrderFragment}. Если != null, то {@link #selectedAddress}
     * Добавляется в {@link OrderFragment#adreses}, вместо создания нового фрагмента */
    private OrderFragment orderFragmentLink;
    /** {@link LocationManager} для работы с GPS */
    private LocationManager locationManager;


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
        mButton = (AppCompatButton) returnedView.findViewById(R.id.button);
        setAddressButton = (AppCompatButton) returnedView.findViewById(R.id.set_address);
        GPSButton = (AppCompatButton) returnedView.findViewById(R.id.gps_button);

        // Установка слушателей
        mButton.setOnClickListener(buttonClickListener);
        setAddressButton.setOnClickListener(addAddressClicklistener);
        GPSButton.setOnClickListener(GPSButtonClickListener);

        // Инициализация GPS
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        if (orderFragmentLink == null) {
            ab.setTitle(getString(R.string.first_step_title));
            ab.setSubtitle("");
        } else {
            ab.setTitle(R.string.main_fragment_title_add_address_mode); // Вывести в титульую строку название блюда
            ab.setSubtitle(""); // Стереть подстроку
            mButton.setText("Готово!");
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
        webView.loadUrl("http://romhacking.pw/NEW_MAP11/map.html");

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
            if (textView.getText().length() == 0)
                return;

            if (orderFragmentLink != null) {
                // Добавляем адрес в список
                orderFragmentLink.adreses.add(selectedAddress);

                // Вовзращаемся назад
                getFragmentManager().popBackStackImmediate();
            } else {
                FragmentTransaction fTrans = getFragmentManager().beginTransaction();

                // ТЕСТОВЫЙ СПИСОК!
                ArrayList<Adres> addresses = new ArrayList<>();
                addresses.add(selectedAddress);
                addresses.add(selectedAddress);

                // Иницилазация нового фрагмета
                AddAddressFragment addAddressFragment = AddAddressFragment.newInstance(false, addresses);
                fTrans.addToBackStack("AddAddressFragment");
                fTrans.replace(R.id.fragment_container, addAddressFragment);
                fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fTrans.commit();

                // Очистка ненужных более View
                // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
                ((ViewGroup) getActivity().findViewById(R.id.fragment_container)).removeAllViews();
            }
        }
    };

    /**
     * Слушатель кликов по кнопке {@link #setAddressButton}
     */
    View.OnClickListener addAddressClicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddAdresDialogFragment addAdresDialogFragment = AddAdresDialogFragment.newInstance(false);
            addAdresDialogFragment.show(getFragmentManager(), "AddAdresDialogFragment");
        }
    };

    /**
     * Слушатель для кнопки определения местоположения
     */
    View.OnClickListener GPSButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                // Запрашиваем координаты (без NETWORK_PROVIDER не вызывается слушатель)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000 * 10, 10, locationListener);
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                        locationListener);
            }
        }
    };

    /**
     * Слушатель для событий GPS датчика
     */
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getActivity(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            // Отображение местонахождения на карте
            webView.loadUrl("javascript:function_two(" + location.getLatitude() + "," + location.getLongitude() + ")");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getActivity(), "onStatusChanged:", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "onProviderEnabled", Toast.LENGTH_SHORT).show();
            //showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "onProviderDisabled", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onJSRequestUpdateTripTime(int h, int m) {
        // Нас не интересует время пути. Ничего не делаем
    }
}
