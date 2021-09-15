package com.example.mydrsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Setup_complete extends AppCompatActivity {
    Button continue1;
    String patient_id, patient_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_complete);

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
                Intent i = new Intent(Setup_complete.this, Record.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                finish();
            }
        });
    }
}