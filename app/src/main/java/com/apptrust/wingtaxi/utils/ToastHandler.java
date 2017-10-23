package com.apptrust.wingtaxi.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ToastHandler extends Handler {
    public static final int CONNECTION_ERROR = 0;
    public static final int SMS_REQUEST_SENT = 1;
    public static final int SERVER_INTERNAL_ERROR = 2;
    public static final int INCORRECT_SMS = 3;

    private Context mContext;

    public ToastHandler(Context context) {
        super();
        this.mContext = context;
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case CONNECTION_ERROR: {
                Toast.makeText(mContext, "Ошибка сети. Проверьте настройки подключения.", Toast.LENGTH_SHORT).show();
            }
            case SMS_REQUEST_SENT: {
                Toast.makeText(mContext, "Сейчас придет СМС с кодом", Toast.LENGTH_SHORT).show();
                break;
            }
            case INCORRECT_SMS: {
                Toast.makeText(mContext, "Неверный код", Toast.LENGTH_SHORT).show();
                break;
            }
            case SERVER_INTERNAL_ERROR: {
                Toast.makeText(mContext, "Ошибка сервера. Попробуйте позже.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
