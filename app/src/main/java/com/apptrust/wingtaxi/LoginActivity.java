package com.apptrust.wingtaxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apptrust.wingtaxi.fragments.SummaryFragment;
import com.apptrust.wingtaxi.utils.NonSwipeableViewPager;
import com.apptrust.wingtaxi.utils.ToastHandler;
import com.github.vacxe.phonemask.*;

import org.intellij.lang.annotations.Flow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


/**
 * Created by rares on 01.08.2017.
 */
public class LoginActivity extends AppCompatActivity {
    private static final long MAP_CHECK_DELAY = 50;
    private static final long MAP_CHECK_PERIOD = 20000;

    /** Кнопка "продолжить" */
    private Button button;

    /** Контейнер текстовых полей с анимацией прокрутки */
    private NonSwipeableViewPager viewPager;

    /** {@link TextInputLayout} для номера телефона */
    private TextInputLayout phoneInputLayout;
    /** {@link TextInputLayout} для проверочного кода*/
    private TextInputLayout codeInputLayout;
    /** Поле для ввода телефона */
    private EditText phoneField;
    /** Поле для ввода SMS кода */
    private EditText codeField;

    /** Если true, то следующее нажатие Back закроет приложение */
    private boolean readyToStop = false;
    /** Если true, то UI на основной активити готов к работе и можно его показать */
    public static boolean mapReady = false;
    public static boolean dataReady = false;
    private static boolean smsConfirmed = false;
    
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Развертка разметки активити
        setContentView(R.layout.fragment_login);

