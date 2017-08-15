package com.apptrust.wingtaxi.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Date;

/**
 * Created by RareScrap on 15.08.2017.
 */

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TextView timeTextView;

    public static TimePickerDialogFragment newInstance(TextView timeTextView) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.timeTextView = timeTextView;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar rightNow = Calendar.getInstance();

        //Date currentTime = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("HH:mm");



        int a = rightNow.get(Calendar.HOUR_OF_DAY);
        int b = rightNow.get(Calendar.MINUTE);
        if (rightNow.get(Calendar.HOUR_OF_DAY) < hourOfDay || (rightNow.get(Calendar.HOUR_OF_DAY) <= hourOfDay && rightNow.get(Calendar.MINUTE) < minute)) {
            String formattedDate = hourOfDay + ":" + minute;
            timeTextView.setText(formattedDate);
        } else {
            timeTextView.setText("Сейчас");
        }
    }

}
