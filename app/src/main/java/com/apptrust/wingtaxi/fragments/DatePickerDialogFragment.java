package com.apptrust.wingtaxi.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author RareScrap
 */
public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private int hourOfDay;
    private int minute;
    private TextView textView;
    private OrderFragment orderFragment;

    public DatePickerDialogFragment() {}

    public static DatePickerDialogFragment newInstance(int hourOfDay, int minute, TextView timeTextView, OrderFragment orderFragment) {

        Bundle args = new Bundle();
        
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.hourOfDay = hourOfDay;
        fragment.minute = minute;
        fragment.textView = timeTextView;
        fragment.orderFragment = orderFragment;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar now = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hourOfDay, minute, 0);

        if (c.after(now)) {
            //"2017.07.03 23:28:50"
            SimpleDateFormat formatSave = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
            orderFragment.dateString = formatSave.format(c.getTime());

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd 'в' kk.mm");
            textView.setText(format.format(c.getTime()));
        } else {
            Toast.makeText(getActivity(), "Вы выбрали дату, которая уже прошла", Toast.LENGTH_SHORT).show();
        }
    }
}