package com.example.mydrsapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mydrsapp.R;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

public class SubscribeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }
}