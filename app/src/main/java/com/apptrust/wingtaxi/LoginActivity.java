package com.apptrust.wingtaxi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.github.vacxe.phonemask.*;


/**
 * Created by rares on 01.08.2017.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        PhoneMaskManager phoneMaskManager = new PhoneMaskManager().withMask(" (###) ###-##-##").withRegion("+7")
                .bindTo((EditText) findViewById(R.id.phoneField));
    }
}
