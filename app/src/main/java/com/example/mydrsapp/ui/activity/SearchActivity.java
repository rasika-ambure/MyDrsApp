package com.example.mydrsapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mydrsapp.R;

public class SearchActivity extends AppCompatActivity {

    String patient_id, patient_name;
    TextView lastThree,searchBySpecialty, searchByDate, navigate, viewAll;
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
                Intent i = new Intent(SearchActivity.this, LastThreeRecordingsActivity.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                overridePendingTransition(0, 0);
//                finish();
            }
        });

        searchByDate = findViewById(R.id.searchByDate);
        searchByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, SearchByDateRangeActivity.class);
                i.putExtra("id", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                overridePendingTransition(0, 0);
//                finish();
            }
        });

        searchBySpecialty = findViewById(R.id.searchBySpecialty);
        searchBySpecialty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, SearchBySpecialistActivity.class);
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
                Intent i = new Intent(SearchActivity.this, RecordActivity.class);
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
                Intent i = new Intent(SearchActivity.this, ViewAllRecActivity.class);
                i.putExtra("id2", patient_id);
                i.putExtra("name", patient_name);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }

}