        // Получение ссылок на элементы GUI
        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewPager);
        button = (Button) findViewById(R.id.login_enter);

        // Установка слушателей
        button.setOnClickListener(buttonClickListener);

        // Инициализация адаптера ViewPager
        final int slides[] = new int[] { // Массив с разметками слайдов ViewPager'а
                R.layout.viewpager_phone_slide_fragment_login,
                R.layout.viewpager_code_slide_fragment_login
        };
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return slides.length;
            }

            /**
             * Create the page for the given position.  The adapter is responsible
             * for adding the view to the container given here, although it only
             * must ensure this is done by the time it returns from
             * {@link #finishUpdate(ViewGroup)}.
             *
             * @param container The containing View in which the page will be shown.
             * @param position  The page position to be instantiated.
             * @return Returns an Object representing the new page.  This does not
             * need to be a View, but can be some other container of the page.
             */
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                LayoutInflater layoutInflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(slides[position], container, false);
                container.addView(view);

                // Установки маски телефонного номера для соответствующего поля
                if (slides[position] == R.layout.viewpager_phone_slide_fragment_login) {
                    EditText editText = (EditText) view.findViewById(R.id.phoneField);

                    phoneField = editText;

                    PhoneMaskManager phoneMaskManager = new PhoneMaskManager();
                    phoneMaskManager.withMask(" (###) ###-##-##").withRegion("+7").bindTo(editText);
                }
                if (slides[position] == R.layout.viewpager_code_slide_fragment_login) {
                    EditText editText = (EditText) view.findViewById(R.id.codeField);
                    codeField = editText;
                }

                return view;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view = (View) object;
                container.removeView(view);
            }
        });
    }

    private Button.OnClickListener buttonClickListener = new Button.OnClickListener() {

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            // Отменяем готовность закрыть приложение
            readyToStop = false;

            // TODO: Добавить проверку валидности номера перед переходом к следующему полю

            //TODO Отправить введенный юзером код запрсом через ретрофит
            if (viewPager.getCurrentItem() == 0) {
                // Сохряняем номер телефона
                SharedPreferences sharedPref = getSharedPreferences("phone", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("phone", phoneField.getText().toString());
                editor.apply();

                // Запрос на отправку данных
                try {
                    RegisterTask registerTask = new RegisterTask();
                    registerTask.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Тестовый сценарий
            if (viewPager.getCurrentItem() == 1) {
                // Скрываем клавиатуру, т.к. она больше нам не понадобится
                InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);

                if (token != null) {
                    ConfirmTask confirmTask = new ConfirmTask();
                    confirmTask.execute(token, codeField.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, "Вам еще не пришел код", Toast.LENGTH_SHORT).show();
                }

                // Проверяем введенный код
                /*if (codeField.getText().toString().equals("666777")) {
                    // Сохраняем номер телефона в главной активити
                    MainActivity.phoneNumber = "test";

                    // Проверяем загрузилась ли карта во фрагменте на основной активити
                    if (!mapReady) {
                        // Начинаем загрузку
                        showDownloadDialog();
                        return; // Действие закончено
                    }
                    else {
                        // Карта успешно загрудена и мы можем отобразить основную активити
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
                } else {
                    // Обработка неверных значений

                    // Поле пусто
                    if (codeField.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, R.string.no_auth_code, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Код неверен
                    else {
                        Toast.makeText(LoginActivity.this, R.string.incorrect_auth_code, Toast.LENGTH_LONG).show();
                        return;
                    }
                }*/
            }

            // Перелистываемся на следующее поле
            if (viewPager.getCurrentItem() != viewPager.getChildCount())
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
            else
                return;
        }
    };

    /**
     * Показываем диалог загрузки и запускаем метод, проверяющий карту на скачанность
     * ({@link #downloadChecker(long, long)})
     */
    private void showDownloadDialog() {
        // Строим и показываем диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Пожалуйста, подождите");
        builder.setView(R.layout.dialog_fragment_download);
        builder.setCancelable(false);
        builder.create().show();

        // Начинаем отслеживать изменение флага загрузки
        downloadChecker(MAP_CHECK_DELAY, MAP_CHECK_PERIOD);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            if (readyToStop) {
                // Выходим из приложения
                finishAffinity();
                return;
            } else {
                Toast.makeText(this, R.string.ready_to_stop, Toast.LENGTH_SHORT).show();
                readyToStop = true;
            }
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
        }
    }

    /**
     * Проверяет, был ли изменен флаг {@link #mapReady} (приходит извне). Если он true, то запускает
     * {@link MainActivity}.
     * @param delay Промежуток между каждой проверкой (в миллисекундах)
     * @param period Период, в течении которого выполняется проверка
     */
    private void downloadChecker(long delay, long period) {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mapReady && dataReady && smsConfirmed) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    timer.cancel();
                    timer.purge();
                }
            }

            // TODO: Обработать ситуацию, когда карта так и не была готова
        };
        timer.schedule(timerTask, delay, period);
    }

    /** Хандлер для вывода toast-сообщений, инициируемых в других потоках */
    private ToastHandler toastHandler = new ToastHandler(this);
    
    private class RegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // Достаем телефон из памяти устройства
            SharedPreferences sharedPref = getSharedPreferences("phone", Context.MODE_PRIVATE);
            String phone = sharedPref.getString("phone", "78005553535");

            // Оставляем только цифры
            phone = phone.replaceAll("[^0-9]", "");

            // Формируем исходящий JSON
            JSONObject json = new JSONObject();
            try {
                json.put("number", phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Отправляем POST на сервер и читаем ответ
            try {
                // Адрес, куда мы будем отсылать запрос для регистрации
                URL url = new URL("http://romhacking.pw:8081/register");

                // Формируем соединение
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(150000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Secret", "c8df37bef1275f33");

                // Формируем поток записи
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                // Отправляем исходящий JSON
                writer.write(json.toString());
                writer.flush();
                writer.close();
                os.close();

                // Пытаем прочитать ответ
                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    // Извещаем ToastHandler об успешной отправке смс запроса
                    toastHandler.obtainMessage(ToastHandler.SMS_REQUEST_SENT).sendToTarget();

                    // Успешно читаем ответ
                    String response = "";
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    return response;
                } else {
                    // Извещаем ToastHandler о неудачной отправке смс запроса
                    toastHandler.obtainMessage(ToastHandler.CONNECTION_ERROR).sendToTarget();
                }

            } catch (Exception e) {
                // Извещаем ToastHandler о неудачной отправке смс запроса
                toastHandler.obtainMessage(ToastHandler.CONNECTION_ERROR).sendToTarget();

                // Обрабатыаваем исключение
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Обратотчик ответа сервера
         * @param response Ответ сервера
         */
        @Override
        protected void onPostExecute(String response) {
            // TODO: Проверку на null

            JSONObject responseJSON = null;
            try {
                responseJSON = new JSONObject(response);
            } catch (JSONException e) {
                // Извещаем ToastHandler о неудаче
                toastHandler.obtainMessage(ToastHandler.SERVER_INTERNAL_ERROR).sendToTarget();
                e.printStackTrace();
            }

            // TODO: Обрабатывать статус ответа

            // Сохраняем токен
            try {
                token = responseJSON.getString("token");
            } catch (JSONException e) {
                // Извещаем ToastHandler об ошибке сервера
                toastHandler.obtainMessage(ToastHandler.SERVER_INTERNAL_ERROR).sendToTarget();

                // Обрабатыаваем исключение
                e.printStackTrace();
            }
        }
    }

    private class ConfirmTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Формируем исходящий JSON
            JSONObject json = new JSONObject();
            try {
                json.put("token", params[0]);
                json.put("code", params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Отправляем POST на сервер и читаем ответ
            try {
                // Адрес, куда мы будем отсылать запрос для регистрации
                URL url = new URL("http://romhacking.pw:8081/confirm");

                // Формируем соединение
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Secret", "c8df37bef1275f33");

                // Формируем поток записи
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                // Отправляем исходящий JSON
                writer.write(json.toString());
                writer.flush();
                writer.close();
                os.close();

                // Читаем ответ
                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    String response = "";
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response+=line;
                    }
                    return response;
                }
                else {
                    // Извещаем ToastHandler о неудачной отправке смс запроса
                    toastHandler.obtainMessage(ToastHandler.CONNECTION_ERROR).sendToTarget();
                }
            } catch (Exception e) {
                // Извещаем ToastHandler о неуспешной отправке запроса с кодом подтверждения
                toastHandler.obtainMessage(ToastHandler.CONNECTION_ERROR).sendToTarget();

                e.printStackTrace();
            }

            return null;
        }

        /**
         * Обратотчик ответа сервера
         * @param response Ответ сервера
         */
        @Override
        protected void onPostExecute(String response) {
            // TODO: Проверку на null

            JSONObject responseJSON = null;
            try {
                responseJSON = new JSONObject(response);
            } catch (JSONException e) {
                // Извещаем ToastHandler о неуспешной отправке запроса с кодом подтверждения
                toastHandler.obtainMessage(ToastHandler.SERVER_INTERNAL_ERROR).sendToTarget();
                e.printStackTrace();
            }

            // TODO: Проверять статус ответа

            // Сохраняем токен
            try {
                token = responseJSON.getString("password");
                SharedPreferences sharedPref = getSharedPreferences("password", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("password", token);
                editor.apply();

                smsConfirmed = true;
            } catch (JSONException e) {
                // Извещаем ToastHandler об ошибке сервера
                toastHandler.obtainMessage(ToastHandler.SERVER_INTERNAL_ERROR).sendToTarget();

                // Обрабатыаваем исключение
                e.printStackTrace();
            }
        }
    }
}
