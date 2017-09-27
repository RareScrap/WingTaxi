package com.apptrust.wingtaxi.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

import java.util.ArrayList;

/**
 * Отображает форму добавления адреса вручную в виде шага 2. Для отображения формы
 * в виде диалога - см {@link AddAdresDialogFragment}
 * @author RareScrap
 */
public class AddAddressFragment extends Fragment {
    /** Поле дял ввода улицы */
    private EditText streetEditText;
    /** Поле для ввода номера дома*/
    private EditText houseNumberEditText;
    /** Кнопка "Назад" */
    private Button backButton;
    /** Кнопка "Далее" */
    private Button nextButton;
    /** Если true - диалог вызван для указания второй точки маршрута. False, если диалог
     * вызва для добавления еще одного адреса в поездку */
    private boolean isNext;
    /** ТЕСТ! Выбранные адреса для шага 3*/
    private ArrayList<Adres> addresses;

    /**
     * Фабричный конструктор
     * @param isNext Если true - диалог вызван для указания второй точки маршрута. False, если диалог
     *               вызва для добавления еще одного адреса в поездку
     * @param addresses TODO
     * @return Экземпляр {@link AddAddressFragment}
     */
    public static AddAddressFragment newInstance(boolean isNext, ArrayList<Adres> addresses) {
        AddAddressFragment fragment = new AddAddressFragment();
        fragment.isNext = isNext;
        fragment.addresses = addresses;
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
        View returnedView = inflater.inflate(R.layout.dialog_set_address, container, false);

        // Получаем элементы UI
        streetEditText = (EditText) returnedView.findViewById(R.id.street_edit_text);
        houseNumberEditText = (EditText) returnedView.findViewById(R.id.house_number_edit_text);
        backButton = (Button) returnedView.findViewById(R.id.backButton);
        nextButton = (Button) returnedView.findViewById(R.id.nextButton);

        // Установка слушателей
        backButton.setOnClickListener(backButtonClickListener);
        nextButton.setOnClickListener(nextButtonClickListener);

        // Если фрагмент вызван из яндекс карт...
        ActionBar actionBar = ((MainActivity) this.getActivity()).getSupportActionBar();
        if (isNext) { // Сохряняем шаг 1
            actionBar.setTitle(R.string.first_step_title);
            actionBar.setSubtitle("");
        } else { // иначе - ставим шаг два
            actionBar.setTitle(R.string.second_step_title);
            actionBar.setSubtitle("");
        }

        // Возвращаем View фрагмента
        return returnedView;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // запрашивает фокус на первое текстовое поле и открывает цифровую клавиатуру
        streetEditText.requestFocus();
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(streetEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Слушатель кнопки "назад", расположенной на вьюхе
     */
    View.OnClickListener backButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Не тестировано!
            getFragmentManager().popBackStack();
        }
    };

    /**
     * Слушатель кнопки "далее", расположенной на вьюхе. Вызывает {@link OrderFragment}.
     */
    View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Скрываем клавиатуру
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            // Начинаем транзакцию
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            // Иницилазация нового фрагмета
            OrderFragment orderFragment = OrderFragment.newInstance(addresses);
            fTrans.addToBackStack("OrderFragment");
            fTrans.replace(R.id.fragment_container, orderFragment);
            fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fTrans.commit();

            // Очистка ненужных более View
            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_container) ).removeAllViews();
        }
    };
}
