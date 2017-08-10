package com.apptrust.wingtaxi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vacxe.phonemask.*;

import java.util.ArrayList;


/**
 * Created by rares on 01.08.2017.
 */
public class LoginActivity extends AppCompatActivity {
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
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);

            if (codeField.getText().toString().isEmpty())
                return;


            //TODO Отправить введенный юзером код запрсом через ретрофит


            // Тестовый сценарий
            if ((!codeField.getText().toString().isEmpty()) && codeField.getText().toString().equals("666777")) {
                MainActivity.phoneNumber = "test";
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Неверный код", Toast.LENGTH_LONG).show();
            }

        }
    };

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 2) {
            super.onBackPressed();
            return;
        }

        viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
    }
}
