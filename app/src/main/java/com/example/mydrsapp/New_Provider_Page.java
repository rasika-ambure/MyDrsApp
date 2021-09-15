package com.example.mydrsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class New_Provider_Page extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText edit_f_name, edit_l_name; // editText on provider page
    TextView t1, t2; // textView on provider page
    ArrayList<String> all_spc_id, selected_spc_array, arrayList, arrayList2, arrayList3, specialties;
    Dialog dialog, dialog2, dialog3;
    Button btn_save;
    EditText editText12, editText13; //editText on add specialty and cred screen
    String providerFName, providerLName, name, code, selected_spc, selected_cred, specialty_id, patient_id, pro_id, pro_code, pro_name, specialty_id1, spc_name, cred_name, patient_name;
    private RequestQueue mQueue;
    private UserService userService;
    BottomNavigationView bottomNavigationView;
    boolean bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_page);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            patient_id = bundle.getString("id1");
            Log.i("Patient ID On Provider", patient_id);
            patient_name = bundle.getString("name");
            Log.i("patient name on New pro", patient_name);
            bottom_nav = bundle.getBoolean("bottom_nav");
        }

        //------------------------------------handle bottom navigation------------------------------
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.add_dr);
        bottomNavigationView.setItemIconTintList(null);

        if (bottom_nav){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else {
            bottomNavigationView.setVisibility(View.GONE);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.search:
                        Intent i1 = new Intent(New_Provider_Page.this, Search.class);
                        i1.putExtra("id3", patient_id);
                        i1.putExtra("name", patient_name);
                        startActivity(i1);
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_dr:
                        return true;

                    case R.id.record:
                        Intent i = new Intent(New_Provider_Page.this, Record.class);
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

        t1 = findViewById(R.id.spc_txt);
        t2 = findViewById(R.id.cred_txt);

        edit_f_name = findViewById(R.id.f_ed1); // provider's first name edit text
        edit_l_name = findViewById(R.id.l_ed1); //provider's last name edit text

        btn_save = findViewById(R.id.btn); // save button.

        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        specialties = new ArrayList<>();
        selected_spc_array = new ArrayList<>();
        all_spc_id = new ArrayList<>();

//        edit_f_name.addTextChangedListener(LoginTextWatcher2);
        edit_f_name.addTextChangedListener(LoginTextWatcher);
        edit_l_name.addTextChangedListener(LoginTextWatcher);
        t1.addTextChangedListener(LoginTextWatcher);
        t2.addTextChangedListener(LoginTextWatcher);

        dialog3 = new Dialog(New_Provider_Page.this);
        dialog3.setContentView(R.layout.dialog2_spinner);

        mQueue = Volley.newRequestQueue(New_Provider_Page.this);


        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null){
            providerFName = bundle1.getString("proName");
            edit_f_name.setText(providerFName);
            providerLName = bundle1.getString("proLName");
            edit_l_name.setText(providerLName);

            Log.i("",providerFName+" and " + providerLName);
        }else{

        }

        //----------------update-api--------------------------------------------
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://65.2.3.41:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);
        //-----------------------------------------------------------------------

        //----------------------------get All specialties --------------------------------------------------------------
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
                        code = data1.getString("code");
                        specialty_id = data1.getString("id");
                        arrayList.add(name);
                        specialties.add(name);
                        all_spc_id.add(specialty_id);
                        arrayList2.add(code);

                        Log.i("specialty_name: " + i, name);
                        Log.i("specialty_code: ", code);
                        Log.i("specialty_id: ", specialty_id);
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
        //------------------------------------------------------------------------------------------------------------------

        //------------------------------save button---------------------------------------------------------------------
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String editInput = edit_f_name.getText().toString().trim();  //New Provider First Name get String
                String editInput1 = edit_l_name.getText().toString().trim(); //New Provider Last Name get String
                String textInput1 = t1.getText().toString().trim(); //Select Specialty get String
                String textInput2 = t2.getText().toString().trim(); //Select Credentials get String

                if (!editInput.isEmpty() && !editInput1.isEmpty() && !textInput2.isEmpty() && !textInput1.isEmpty()) {

                    //----------------------------------------------------------------------------------------------------------------
                    saveUser2(createRequest2());
                    //---------------------------get all provider details-----------------------------------------------------
                    String url2 = "http://65.2.3.41:8080/provider?patient_id=" + patient_id;
                    JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response2) {
                            try {
                                JSONArray jsonArray2 = response2.getJSONArray("data");
                                for (int i = 0; i < jsonArray2.length(); i++) {
                                    JSONObject data2 = jsonArray2.getJSONObject(i);
                                    pro_id = data2.getString("id");
                                    pro_code = data2.getString("code");
                                    pro_name = data2.getString("name");
                                    Log.i("Provider ID: ", pro_id);
                                    Log.i("Provider Code: ", pro_code);
                                    Log.i("Provider Name: ", pro_name);
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
                    mQueue.add(request2);
                    //------------------------------------------------------------------------------


                    //----------------------saved successfully popup-------------------------------------------------------
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(New_Provider_Page.this);
                    myAlert.setTitle("Saved successfully");
                    myAlert.setMessage("Provider:\n" + "Dr. " + edit_f_name.getText().toString() + " " + edit_l_name.getText().toString() + ", " + t1.getText().toString());
                    myAlert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateSetupDone();
                            if (bottom_nav) {
                                Intent i = new Intent(New_Provider_Page.this, Record.class);
                                i.putExtra("id2", patient_id);
                                i.putExtra("name", patient_name);
                                startActivity(i);
                                overridePendingTransition(0,0);
                                finish();
                            }else {
                                Intent i2 = new Intent(New_Provider_Page.this, Setup_complete.class);
                                i2.putExtra("id", patient_id);
                                i2.putExtra("name", patient_name);
                                startActivity(i2);
                                finish();
                            }
                        }
                    });
                    myAlert.setNegativeButton("Add Another Provider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            edit_f_name.setSelection(0);
                            edit_f_name.setText("");
                            edit_l_name.setText("");
                            t1.setText("");
                            t2.setText("");
                        }
                    });
                    myAlert.setCancelable(false);
                    myAlert.show();
                    //-------------------------------------------------------------------------------------------------------

                }
            }
        });

        //------------------------------------------text1 on click--------------------------------------------------------------
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String editInput = edit_f_name.getText().toString().trim();  //New Provider First Name get String
                String editInput1 = edit_l_name.getText().toString().trim(); //New Provider Last Name get String

                if (!editInput.isEmpty() && !editInput1.isEmpty()) {

                    //-----------------------Specialty list dialog box--------------------------------------
                    dialog = new Dialog(New_Provider_Page.this);
                    dialog.setContentView(R.layout.dialog_spinner);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    EditText search_edit = dialog.findViewById(R.id.edit_text); // edittext to search speciality in dialog box
                    Button btn_add = dialog.findViewById(R.id.btn_add); // Button to open dialog to add new speciality.

                    //---------------------------cred- list dialog box----------------------------------
                    dialog2 = new Dialog(New_Provider_Page.this);
                    dialog2.setContentView(R.layout.dialog_spinner_credential);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //----------------------------------------------------------------------------------

                    ListView listView = dialog.findViewById(R.id.list_view); // List to select speciality.
                    ListView listView1 = dialog2.findViewById(R.id.list_view1);
                    ListView listView2 = dialog2.findViewById(R.id.list_view2);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(New_Provider_Page.this, android.R.layout.simple_list_item_1, arrayList);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(New_Provider_Page.this, android.R.layout.simple_list_item_1, arrayList2);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(New_Provider_Page.this, android.R.layout.simple_list_item_1, arrayList3);

                    //-------------------------------Add specialty button -------------------------------------

                    Collections.sort(arrayList);

                    btn_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //---------------------add specialty and cred dialog box------------------------------
                            dialog3 = new Dialog(New_Provider_Page.this);
                            dialog3.setContentView(R.layout.dialog2_spinner);
                            dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog3.show();
                            //------------------------------------------------------------------------------------

                            Button btn1 = dialog3.findViewById(R.id.btn_add2); // add button in add speciality screen
                            Button btn2 = dialog3.findViewById(R.id.can_btn); // cancel button

                            //---------------------------add button in add specialty dialog box---------------------------
                            btn1.setOnClickListener(new View.OnClickListener() {    //add button from dialog3
                                @Override
                                public void onClick(View v) {

                                    editText12 = dialog3.findViewById(R.id.spec_add);
                                    editText13 = dialog3.findViewById(R.id.cred_add);

                                    spc_name = editText12.getText().toString();
                                    cred_name = editText13.getText().toString();

                                    if (!spc_name.isEmpty() && !cred_name.isEmpty()) {

                                        saveUser3(createRequest3(), new OnSaveSpecialty() {
                                            @Override
                                            public void onComplete(SpecialtyResponse specialtyResponse) {
                                                String spc_msg1 = specialtyResponse.getMessage();
                                                Log.i("spc msg on save: ", spc_msg1);

                                                String spcMsg = "speciality created!";
                                                if (spc_msg1.equals(spcMsg)) {

                                                    arrayList.add(spc_name);
                                                    specialties.add(spc_name);
                                                    Collections.sort(arrayList);
                                                    adapter.notifyDataSetChanged();
                                                    listView.setAdapter(adapter);

                                                    Arrays.sort(new ArrayList[]{arrayList2});
                                                    arrayList2.add(cred_name);
                                                    adapter1.notifyDataSetChanged();
                                                    listView1.setAdapter(adapter1);

                                                    //-------------------------alert for added successfully-----------------------------------
                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(New_Provider_Page.this);
                                                    myAlert.setTitle("Added successfully");
                                                    myAlert.setMessage("Speciality: " + spc_name + "\nCredentials: " + cred_name);
                                                    myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog3.dismiss();
                                                            Intent intent90 = new Intent(New_Provider_Page.this, New_Provider_Page.class);
                                                            intent90.putExtra("id1", patient_id);
                                                            intent90.putExtra("name", patient_name);
                                                            intent90.putExtra("bottom_nav", bottom_nav);
                                                            intent90.putExtra("proName", edit_f_name.getText().toString());
                                                            intent90.putExtra("proLName", edit_l_name.getText().toString());
                                                            startActivity(intent90);
                                                            overridePendingTransition(0, 0);
                                                            finish();
                                                        }
                                                    });
                                                    myAlert.setCancelable(false);
                                                    myAlert.show();
                                                    dialog.dismiss();
                                                    dialog3.setCancelable(false);
                                                    Log.i("last added spc updated?", String.valueOf(arrayList));

                                                    //-----------------------------------------------------------------------------------------

                                                } else {

                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(New_Provider_Page.this);
                                                    myAlert.setTitle("Error");
                                                    myAlert.setMessage("Already exists.");
                                                    myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog3.dismiss();
                                                        }
                                                    });
                                                    myAlert.setCancelable(false);
                                                    myAlert.show();
                                                    dialog.dismiss();
                                                    dialog3.setCancelable(false);
                                                }
                                            }
                                        });

                                        //new added specialty ID will create here


                                        //----------------------------get All specialties --------------------------------------------------------------
                                        String url1 = "http://65.2.3.41:8080/speciality?patient_id=" + patient_id;
                                        Log.i("URL: ", url1);

                                        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1, null, new com.android.volley.Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response1) {
                                                try {
                                                    JSONArray jsonArray1 = response1.getJSONArray("data");
                                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                                        JSONObject data1 = jsonArray1.getJSONObject(i);
                                                        specialty_id = data1.getString("id");
                                                        all_spc_id.add(specialty_id);
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
                                        Log.i("all spc id onAdd", String.valueOf(all_spc_id));
                                        //------------------------------------------------------------------------------------------------------------------


                                    }
                                }
                            });
                            btn2.setOnClickListener(new View.OnClickListener() {     // cancel button
                                @Override
                                public void onClick(View v) {
                                    dialog3.dismiss();
                                }
                            });
                        }
                    });

                    listView.setAdapter(adapter);     //Update List
                    listView1.setAdapter(adapter1);   //Update List
                    listView2.setAdapter(adapter2);   //Update List

                    search_edit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    listView.setAdapter(adapter);     //Update List
                    listView1.setAdapter(adapter1);   //Update List
                    listView2.setAdapter(adapter2);   //Update List

                    //----------------------------item selected listener --------------------------------------
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            t1.setText(adapter.getItem(i));
                            selected_spc = t1.getText().toString();
                            Log.i("selected spc: ", selected_spc);



                            //---------------get credentials for selected specialty ------------
                            for (int j = 0; j < arrayList2.size(); j++) {
                                if (selected_spc.equals(specialties.get(j))) {
                                    selected_cred = arrayList2.get(j);
                                    specialty_id1 = all_spc_id.get(j);
                                    Log.i("Selected Credentials", selected_cred);
                                    Log.i("selected spc id: ", specialty_id1);
                                }
                            }
                            //------------------------------------------------------------------

                            String[] sep_cred = selected_cred.split(", ", 2);
                            arrayList3.clear();
                            for (String a : sep_cred) {
                                Log.i("Split Cred", a);
                                arrayList3.add(a);
                                t2.setText(adapter2.getItem(0));
                            }
                            dialog.dismiss();

