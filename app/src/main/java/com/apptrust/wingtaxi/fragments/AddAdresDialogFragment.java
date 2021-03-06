package com.apptrust.wingtaxi.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptrust.wingtaxi.JSInterfaces.GetSearchResultJSInterface;
import com.apptrust.wingtaxi.LoginActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Отображает форму добавления адреса вручную в виде диалогового окна
 * Created by RareScrap on 15.08.2017.
 */
public class AddAdresDialogFragment extends DialogFragment {
    private static final String LOG_TAG = "Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyC6xpohg6WC10BtWKqu9ZhoqiCA-mutomE";

    private AppCompatButton backButton;
    private AppCompatButton nextButton;
    private EditText streetEditText;
    private EditText houseNumberEditText;
    private RecyclerView resultsRecyclerView;
    /** Если true - фрагмент открыт из страницы настройка заказа. False, если
     * фрагмент открыт из яндекс карт */
    private boolean isAddmode;
    private WebView webView;

    private AutocompleteResultAdapter adapter;

    private ArrayList<Adres> results = new ArrayList<>();
    private OrderFragment orderFragmentLink = null;

    /**
     * Фабричный конструктор
     * @param isAddMode Если true - фрагмент открыт из страницы настройка заказа. False, если
     *                  фрагмент открыт из яндекс карт
     * @return Экземпляр {@link AddAdresDialogFragment}
     */
    public static AddAdresDialogFragment newInstance(boolean isAddMode, OrderFragment orderFragment) {
        AddAdresDialogFragment fragment = new AddAdresDialogFragment();
        fragment.isAddmode = isAddMode;
        fragment.orderFragmentLink = orderFragment;
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
        resultsRecyclerView = (RecyclerView) addAdresDialogView.findViewById(R.id.results);

        // Установка слушателей на кнопки
        backButton.setOnClickListener(backButtonClickListener);
        nextButton.setOnClickListener(nextButtonClickListener);
        streetEditText.addTextChangedListener(streetEditTextWatcher);

        //adapter = new AutocompleteResultAdapter(new ArrayList<Adres>());
        //resultsRecyclerView.setAdapter(adapter);

        // Создание диалога
        Dialog dialog = builder.create();

        // Автоматически открывать клавиатуру для ввода на первое текстовое поле
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);

        // Инициализация JS-интерфейсов
        //GetSearchResultJSInterface getSearchResultJSInterface = new GetSearchResultJSInterface(this);
        //webView.addJavascriptInterface(getSearchResultJSInterface, "sendSearchResult");

        // Последние приготолеия
        webView.clearCache(true);
        webView.loadUrl("http://romhacking.pw/NEW_MAP10/map.html");

        adapter = new AutocompleteResultAdapter(new ArrayList<Adres>());
        resultsRecyclerView.setAdapter(adapter);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    private TextWatcher streetEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            new Autocomplete().execute(s+"");
            /*Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault()); // TODO: Мейби заменить на русскую
            geocoder.
            ArrayList<Address> a = new ArrayList<>();
            try {
                a = (ArrayList<Address>) geocoder.getFromLocationName("иркутск больница", 20);
                int b = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getActivity(), String.valueOf(a.size()), Toast.LENGTH_SHORT).show();
            houseNumberEditText.setText(String.valueOf(a.size()));
            int asdqwe = 123;*/
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private class Autocomplete extends AsyncTask<String, Void, String> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            ArrayList resultList = null;
            HttpURLConnection conn = null;
            final StringBuilder jsonResults = new StringBuilder();

            // TODO: Добавить шелехов
            String input = "Иркутск" + params[0];
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + API_KEY);
                //sb.append("&components=country:gr");
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));
                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error URL", e);
                return "";
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting", e);
                return "";
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                resultList = new ArrayList(predsJsonArray.length());

                for (int i = 0; i < predsJsonArray.length(); i++) {
                    System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                    System.out.println("============================================================");
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
            return jsonResults.toString();
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param arrayList The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String jsonString) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String status = null;
            try {
                status = jsonObject.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!"OK".equals(status)) {
                Log.e("STATUS", "NOT OK!!!");
                return;
            }

            JSONArray elements = new JSONArray();
            try {
                elements = jsonObject.getJSONArray("predictions");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<Adres> resultArrayList = new ArrayList<>();
            for (int i = 0; i < elements.length(); i++) {
                JSONObject result = new JSONObject();
                String addressString = "";
                String placeID = "";
                // TODO: парсить координаты


                try {
                    result = (JSONObject) elements.get(i);
                    //addressString = result.getString("description");
                    addressString += result.getJSONArray("terms").getJSONObject(0).getString("value") + ", ";
                    addressString += result.getJSONArray("terms").getJSONObject(1).getString("value") + ", ";
                    addressString += result.getJSONArray("terms").getJSONObject(2).getString("value");

                    placeID = result.getString("place_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!addressString.contains("Иркутск")) {
                    break;
                }

                resultArrayList.add(new Adres(addressString, placeID));
            }



            adapter.addresses = resultArrayList;
            adapter.notifyDataSetChanged();
        }
    }

    private class AutocompleteResultAdapter extends RecyclerView.Adapter<AutocompleteResultAdapter.ViewHolder> {
        public ArrayList<Adres> addresses = new ArrayList<>();

        public AutocompleteResultAdapter(ArrayList<Adres> adreses) {
            this.addresses = adreses;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout linearLayout;
            private TextView addressTextView;

            public ViewHolder(View itemView) {
                super(itemView);

                // Получение ссылок на элеметы UI
                addressTextView = (TextView) itemView.findViewById(R.id.address);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);

                // Устанавливаем слушатели
                linearLayout.setOnClickListener(elementClickListener);
            }

            View.OnClickListener elementClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Скрываем клавиатуру, т.к. она больше нам не понадобится
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    // ТЕСТОВЫЙ СПИСОК!
                    ArrayList<Adres> addresses = new ArrayList<>();
                    addresses.add( Adres.initCoordsByPlaceID(adapter.addresses.get(getAdapterPosition()), getContext()) );
                    //addresses.add(v.getParent().);
                    //addresses.add(selectedAddress);

                    if (isAddmode) {
                        orderFragmentLink.addAdres(addresses.get(addresses.size()-1));
                        AddAdresDialogFragment.this.dismiss();
                    } else {
                        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
                        // Иницилазация нового фрагмета
                        AddAddressFragment addAddressFragment = AddAddressFragment.newInstance(false, addresses);
                        fTrans.addToBackStack("AddAddressFragment");
                        fTrans.replace(R.id.fragment_container, addAddressFragment);
                        fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fTrans.commit();

                        // Очистка ненужных более View
                        // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
                        ((ViewGroup) getActivity().findViewById(R.id.fragment_container)).removeAllViews();

                        // Скрываем диалоговое меню
                        AddAdresDialogFragment.this.dismiss();
                    }
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
        public AutocompleteResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_fragment_autocomplete_result_recyclerview, parent, false);

            return (new ViewHolder(view));
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
        public void onBindViewHolder(AutocompleteResultAdapter.ViewHolder holder, int position) {
            // Получение объекта FoodItem для заданной позиции ListView
            Adres adresItem = addresses.get(position);

            holder.addressTextView.setText(adresItem.textAdres);
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return addresses.size();
        }
    }
}
