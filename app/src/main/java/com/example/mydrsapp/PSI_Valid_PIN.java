package com.example.mydrsapp;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PSI_Valid_PIN extends AppCompatActivity {
    EditText e1;
    Button b1;
    Boolean pin_1;
    String patient_id;
    private UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psi_valid);
        e1 =findViewById(R.id.l_ed1);
        b1 =findViewById(R.id.btn1);

        e1.addTextChangedListener(loginTextWatcher);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://65.2.3.41:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            patient_id = bundle.getString("id");
            Log.i("Patient ID ON Valid", patient_id);
        }

        //--------------------------continue button---------------------------------------------------
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = ProgressDialog.show(PSI_Valid_PIN.this, "Loading", "Please wait", true);


                String editInput = e1.getText().toString().trim();

                //----------------------------get PSI valid or not?----------------------------------------------------
                RequestQueue queue = Volley.newRequestQueue(PSI_Valid_PIN.this);

                String url = "https://mydrsorders.patientservicesinc.org/api/VerifyPIN/"+editInput;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("data", "data..........." + response.toString());
                        try {
                            pin_1 = response.getBoolean("isValid");
                            Log.i("is valid: ", String.valueOf(pin_1));

                            progressDialog.dismiss();
                            if (pin_1){

                                //----------------------------alert for validating psi-----------------------------------------
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(PSI_Valid_PIN.this);
                                myAlert.setTitle("Thank You");
                                myAlert.setMessage("Thank you for validating your PSI portal PIN");
                                myAlert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent pro = new Intent(PSI_Valid_PIN.this, New_Patient.class);
                                        updateType();
                                        pro.putExtra("id", patient_id);
                                        startActivity(pro);
                                        finish();
                                    }
                                });
                                myAlert.setCancelable(false);
                                myAlert.show();
                            }else {
                                //-----------------------------alert for invalid psi----------------------------------
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(PSI_Valid_PIN.this);
                                myAlert.setTitle("Invalid PSI Portal PIN");
                                myAlert.setMessage("Please enter a valid PSI Portal PIN");
                                myAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                myAlert.setCancelable(false);
                                myAlert.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                            VolleyLog.wtf(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error", "error..........." + error.getMessage());
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String USERNAME = "Basic";
                        String PASSWORD = "e9416093e59d4bdbbb96ad8892177f6e";
                        Map<String, String> headers = new HashMap<String, String>();
                        // add headers <key,value>
                        String credentials = USERNAME + ":" + PASSWORD;
                        String auth = "Basic "
                                + Base64.encodeToString(credentials.getBytes(),
                                Base64.NO_WRAP);
                        headers.put("Authorization", auth);
                        return headers;
                    }
                };
                queue.add(jsonObjectRequest);
            }
        });
    }

    //--------------------------text watcher---------------------------------------------------------
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String editInput = e1.getText().toString().trim();
            b1.setEnabled(editInput.length()>5);
            if(editInput.length()<=5){
                b1.setTextColor(Color.parseColor("#868E96"));
            }
            else {
                b1.setTextColor(Color.parseColor("#0272B9"));
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    //----------------------------------------------------------------------------------------------

    //---------------------------------------update patient type to paid-----------------------------------------------
    private void updateType() {
        UserUpdate userUpdate = new UserUpdate(""+patient_id, null, false, "paid" );
        Call<UserUpdate> call = userService.putPost(""+patient_id, userUpdate);
        call.enqueue(new Callback<UserUpdate>() {
            @Override
            public void onResponse(Call<UserUpdate> call, retrofit2.Response<UserUpdate> response) {
            }
            @Override
            public void onFailure(Call<UserUpdate> call, Throwable t) {
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------


}

