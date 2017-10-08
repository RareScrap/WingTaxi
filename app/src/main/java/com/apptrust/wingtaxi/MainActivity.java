package com.apptrust.wingtaxi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.apptrust.wingtaxi.JSInterfaces.GPSRequireJSInterface;
import com.apptrust.wingtaxi.fragments.HistoryFragment;
import com.apptrust.wingtaxi.fragments.MainFragment;
import com.apptrust.wingtaxi.fragments.OrderFragment;
import com.apptrust.wingtaxi.utils.DataProvider;

import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DataProvider.DataReady {

    /** Хранилище загруженных из сети данных в виде готовых для работы объектов */
    public static DataProvider dataProvider;
    /** Адрес сервера */
    private static final String DATA_URL = "http://romhacking.pw/price_list.json";
    /** Номер телефона пользователя */
    //public static String phoneNumber;

    /**
     * Заполняет UI и инициализирует компоненты активити: NavDraver, Draverlayout. После иницализации
     * отображает первый фрагмент - {@link MainFragment}. Если приложение запускается впервые
     * (когда номер телефона не указан) - вызывается {@link LoginActivity}.
     * @param savedInstanceState Сохраненное состояние фрагмента. Null, если фрагмент запущен впервые.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Тестовый сценарий
        //phoneNumber = "111";

        // Запуск инициализация MainActivity
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Инициализация контейнера бокового меню
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Инициализация содержимого бокового меню
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Показываем первый фрагмент
        MainFragment mainFragment = MainFragment.newInstance(null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment);
        //transaction.addToBackStack(null);
        transaction.commit();

        verifyChech();



        // Подготавливаем DataProvider для загрузки данных
        if (dataProvider == null) {
            try {
                dataProvider = new DataProvider(this, new URL(DATA_URL));
                dataProvider.startDownloadData(); // Начинаем загрузку данных
            } catch (MalformedURLException e) {
                e.printStackTrace();
            /* По хорошему, тут должна показываться надпись "Ошибка в приложении. Сообщить разработчикам?",
            но я думаю, что это будет очень плохо смотреться */
                this.onDownloadError(); // Показать ошибку сети
            }
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        verifyChech();
        super.onResume();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GPSRequireJSInterface.ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Строка для остановки отладчика
                    int a;

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Скрывает видимый на экране{@link NavigationView}, если была нажата кнопка back
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
            if (index == -1) {
                super.onBackPressed();
                return;
            }
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            //Fragment asd = getSupportFragmentManager();
            String tag = backEntry.getName();
            if ("MainFragment".equals(tag)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Вернуться к выбору начальной точке?")
                        .setMessage("Если вы продолжите, адреса в этом заказе будут очищены")
                        //.setIcon(R.drawable.ic_android_cat)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok_alert, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.super.onBackPressed();
                                return;
                            }
                        })
                        .setNegativeButton(R.string.cancel_alert_dialog,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * Обработка выбранных элементов меню из {@link NavigationView}
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            // TODO: Отменить загрузку фрагмента, если он уже на экране

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Вернуться к выбору начальной точке?")
                    .setMessage("Если вы продолжите, адреса в текущем заказе будут очищены. Хотите создать заказ заново?")
                    //.setIcon(R.drawable.ic_android_cat)
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok_alert, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Очищаем бекстак
                            while (getSupportFragmentManager().getBackStackEntryCount() > 0){
                                getSupportFragmentManager().popBackStackImmediate();
                            }

                            // Показываем первый фрагмент
                            MainFragment mainFragment = MainFragment.newInstance(null);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, mainFragment);
                            //transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    })
                    .setNegativeButton(R.string.cancel_alert_dialog,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.nav_history) {
            // TODO: Отменить загрузку фрагмента, если он уже на экране
            // TODO: Пофиксить баг с заполнением списка, если дважды тыкнуть на кнопку в дравере

            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();

            // Иницилазация нового фрагмета
            HistoryFragment historyFragment = HistoryFragment.newInstance();
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_container, historyFragment);
            fTrans.commit();

            // Очистка ненужных более View
            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) findViewById(R.id.fragment_container) ).removeAllViews();
        }

        // Закрыть после выбора элемента
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDataReady() {
        LoginActivity.dataReady = true;
    }

    @Override
    public void onDownloadError() {
        LoginActivity.dataReady = false;
        // TODO: Вывести диалог с объяснением ошибки сети
    }

    private boolean verifyChech() {
        SharedPreferences sharedPref = getSharedPreferences("password", Context.MODE_PRIVATE);
        String phoneNumber = sharedPref.getString("password", "");
        boolean smsConfirmed = false;
        if (getIntent().getExtras() != null) {
            smsConfirmed = getIntent().getExtras().getBoolean("smsConfirmed");
        }
        // Проверка на наличие номера
        if (/*smsConfirmed || */phoneNumber.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent); // Показать активити логина
            return false;
        }
        return true;
    }
}
