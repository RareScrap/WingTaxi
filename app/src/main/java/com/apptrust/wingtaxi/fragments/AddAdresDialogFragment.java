package com.apptrust.wingtaxi.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

/**
 * Created by RareScrap on 15.08.2017.
 */

public class AddAdresDialogFragment extends DialogFragment {
    public OrderFragment.TaxiListAdapter taxiListAdapter;
    private EditText editText;


    public static AddAdresDialogFragment newInstance(OrderFragment.TaxiListAdapter taxiListAdapter) {
        AddAdresDialogFragment fragment = new AddAdresDialogFragment();
        fragment.taxiListAdapter = taxiListAdapter;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View addAdresDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_fragment_add_adres, null);
        builder.setView(addAdresDialogView); // Добавление GUI

        // Назначение сообщения AlertDialog
        builder.setTitle("asd1");

        // Получение ссылки на элементы UI
        editText = (EditText) addAdresDialogView.findViewById(R.id.editText);

        // Добавление кнопки Set Line Width
        builder.setPositiveButton("asd2",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        taxiListAdapter.adreses.add(new Adres(0f, 0f, editText.getText().toString()));
                        taxiListAdapter.notifyDataSetChanged();
                    }
                }
        );

        return builder.create(); // Возвращение диалогового окна
    }
}
