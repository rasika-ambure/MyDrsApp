package com.example.mydrsapp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SearchBySpecialistActivity extends AppCompatActivity {

//---------------------- Declaration for Specialty & Provider Edit Views ---------------------------

    String id, name, specialty_id, specialty_id_all, specialty_name, selected_spc_name, selected_spc_id, provider_name, providerName, categoryName;
    Dialog dialog, dialog2;
    TextView txt1, txt2, pro;
    Button continue1;

    ArrayList<String> arrayList, arrayList2, spc_array, data, spc_name, pro_name, txt2_array, temp;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_specialist);

        continue1 = findViewById(R.id.search_spec);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id2");
            Log.i("Patient ID SBS", id);
            name = bundle.getString("name");
            Log.i("Patient Name SBS", name);
        }
        Log.i("Outside SBS ID", id);
        Log.i("Outside SBS Name", name);

//----------------------------------- Specialty ----------------------------------------------------

        txt1 = findViewById(R.id.select_soc_specialty);
        txt2 = findViewById(R.id.select_pro_specialty);
        pro = findViewById(R.id.pro_specialty);

        txt2.setVisibility(View.GONE);
        pro.setVisibility(View.GONE);

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        spc_array = new ArrayList<>();
        data = new ArrayList<>();
        spc_name = new ArrayList<>();
        pro_name = new ArrayList<>();
        txt2_array = new ArrayList<>();
        temp = new ArrayList<>();

        mQueue = Volley.newRequestQueue(SearchBySpecialistActivity.this);

        //----------------------------get All specialty Ids --------------------------------------------------------------
        String url = "http://65.2.3.41:8080/speciality?patient_id=" + id;
        Log.i("URL: ", url);

        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response1) {
                try {
                    JSONArray jsonArray1 = response1.getJSONArray("data");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject data1 = jsonArray1.getJSONObject(i);
                        specialty_id_all = data1.getString("id");
                        specialty_name = data1.getString("name");
//                        Log.i("all specialties:", specialty_id_all);
                        spc_array.add(specialty_id_all);
                        spc_name.add(specialty_name);
                    }
//                    Log.i("array of all specialty", String.valueOf(spc_array));

                    //----------------------------get All provider details --------------------------------------------------------------
                    String url1 = "http://65.2.3.41:8080/provider?patient_id=" + id;
                    Log.i("URL: ", url1);

                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response1) {
                            try {
                                JSONArray jsonArray1 = response1.getJSONArray("data");
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject data1 = jsonArray1.getJSONObject(i);
                                    specialty_id = data1.getString("speciality_id");
                                    provider_name = data1.getString("name");
//                                    Log.i("selected specialty ID: ", specialty_id);
                                    arrayList.add(specialty_id);
                                    pro_name.add(provider_name);
                                }

                                //------------------check for similar ids---------------------------
                                int size1 = arrayList.size();
                                Log.i("size of array selected:", String.valueOf(size1));
                                int size2 = spc_array.size();
                                Log.i("size of array all: ", String.valueOf(size2));
                                for (int i = 0; i < size2; i++) {
                                    for (int j = 0; j < size1; j++) {
//                                        Log.i("All specialty in for: ", spc_array.get(j));
//                                        Log.i("specialty in for loop: ", arrayList.get(j));
                                        if (arrayList.get(j).equals(spc_array.get(i))) {
                                            data.add(spc_name.get(i));
                                        }
                                    }
                                }
                                int size3 = data.size();
                                Log.i("array of selected spc: ", String.valueOf(data));
                                Collections.sort(data);
                                for (int i = 0; i < size3 - 1; i++) {
                                    if (data.get(i) != data.get(i + 1)) {
                                        temp.add(data.get(i));
                                    }
                                }
                                //------------------------------------------------------------------

                                //----------------------------select_specialty----------------------------------------------

                                txt1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog = new Dialog(SearchBySpecialistActivity.this);
                                        dialog.setContentView(R.layout.dialog_selected_specialty);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                        WindowManager.LayoutParams lp5 = new WindowManager.LayoutParams();
//                                        lp5.copyFrom(dialog.getWindow().getAttributes());
//                                        lp5.width = WindowManager.LayoutParams.MATCH_PARENT;
//                                        lp5.height = WindowManager.LayoutParams.MATCH_PARENT;
//                                        lp5.gravity = Gravity.CENTER;

                                        dialog.show();
//                                        dialog.getWindow().setAttributes(lp5);

                                        ListView listView = dialog.findViewById(R.id.selected_list);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchBySpecialistActivity.this, android.R.layout.simple_list_item_1, temp);
                                        listView.setAdapter(adapter);

                                        dialog2 = new Dialog(SearchBySpecialistActivity.this);
                                        dialog2.setContentView(R.layout.dialog_selected_provider);
                                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        ListView listView2 = dialog2.findViewById(R.id.selected_provider_list);
                                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(SearchBySpecialistActivity.this, android.R.layout.simple_list_item_1, txt2_array);
                                        listView2.setAdapter(adapter2);

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                txt1.setText(adapter.getItem(position));
                                                Log.i("List1 SpecialtyName", adapter.getItem(position));

                                                txt1.setTextColor(getResources().getColor(R.color.selected_blue));
                                                txt2.setVisibility(View.VISIBLE);
                                                pro.setVisibility(View.VISIBLE);
                                                selected_spc_name = txt1.getText().toString();

                                                //---------------get specialty id of selected specialty ------------
                                                for (int i = 0; i < size2; i++) {
                                                    if (selected_spc_name.equals(spc_name.get(i))) {
                                                        selected_spc_id = spc_array.get(i);
//                                                        Log.i("selected spc Id: ", selected_spc_id);
                                                    }
                                                }
                                                //------------------------------------------------------------------

                                                //-----------get provider name according to spc_id------------------
                                                txt2_array.clear();
                                                for (int i = 0; i < size1; i++) {
                                                    if (selected_spc_id.equals(arrayList.get(i))) {
                                                        txt2_array.add(pro_name.get(i));
                                                    }
                                                }

                                                int count = adapter2.getCount();
                                                if (count == 1) {
                                                    txt2.setText(adapter2.getItem(0));
                                                    providerName = adapter2.getItem(0);
                                                    Log.i("Provider Name", providerName);
                                                    txt2.setTextColor(getResources().getColor(R.color.selected_blue));
                                                    continue1.setTextColor(getResources().getColor(R.color.active));
                                                } else if (count > 1) {
                                                    //---------------------------------select_provider------------------------------------------
                                                    txt2.setText("");
                                                    continue1.setTextColor(getResources().getColor(R.color.de_active));

                                                    dialog2 = new Dialog(SearchBySpecialistActivity.this);
                                                    dialog2.setContentView(R.layout.dialog_selected_provider);
                                                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                                    WindowManager.LayoutParams lp6 = new WindowManager.LayoutParams();
//                                                    lp6.copyFrom(dialog2.getWindow().getAttributes());
//                                                    lp6.width = WindowManager.LayoutParams.MATCH_PARENT;
//                                                    lp6.height = WindowManager.LayoutParams.MATCH_PARENT;
//                                                    lp6.gravity = Gravity.CENTER;

                                                    dialog2.show();
//                                                    dialog2.getWindow().setAttributes(lp6);

                                                    ListView listView2 = dialog2.findViewById(R.id.selected_provider_list);
                                                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(SearchBySpecialistActivity.this, android.R.layout.simple_list_item_1, txt2_array);
                                                    listView2.setAdapter(adapter2);

                                                    int count2 = adapter2.getCount();
                                                    Log.i("count2 ", String.valueOf(count2));

                                                    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            txt2.setText(adapter2.getItem(position));
                                                            providerName = adapter2.getItem(position);
                                                            Log.i("Provider Name", providerName);
                                                            txt2.setTextColor(getResources().getColor(R.color.selected_blue));
                                                            continue1.setTextColor(getResources().getColor(R.color.active));
                                                            dialog2.dismiss();
                                                        }
                                                    });

                                                    txt2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog2 = new Dialog(SearchBySpecialistActivity.this);
                                                            dialog2.setContentView(R.layout.dialog_selected_provider);
                                                            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            WindowManager.LayoutParams lp6 = new WindowManager.LayoutParams();
                                                            lp6.copyFrom(dialog2.getWindow().getAttributes());
                                                            lp6.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                            lp6.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                            lp6.gravity = Gravity.CENTER;

                                                            dialog2.show();
                                                            dialog2.getWindow().setAttributes(lp6);

                                                            ListView listView2 = dialog2.findViewById(R.id.selected_provider_list);
                                                            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(SearchBySpecialistActivity.this, android.R.layout.simple_list_item_1, txt2_array);
                                                            listView2.setAdapter(adapter2);

                                                            int count2 = adapter2.getCount();
                                                            Log.i("count2 ", String.valueOf(count2));

                                                            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                    txt2.setText(adapter2.getItem(position));
                                                                    providerName = adapter2.getItem(position);
                                                                    Log.i("Provider Name", providerName);
                                                                    txt2.setTextColor(getResources().getColor(R.color.selected_blue));
                                                                    continue1.setTextColor(getResources().getColor(R.color.active));
                                                                    dialog2.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });
                                                    //------------------------------------------------------------------------------------------
                                                }
                                                //------------------------------------------------------------------
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });

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
//--------------------------------------------------------------------------------------------------

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
        mQueue.add(request2);


        int lp = txt1.getText().length();
        int tp = txt2.getText().length();

        if (lp == 0 && tp == 0) {
            continue1.setTextColor(getResources().getColor(R.color.de_active));
        }

        continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String specialtyName = txt1.getText().toString();
                String provider = txt2.getText().toString();

                String countSpec = txt1.getText().toString().trim();
                String countProv = txt2.getText().toString().trim();

                if (!countSpec.isEmpty() && !countProv.isEmpty()) {
                    Intent i2 = new Intent(SearchBySpecialistActivity.this, ResultForSpecialtyActivity.class);
                    i2.putExtra("specialty", specialtyName);
                    i2.putExtra("provider", provider);
                    i2.putExtra("id11", id);
                    i2.putExtra("name10", name);
                    startActivity(i2);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });
    }

    public void title_back(View view) {
        Intent i1 = new Intent(SearchBySpecialistActivity.this, SearchActivity.class);
        i1.putExtra("id3", id);
        i1.putExtra("name", name);
        startActivity(i1);
        overridePendingTransition(0, 0);
        finish();
    }
}