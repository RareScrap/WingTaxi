package com.apptrust.wingtaxi.fragments;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
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
    //private HistoryListAdapter mHistoryListAdapter;
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
        View returnedView = inflater.inflate(R.layout.fragment_order, container, false);

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
        //mHistoryListAdapter = new HistoryListAdapter(orders,reuseClickListener)
        //mRecyclerView.setAdapter(mHistoryListAdapter);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(R.string.fragment_history_title); // Вывести в титульую строку название блюда
        ab.setSubtitle(""); // Стереть подстроку

        // Вернуть UI фрагмента
        return returnedView;
    }
}
