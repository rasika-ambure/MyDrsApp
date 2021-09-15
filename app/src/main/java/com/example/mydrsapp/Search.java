package com.example.mydrsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class Search extends AppCompatActivity {

    String patient_id, patient_name;
    TextView lastThree, navigate, viewAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            patient_id = bundle.getString("id3");
            Log.i("Patient ID On Share", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on record", patient_name);
        }

        lastThree = findViewById(R.id.last_three);
        lastThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Search.this, LastThreeRec.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                overridePendingTransition(0, 0);
//                finish();
            }
        });

        navigate = findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Search.this, Record.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        viewAll = findViewById(R.id.viewAll);
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Search.this, ViewAllRec.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }

}