////                            //------------------------------get selected specialty ID-----------------------------------------------------------
//                            String url3 = "http://65.2.3.41:8080/speciality?patient_id=" + patient_id;
//                            JsonObjectRequest request3 = new JsonObjectRequest(Request.Method.GET, url3, null, new com.android.volley.Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response3) {
//                                    try {
//                                        JSONArray jsonArray3 = response3.getJSONArray("data");
//                                        JSONObject data2 = jsonArray3.getJSONObject(i);
//                                        specialty_id1 = data2.getString("id");
//                                        Log.i("Selected specialty_id: ", specialty_id1);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }, new com.android.volley.Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    error.printStackTrace();
//                                }
//                            });
//                            mQueue.add(request3);
//                            //------------------------------------------------------------------------------------------------

                        }
                    });
                    listView.setAdapter(adapter);     //Update List
                    listView1.setAdapter(adapter1);   //Update List
                    listView2.setAdapter(adapter2);
                }
            }
        });

        //------------------------------------------------------------------------------------------------------------------

        //-------------------------------text2 on click action--------------------------------------------------------------
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String editInput = edit_f_name.getText().toString().trim();  //New Provider First Name get String
                String editInput1 = edit_l_name.getText().toString().trim(); //New Provider Last Name get String

                if (!editInput.isEmpty() && !editInput1.isEmpty()) {
                    //-----------------------------cred dialog box--------------------------------------
                    dialog2 = new Dialog(New_Provider_Page.this);
                    dialog2.setContentView(R.layout.dialog_spinner_credential);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog2.show();
                    //----------------------------------------------------------------------------------

                    ListView listView2 = dialog2.findViewById(R.id.list_view2);
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(New_Provider_Page.this, android.R.layout.simple_list_item_1, arrayList3);
                    listView2.setAdapter(adapter2);

                    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            t2.setText(adapter2.getItem(position));
                            dialog2.dismiss();
                        }
                    });
                }
            }
        });
        //-------------------------------------------------------------------------------------------------------------

    }

    //-----------------------provider Request post method-------------------------------------------
    public ProviderRequest createRequest2() {
        ProviderRequest providerRequest = new ProviderRequest();
        providerRequest.setName(edit_f_name.getText().toString());
        providerRequest.setCode(t2.getText().toString());
        providerRequest.setPatient_id(patient_id);
        Log.i("P_Id Create req2:", patient_id);
        providerRequest.setSpeciality_id(specialty_id1);
        return providerRequest;
    }
    //----------------------------------------------------------------------------------------------

    //--------------------------------Specialty request post ---------------------------------------
    public SpecialtyRequest createRequest3() {
        SpecialtyRequest specialtyRequest = new SpecialtyRequest();
        specialtyRequest.setName(editText12.getText().toString());
        specialtyRequest.setCode(editText13.getText().toString());
        specialtyRequest.setPatient_id(patient_id);
        Log.i("P_id onCreate req3:", patient_id);
        return specialtyRequest;
    }
    //----------------------------------------------------------------------------------------------

    //---------------------------------save provider request----------------------------------------
    public void saveUser2(ProviderRequest providerRequest) {
        Call<ProviderResponse> userResponseCall = ApiClient.getProviderService().saveUser2(providerRequest);
        userResponseCall.enqueue(new Callback<ProviderResponse>() {
            @Override
            public void onResponse(Call<ProviderResponse> call, Response<ProviderResponse> response) {
            }

            @Override
            public void onFailure(Call<ProviderResponse> call, Throwable t) {
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    //-----------------------------------save specialty request-------------------------------------
    public void saveUser3(SpecialtyRequest specialtyRequest, OnSaveSpecialty onSaveSpecialty) {
        Call<SpecialtyResponse> specialtyResponseCall = ApiClient.getSpecialtyService().saveUser3(specialtyRequest);
        specialtyResponseCall.enqueue(new Callback<SpecialtyResponse>() {
            @Override
            public void onResponse(Call<SpecialtyResponse> call, Response<SpecialtyResponse> response) {
                SpecialtyResponse specialtyResponse = response.body();
                onSaveSpecialty.onComplete(specialtyResponse);
            }

            @Override
            public void onFailure(Call<SpecialtyResponse> call, Throwable t) {
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //----------------------------- Text Watcher Continue ----------------------------------------------
    private TextWatcher LoginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

//            String upperString = edit_f_name.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();

            String editInput = edit_f_name.getText().toString().trim();  //New Provider First Name get String
            String editInput1 = edit_l_name.getText().toString().trim(); //New Provider Last Name get String
            String textInput1 = t1.getText().toString().trim(); //Select Specialty get String
            String textInput2 = t2.getText().toString().trim(); //Select Credentials get String

            btn_save.setEnabled(!editInput.isEmpty() && !editInput1.isEmpty() && !textInput1.isEmpty() && !textInput2.isEmpty());

            //------------------ Change Button Color ------------------------------------------------
            if (!editInput.isEmpty() && !editInput1.isEmpty() && !textInput2.isEmpty() && !textInput1.isEmpty()) {
                btn_save.setTextColor(Color.parseColor("#0272B9"));
            } else {
                btn_save.setTextColor(Color.parseColor("#868E96"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //----------------------------------------------------------------------------------------------------

    //---------------------------------update user setup_done---------------------------------------
    private void updateSetupDone() {
        UserUpdate userUpdate = new UserUpdate("" + patient_id, patient_name, true, "paid");
        Call<UserUpdate> call = userService.putPost("" + patient_id, userUpdate);
        call.enqueue(new Callback<UserUpdate>() {
            @Override
            public void onResponse(Call<UserUpdate> call, retrofit2.Response<UserUpdate> response) {
            }

            @Override
            public void onFailure(Call<UserUpdate> call, Throwable t) {
            }
        });
    }
    //----------------------------------------------------------------------------------------------


}