package com.apptrust.wingtaxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.vacxe.phonemask.*;


/**
 * Created by rares on 01.08.2017.
 */
public class LoginActivity extends AppCompatActivity {
    /** Кнопка "продолжить" */
    private Button button;

    TextInputLayout a;
    TextInputLayout b;

    /** Поле для ввода телефона */
    private EditText phoneField;
    /** Поле для ввода SMS кода */
    private EditText codeField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        // Получение ссылко на элементы GUI
        phoneField = (EditText) findViewById(R.id.phoneField);
        codeField = (EditText) findViewById(R.id.codeField);
        button = (Button) findViewById(R.id.login_enter);

        a = (TextInputLayout) findViewById(R.id.phoneInputLayout);
        b = (TextInputLayout) findViewById(R.id.codeInputLayout);


        // Установка слушателей
        button.setOnClickListener(buttonClickListener);

        // Применяем маску для воода телефона (для удобства пользователя)
        PhoneMaskManager phoneMaskManager = new PhoneMaskManager();
        phoneMaskManager.withMask(" (###) ###-##-##").withRegion("+7").
                bindTo((EditText) findViewById(R.id.phoneField));
    }

    private Button.OnClickListener buttonClickListener = new Button.OnClickListener() {

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            a.setVisibility(View.INVISIBLE);
            b.setVisibility(View.VISIBLE);
            if (codeField.getText().toString().isEmpty())
                return;


            //TODO Отправить введенный юзером код запрсом через ретрофит


            // Тестовый сценарий
            if ((!codeField.getText().toString().isEmpty()) && codeField.getText().toString().equals("666777")) {
                MainActivity.phoneNumber = "test";
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Неправильный код", Toast.LENGTH_LONG).show();
            }

        }
    };

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (a.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            return;
        }

        a.setVisibility(View.VISIBLE);
        b.setVisibility(View.INVISIBLE);
    }
}
