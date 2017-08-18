package com.apptrust.wingtaxi;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.apptrust.wingtaxi.JSInterfaces.GPSRequireJSInterface;
import com.apptrust.wingtaxi.fragments.MainFragment;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** Номер телефона пользователя */
    public static String phoneNumber;

    /**
     * Заполняет UI и инициализирует компоненты активити: NavDraver, Draverlayout. После иницализации
     * отображает первый фрагмент - {@link MainFragment}. Если приложение запускается впервые
     * (когда номер телефона не указан) - вызывается {@link LoginActivity}.
     * @param savedInstanceState Сохраненное состояние фрагмента. Null, если фрагмент запущен впервые.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Загружать номер телефоа из SharedPreferences

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
        MainFragment mainFragment = MainFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainFragment);
        //transaction.addToBackStack(null);
        transaction.commit();


        // Проверка на наличие номера
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent); // Показать активити логина
        }
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
            super.onBackPressed();
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
            // Handle the camera action
        } else if (id == R.id.nav_history) {

        }

        // Закрыть после выбора элемента
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
