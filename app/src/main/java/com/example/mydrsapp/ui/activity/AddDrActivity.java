package com.example.mydrsapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddDrActivity extends AppCompatActivity {

    EditText edit_first, edit_last;
    TextView txt1, txt2;
    Button save_btn;
    Dialog dialog;
    String patient_id, name, patient_name;
    ArrayList<String> arrayList;
    private RequestQueue mQueue;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dr);

        arrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patient_id = bundle.getString("id");
            Log.i("Patient ID On Add Dr.", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on record", patient_name);
        }

        mQueue = Volley.newRequestQueue(AddDrActivity.this);

        //--------------------------------get all specialty list------------------------------------
        String url1 = "http://65.2.3.41:8080/speciality?patient_id=" + patient_id;
        Log.i("URL: ", url1);

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response1) {
                try {
                    JSONArray jsonArray1 = response1.getJSONArray("data");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject data1 = jsonArray1.getJSONObject(i);
                        name = data1.getString("name");
                        Log.i("specialty name list: ", name);
                        arrayList.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request1);
        //------------------------------------------------------------------------------------------

        //------------------------------------handle bottom navigation------------------------------
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.add_dr);
        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.search:
                        Intent i1 = new Intent(AddDrActivity.this, SearchActivity.class);
                        i1.putExtra("id3", patient_id);
                        i1.putExtra("name", patient_name);
                        startActivity(i1);
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_dr:
                        return true;

                    case R.id.record:
                        Intent i = new Intent(AddDrActivity.this, RecordActivity.class);
                        i.putExtra("id2", patient_id);
                        i.putExtra("name", patient_name);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        //------------------------------------------------------------------------------------------

        txt1 = findViewById(R.id.spc_txt);
        txt2 = findViewById(R.id.cred_txt);

        edit_first = findViewById(R.id.f_ed1);
        edit_last = findViewById(R.id.l_ed1);

        save_btn = findViewById(R.id.btn_save);

        //---------------------------------save button----------------------------------------------
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editInput = edit_first.getText().toString().trim();  //New Provider First Name get String
                String editInput1 = edit_last.getText().toString().trim(); //New Provider Last Name get String
//                String textInput1 = txt1.getText().toString().trim(); //Select Specialty get String
//                String textInput2 = txt2.getText().toString().trim(); //Select Credentials get String

                if (!editInput.isEmpty() && !editInput1.isEmpty()) {

                    //----------------------saved successfully popup-------------------------------------------------------
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(AddDrActivity.this);
                    myAlert.setTitle("Saved successfully");
                    myAlert.setMessage("Provider:\n" + "Dr. " + edit_first.getText().toString() + " " + edit_last.getText().toString() + ", " + txt1.getText().toString());
                    myAlert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    myAlert.setNegativeButton("Add Another Provider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            edit_first.setText("");
                            edit_last.setText("");
                            txt1.setText("");
                            txt2.setText("");
                        }
                    });
                    myAlert.setCancelable(false);
                    myAlert.show();
                }
            }
        });
        //------------------------------------------------------------------------------------------

        //--------------------------------text1 on click action-------------------------------------
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editInput = edit_first.getText().toString().trim();  //New Provider First Name get String
                String editInput1 = edit_last.getText().toString().trim(); //New Provider Last Name get String

                if (!editInput.isEmpty() && !editInput1.isEmpty()) {
                    //-----------------------Specialty list dialog box--------------------------------------
                    dialog = new Dialog(AddDrActivity.this);
                    dialog.setContentView(R.layout.specialty_popup);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

//                    ListView listView = findViewById(R.id.list1);
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Add_Dr.this, android.R.layout.simple_list_item_1, arrayList);
//                    listView.setAdapter(adapter);
//
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Add_Dr.this, android.R.layout.simple_list_item_1, arrayList);
//                            listView.setAdapter(adapter);
//                            txt1.setText(adapter.getItem(position));
//                        }
//                    });
                }
            }
        });
    }
}