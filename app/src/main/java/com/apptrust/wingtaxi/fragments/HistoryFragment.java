package com.apptrust.wingtaxi.fragments;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;
import com.apptrust.wingtaxi.utils.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author RareScrap
 */
public class HistoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    /** Адаптер списка */
    private HistoryListAdapter mHistoryListAdapter;
    private ArrayList<Order> orders = new ArrayList<>();

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
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
        View returnedView = inflater.inflate(R.layout.fragment_history, container, false);

        // TODO: Взять orders с диска
        // Получаем список имен файлов
        /*File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, ContextWrapper.getFilesDir());*/

        File yourDir = getActivity().getFilesDir();

        ArrayList<String> fileNames = new ArrayList<>();
        for (File f : yourDir.listFiles()) {
            if (f.isFile())
                fileNames.add(f.getName());
        }





        try {
            for (int i = 0; i < fileNames.size(); i++) {
                String jsonText = "";
                InputStream inputStream = getContext().openFileInput(fileNames.get(i));

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    jsonText = stringBuilder.toString();

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Order order = gson.fromJson(jsonText, Order.class);
                    orders.add(order);
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }







        // Инициализация UI списка
        mRecyclerView = (RecyclerView) returnedView.findViewById(R.id.recyclerView);
        mHistoryListAdapter = new HistoryListAdapter(orders, reuseClickListener);
        mRecyclerView.setAdapter(mHistoryListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(R.string.fragment_history_title); // Вывести в титульую строку название блюда
        ab.setSubtitle(""); // Стереть подстроку

        // Вернуть UI фрагмента
        return returnedView;
    }

    public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {
        /**
         * Список адресов, которые выбрал пользователь
         */
        public ArrayList<Order> orders = new ArrayList<>();
        private final View.OnClickListener clickListener;

        public HistoryListAdapter(ArrayList<Order> orders, View.OnClickListener clickListener) {
            this.orders = orders;
            this.clickListener = clickListener;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final LinearLayout linearLayout;
            public final Button reuseButton;

            public ViewHolder(View itemView, View.OnClickListener clickListener) {
                super(itemView);

                // Получение ссылок на элеметы UI
                linearLayout = (LinearLayout) itemView.findViewById(R.id.addresses_list);
                reuseButton = (Button) itemView.findViewById(R.id.reuse_button);
                reuseButton.setOnClickListener(clickListener);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Заполнение макета item'а списка
            View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_fragment_history_recyclerview, parent, false);

            // Создание ViewHolder для текущего элемента
            return (new ViewHolder(view, clickListener));
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Получение объекта Order для заданной позиции RecyclerView
            Order orderItem = orders.get(position);
            LinearLayout linearLayout = (LinearLayout) holder.itemView.findViewById(R.id.addresses_list);

            linearLayout.removeAllViews();

            for (int i = 0; i < orderItem.adresses.size(); i++) {
                // TODO: Лучшее ли это место для инфлейта элемента списка адресов? Хорошая ли идея перенести это в onCreateViewHolder()?
                RelativeLayout addressItem = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_fragment_order_recyclerview, null, false);
                TextView addressItemTExtView = (TextView) addressItem.findViewById(R.id.adresTextView);
                ImageButton deleteButton = (ImageButton) addressItem.findViewById(R.id.deleteButton);

                addressItemTExtView.setText(orderItem.adresses.get(i).textAdres);
                deleteButton.setVisibility(View.GONE);

                // Обработка поведения View'хи при разных количествах элементов в списке
                ArrayList<Adres> adreses = orderItem.adresses;
                int pos = i;

                View topLine = addressItem.findViewById(R.id.top_line);
                View bottomLine = addressItem.findViewById(R.id.bottom_line);
                topLine.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.VISIBLE);
                if (adreses.size() == pos+1 && adreses.size() == 1) {
                    topLine.setVisibility(View.INVISIBLE);
                    bottomLine.setVisibility(View.INVISIBLE);
                } else {
                    if (pos == 0)
                        topLine.setVisibility(View.INVISIBLE);
                    if (pos == adreses.size()-1)
                        bottomLine.setVisibility(View.INVISIBLE);
                }

                linearLayout = (LinearLayout) holder.itemView.findViewById(R.id.addresses_list);
                //((ViewGroup)address.getParent()).removeView(address);
                linearLayout.addView(addressItem);
            }

        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return orders.size();
        }
    }

    View.OnClickListener reuseClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            HistoryListAdapter.ViewHolder viewHolder = (HistoryListAdapter.ViewHolder) mRecyclerView.getChildViewHolder((View) view.getParent());
            int pos = viewHolder.getLayoutPosition();

            // Иницилазация нового фрагмета
            OrderFragment orderFragment = OrderFragment.newInstance(orders.get(pos).adresses);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_container, orderFragment);
            fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fTrans.commit();

            // Очистка ненужных более View
            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_container) ).removeAllViews();
        }
    };
}
