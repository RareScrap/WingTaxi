package com.apptrust.wingtaxi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apptrust.wingtaxi.customviews.TaxiAddresRecyclerView;
import com.github.vacxe.phonemask.*;

import java.util.ArrayList;


/**
 * Created by rares on 01.08.2017.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Развертка разметки активити
        setContentView(R.layout.test);

        TaxiAddresRecyclerView taxiAddresRecyclerView = (TaxiAddresRecyclerView) findViewById(R.id.pidr);
        taxiAddresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LayoutInflater li = LayoutInflater.from(this);
        View cv = li.inflate(R.layout.test1, null);
        taxiAddresRecyclerView.addView(cv);
    }

}
