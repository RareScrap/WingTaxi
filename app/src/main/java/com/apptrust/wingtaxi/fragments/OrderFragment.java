package com.apptrust.wingtaxi.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptrust.wingtaxi.JSInterfaces.SendDataJSInterface;
import com.apptrust.wingtaxi.JSInterfaces.UpdateDataJSInterface;
import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;
import com.apptrust.wingtaxi.utils.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

/**
 * Фрагмент настойки адресов поездки. Позволяет пользователю задать дополительые адреса
 * и назачит время к которому должно быть подано такси
 * @author RareScrap
 */
public class OrderFragment extends Fragment implements
        UpdateDataJSInterface.JSRequestUpdateData,
        SendDataJSInterface.JSRequestData {
    /** Минимальное количество адресов, достаточных для заказа такси*/
    private static final int MIN_ADDRESSES_COUNT = 2;
    /** Список адресов, которые выбрал пользователь */
    public ArrayList<Adres> adreses = new ArrayList<>();
    /** UI списка */
    private RecyclerView mRecyclerView;
    /** Адаптер списка */
    private TaxiListAdapter mTaxiListAdapter;
    /** Кнопка перехода к следующему фрагменту */
    private Button nextButton;
    /** Невидимый {@link android.webkit.WebView}, вычисляющий цену поездки */
    private WebView webView;
    /** Время заказа для отправки на сервер */
    public String dateString;
    /** View банера */
    private ImageView banner;

    private int price;
    private Handler toastHandler;

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параметров
     * @param firstAdres Первый адрес, с которого пользователь начинает путь
     * @return Новый объект фрагмента {@link OrderFragment}
     */
    public static OrderFragment newInstance(Adres firstAdres) {
        OrderFragment fragment = new OrderFragment();

        // TODO
        // Добавляет первый адрес в список адресов
        fragment.adreses.add(firstAdres);

        // Вернуть новый экземпляр фрагмента
        return fragment;
    }

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параметров
     * @param addressList Начальный список адресов
     * @return Новый объект фрагмента {@link OrderFragment}
     */
    public static OrderFragment newInstance(ArrayList<Adres> addressList) {
        OrderFragment fragment = new OrderFragment();

        // Добавляет начальные адреса в список адресов
        fragment.adreses = addressList;

        // Вернуть новый экземпляр фрагмента
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
     * Инициализирует {@link #mRecyclerView} и вызывает UI фрагмента.
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
        View returnedView = inflater.inflate(R.layout.fragment_order, container, false);

        // Инициализация UI списка
        mRecyclerView = (RecyclerView) returnedView.findViewById(R.id.adresRecyclerView);
        //addAdresImageButton = (ImageButton) returnedView.findViewById(R.id.addAdresImageButton);
        //timeSetImageButton = (ImageButton) returnedView.findViewById(R.id.timeSetImageButton);
        nextButton = (Button) returnedView.findViewById(R.id.next_button);
        //timeTextView = (TextView) returnedView.findViewById(R.id.selectedTimeTextView);
        banner = (ImageView) returnedView.findViewById(R.id.banner);
        mTaxiListAdapter = new TaxiListAdapter(adreses, deleteClickListener);
        mRecyclerView.setAdapter(mTaxiListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(R.string.third_step_title); // Вывести в титульую строку название блюда
        ab.setSubtitle(""); // Стереть подстроку

        // Устаовка слушателя на кнопки
        nextButton.setOnClickListener(nextClickListener);

        // Инициализация WebView
        webView = (WebView) returnedView.findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // Включения вывода в консоль логов с WebView
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                /*Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );*/
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());

                return true;
            }
        });

        // Инициализация JS-интерфейсов
        UpdateDataJSInterface updateDataJSInterface = new UpdateDataJSInterface(this);
        SendDataJSInterface sendDataJSInterface = new SendDataJSInterface(this);
        // TODO: Заменить название интерфейса в JS
        webView.addJavascriptInterface(updateDataJSInterface, "updateDataJSInterface");
        webView.addJavascriptInterface(sendDataJSInterface, "getDataJSInterface");

        // Последние приготолеия WebView
        webView.clearCache(true);
        if (adreses.size() < MIN_ADDRESSES_COUNT) {
            nextButton.setText("Добавьте еще один адреес");
            nextButton.setEnabled(false);
        } else {
            nextButton.setText("Ёбаный позор и костылина");
            nextButton.setEnabled(true);
            // webView.loadUrl("http://romhacking.pw/NEW_ROUTE2/route_map/map.html");
        }

        // Инициализаци тост=хандлера (для вызова тостов не из UI-потока)
        toastHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Заказ отправлен!")
                            .setMessage("Вам придет СМС с Именем и Телефоном водителя. Пожалуйста, ожидайте :)")
                            .setCancelable(false)
                            .setNegativeButton("Посмотреть маршрут",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

                                            // Удаляем последний null-элемент (маркер для элемента с навигацией)
                                            adreses.remove(adreses.size()-1);

                                            // Иницилазация нового фрагмета
                                            SummaryFragment summaryFragment = SummaryFragment.newInstance(adreses, dateString);
                                            fTrans.addToBackStack(null);
                                            fTrans.replace(R.id.fragment_container, summaryFragment);
                                            fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                            fTrans.commit();

                                            // Очистка ненужных более View
                                            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
                                            ( (ViewGroup) getActivity().findViewById(R.id.fragment_container) ).removeAllViews();

                                            // Закрываем диалог
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(getContext(), "Не удалось отправить заказ", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Вернуть UI фрагмента
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
        // Расчет размеров банера (относительно размера экрана)
        // TODO: Или лучше относительно своей собственной ширины?
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ViewGroup.LayoutParams bannerLayoutParams =  banner.getLayoutParams();
        bannerLayoutParams.width = size.x;
        // TODO: magic numbers!
        bannerLayoutParams.height =  3*bannerLayoutParams.width / 8;
    }

    public class TaxiListAdapter extends RecyclerView.Adapter<TaxiListAdapter.ViewHolder> {
        /**
         * Список адресов, которые выбрал пользователь. Последний элемент списка должен быть
         * null, чтобы в качестве последнего элемента показалась панель с кнопками
         */
        public ArrayList<Adres> addresses = new ArrayList<>();
        /** Слушатель клика по кнопке "удалить" */
        private final View.OnClickListener deleteClickListener;

        /**
         * Конструктор, инициализирующий свои поля
         *
         * @param adreses Список адресов, которые нужно отобразить в элеметах списка
         * @param deleteClickListener Слушатель клика по кнопке "удалить"
         */
        public TaxiListAdapter(ArrayList<Adres> adreses, View.OnClickListener deleteClickListener) {
            this.addresses = adreses;

            // null-элемент - маркер для создания последнего элемента с кнопками
            adreses.add(null);

            this.deleteClickListener = deleteClickListener;
        }


        /**
         * Этот ViewHolder используется для двух макетов сразу - item_fragment_order_recyclerview.xml
         * и item_fragment_order_last_recyclerview. Последняя разметка - последний элемент списка с
         * кнопками добавления адреса и выбора времени.
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView adresTextView;
            private final ImageButton deleteButton;

            // Эти View - только для последнего элемента
            private ImageView point = null;
            private ImageView timeImageButton = null;
            private ImageView addCommentButton = null;
            private TextView addressEditText = null;
            private TextView timeTextView = null;
            private TextView addCommentTextView = null;

            public ViewHolder(View itemView, View.OnClickListener clickListener) {
                super(itemView);

                // Получение ссылок на элеметы UI
                adresTextView = (TextView) itemView.findViewById(R.id.adresEditText);
                deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);

                /*
                Проверка на последний элемент: deleteButton будет null, т.к. кнопки
                "удалить" не будет на последнем макете списка, который вызовется onCreateViewHolder()
                 */
                if (deleteButton == null) {
                    // Получаем ссылки на элементы UI последнего элемента списка
                    point = (ImageView) itemView.findViewById(R.id.point);
                    timeImageButton = (ImageView) itemView.findViewById(R.id.timeButton);
                    addCommentButton = (ImageView) itemView.findViewById(R.id.comment_image_view);
                    addressEditText = (TextView) itemView.findViewById(R.id.adresEditText);
                    timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
                    addCommentTextView = (TextView) itemView.findViewById(R.id.comment_text_view);

                    // Устанавливаем слушатели
                    point.setOnClickListener(addAdresClickListener);
                    addressEditText.setOnClickListener(addAdresClickListener);
                    timeImageButton.setOnClickListener(timeSetClickListener);
                    timeTextView.setOnClickListener(timeSetClickListener);
                    addCommentButton.setOnClickListener(addCommentClickListener);
                    addCommentTextView.setOnClickListener(addCommentClickListener);
                } else {
                    // Связывание слушателя со кнопкой "удалить"
                    deleteButton.setOnClickListener(clickListener);
                }
            }

            View.OnClickListener addAdresClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddAdresDialogFragment addAdresDialogFragment = AddAdresDialogFragment.newInstance(true, OrderFragment.this);
                    addAdresDialogFragment.show(getFragmentManager(), "AddAdresDialogFragment");
                }
            };
            View.OnClickListener timeSetClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Выбрать время")
                            .setMessage("Заказать машину на сейчас или выбрать другое время?")
                            //.setIcon(R.drawable.ic_android_cat)
                            .setCancelable(true)
                            .setPositiveButton("Сейчас", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    timeTextView.setText("Сейчас");
                                }
                            })
                            .setNegativeButton("Выбрать",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(timeTextView, OrderFragment.this);
                                            timePickerDialogFragment.show(getFragmentManager(), "l2312");
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            };
            View.OnClickListener addCommentClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO
                }
            };

        }

        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(ViewHolder, int)
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Заполнение макета item'а списка
            View view;
            if (parent.getChildCount() < TaxiListAdapter.this.addresses.size()-1)
                view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_fragment_order_recyclerview, parent, false);
            else
                view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_fragment_order_last_recyclerview, parent, false);


            // Создание ViewHolder для текущего элемента
            return (new ViewHolder(view, deleteClickListener));
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p>
         * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Получение объекта FoodItem для заданной позиции ListView
            Adres adresItem = adreses.get(position);

            // Проверяем итем на тип - обычный или с кнопками
            if (adresItem != null) { // Заполняем обычный элемент
                // Обработка поведения View'хи при разных количествах элементов в списке
                View topLine = holder.itemView.findViewById(R.id.top_line);
                View topLineArrow = holder.itemView.findViewById(R.id.top_line_arrow);
                View bottomLine = holder.itemView.findViewById(R.id.bottom_line);
                topLine.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.VISIBLE);
                if (adreses.size()-1 == position + 1 && adreses.size()-1 == 1) {
                    topLine.setVisibility(View.INVISIBLE);
                    topLineArrow.setVisibility(View.INVISIBLE);
                    bottomLine.setVisibility(View.INVISIBLE);
                } else {
                    if (position == 0) {
                        topLine.setVisibility(View.INVISIBLE);
                        topLineArrow.setVisibility(View.INVISIBLE);
                    }
                    if (position == adreses.size()-1 - 1)
                        bottomLine.setVisibility(View.INVISIBLE);
                }

                // Скрываем крестики у первых двух адресов
                if (position < MIN_ADDRESSES_COUNT)
                    holder.deleteButton.setVisibility(View.INVISIBLE);

                // Назначения текста элементам GUI
                holder.adresTextView.setText(adresItem.textAdres);
            } else { // заполняем элемент с кнопками
                // TODO: Позже
            }
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return adreses.size();
        }
    }

    public void addAdres(Adres adres) {
        adreses.add(adreses.size()-1, adres);
        for (int i = 0; i < adreses.size(); i++) {
            if (adreses.get(i) == null) {
                adreses.remove(i);
            }
        }
        //adreses.add(null);

        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
        mTaxiListAdapter = new TaxiListAdapter(adreses, deleteClickListener);
        mRecyclerView.setAdapter(mTaxiListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTaxiListAdapter.notifyDataSetChanged();

        if (adreses.size() < MIN_ADDRESSES_COUNT) {
            nextButton.setText("Добавьте еще один адрес");
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(false);
            nextButton.setText(getString(R.string.next_button_calc));
            webView.loadUrl("http://romhacking.pw/NEW_ROUTE2/route_map/map.html");
        }
    }

    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            // TODO: Убрать цепучку getParent
            TaxiListAdapter.ViewHolder viewHolder = (TaxiListAdapter.ViewHolder) mRecyclerView.getChildViewHolder((View) view.getParent().getParent().getParent());
            int pos = viewHolder.getLayoutPosition()-1;
            adreses.remove(pos+1);
            adreses.remove(adreses.size()-1);

            mRecyclerView.setAdapter(null);
            mRecyclerView.setLayoutManager(null);
            mTaxiListAdapter = new TaxiListAdapter(adreses, deleteClickListener);
            mRecyclerView.setAdapter(mTaxiListAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mTaxiListAdapter.notifyDataSetChanged();

            if (adreses.size() < MIN_ADDRESSES_COUNT) {
                nextButton.setText("Добавьте еще один адрес");
                nextButton.setEnabled(false);
            } else {
                nextButton.setEnabled(true);
                nextButton.setText("Ебаный позор и костылина");
                // webView.loadUrl("http://romhacking.pw/NEW_ROUTE2/route_map/map.html");
            }
        }
    };

    /**
     * Слушатель для кнопки перехода к фрагменту {@link SummaryFragment}
     */
    View.OnClickListener nextClickListener = new View.OnClickListener() {
        /**
         * Откывает фрагмента {@link SummaryFragment} при клике
         * @param v {@link View} кнопки, по которой был сделан клик
         */
        @Override
        public void onClick(View v) {
            Calendar rightNow = Calendar.getInstance();
            int h = rightNow.get(Calendar.HOUR_OF_DAY);
            int m = rightNow.get(Calendar.MINUTE);

            ArrayList<Adres> notNullAdreses = adreses;
            if (notNullAdreses.get(notNullAdreses.size()-1)== null) {
                notNullAdreses.remove(notNullAdreses.size()-1);
            }
            Order order = new Order(notNullAdreses, h, m);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Log.i("GSON", gson.toJson(order));

            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyy-HH.mm.ss");
            String fileName = format.format(rightNow.getTime()) + ".json";

            try {
                //File file = new File(path, rightNow.toString() + ".json");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        getContext().openFileOutput(
                                fileName,
                                Context.MODE_PRIVATE
                        )
                );
                outputStreamWriter.write(gson.toJson(order));
                outputStreamWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            SendDataTask sendDataTask = new SendDataTask();
            sendDataTask.execute();
        }
    };

    @Override
    public void onJSRequestUpdateAdres(double longitude, double latitude, String address) {}

    @Override
    public void onJSRequestUpdateRouteLength(float length) {
        // TODO: Перенести вычисление стоимости на сервер
        float calclAdditionalKm = 0;
        if (length - MainActivity.dataProvider.minTariffKm*1000 > 0)
            calclAdditionalKm = length/1000 - MainActivity.dataProvider.minTariffKm;

        final float additionalKm = calclAdditionalKm;
        final float additionalPay = additionalKm * MainActivity.dataProvider.additionalPricePerKm;

        final float totalPrice = MainActivity.dataProvider.minTariffPrice + additionalPay;
        price = (int) totalPrice;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextButton.setEnabled(true);
                nextButton.setText(getResources().getString(R.string.next_button_cash, totalPrice));
            }
        });
    }

    @Override
    public void onJSRequestUpdateTripTime(int h, int m) {}

    @Override
    public int onJSRequestRoutePointsNumber() {
        if (adreses.get(adreses.size()-1) == null) {
            return adreses.size()-1;
        } else {
            return adreses.size();
        }
    }

    @Override
    public double onJSRequestRoutePointCoord(int pointIndex, int coordIndex) {
        if (pointIndex < 0 || pointIndex > adreses.size()) {
            Log.e("RoutePointCoord", "Error pointIndex");
            return -1;
        }

        switch (coordIndex) {
            case 0: {
                return adreses.get(pointIndex).longitude;
            }
            case 1: {
                return adreses.get(pointIndex).latitude;
            }
            default: {
                Log.e("RoutePointCoord", "Error coordIndex");
                return -1;

            }
        }
    }

    /**
     * Отправляет заказ на сервер
     */
    private class SendDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject json = new JSONObject();

            JSONArray arr = new JSONArray();
            for (int i = 0; i < adreses.size(); i++) {
                if (adreses.get(i) != null) {
                    arr.put(adreses.get(i).textAdres);
                }
            }

            SharedPreferences sharedPref = getActivity().getSharedPreferences("phone", Context.MODE_PRIVATE);
            String phone = sharedPref.getString("phone", "AZAZA");

            // Оставляем только цифры
            phone = phone.replaceAll("[^0-9]", "");

            SharedPreferences sharedPref1 = getActivity().getSharedPreferences("password", Context.MODE_PRIVATE);
            String password = sharedPref.getString("password", "nahui_idi_.!.");

            try {
                json.put("addresses", arr);
                json.put("phoneNumber", phone);
                json.put("password", password);
                json.put("price", price);
                json.put("date", dateString);
            } catch (JSONException e) {}



            String response = "";
            try {
                URL url = new URL("http://romhacking.pw:8081/makeorder");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Secret", "c8df37bef1275f33");


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(json.toString());

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Message message = toastHandler.obtainMessage(1);
                    message.sendToTarget();

                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    Message message = toastHandler.obtainMessage(0);
                    message.sendToTarget();
                    response="";

                }
            } catch (Exception e) {
                Message message = toastHandler.obtainMessage(0);
                message.sendToTarget();
                e.printStackTrace();
            }

            return null;
        }
    }
}
