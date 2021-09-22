package com.example.mydrsapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mydrsapp.R;

public class SetupCompleteActivity extends AppCompatActivity {
    Button continue1;
    String patient_id, patient_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_complete);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(Color.parseColor("#FDE583"));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            patient_id = bundle.getString("id");
            Log.i("Patient ID On Setup", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on record", patient_name);
        }

        continue1 = findViewById(R.id.continue1);
        continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SetupCompleteActivity.this, RecordActivity.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                finish();
            }
        });
    }
}