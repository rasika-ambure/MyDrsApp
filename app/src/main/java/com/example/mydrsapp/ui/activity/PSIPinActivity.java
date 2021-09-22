package com.example.mydrsapp.ui.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mydrsapp.R;
import com.example.mydrsapp.model.TypeUpdate;
import com.example.mydrsapp.model.UserUpdate;
import com.example.mydrsapp.services.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PSIPinActivity extends AppCompatActivity {
    EditText e1;
    Button b1;
    Boolean pin_1;
    Dialog dialog3;
    String patient_id, patient_name;
    private UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psi_valid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(Color.parseColor("#FDE583"));
        }

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
            patient_name = bundle.getString("name");
            Log.i("Patient name ON Valid", patient_name);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        e1.requestFocus();

        //--------------------------continue button---------------------------------------------------
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = ProgressDialog.show(PSIPinActivity.this, "Loading", "Please wait", true);
                String editInput = e1.getText().toString().trim();

                //----------------------------get PSI valid or not?----------------------------------------------------
                RequestQueue queue = Volley.newRequestQueue(PSIPinActivity.this);

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
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(PSIPinActivity.this);
                                myAlert.setTitle("Thank You");
                                myAlert.setMessage("Thank you for validating your PSI portal PIN");
                                myAlert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateType();
                                        if (patient_name.length()==0){
                                            Intent pro = new Intent(PSIPinActivity.this, NewPatientActivity.class);
                                            pro.putExtra("id", patient_id);
                                            startActivity(pro);
                                            finish();
                                        }else {
                                            Intent i = new Intent(PSIPinActivity.this, RecordActivity.class);
                                            i.putExtra("id2", patient_id);
                                            i.putExtra("name", patient_name);
                                            startActivity(i);
//                                            overridePendingTransition(0, 0);
                                            finish();
                                        }
                                    }
                                });
                                myAlert.setCancelable(false);
                                myAlert.show();
                            }else {
                                //-----------------------------alert for invalid psi----------------------------------
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(PSIPinActivity.this);
                                myAlert.setTitle("Invalid PSI Portal PIN");
//                                myAlert.setMessage("Please enter a valid PSI Portal PIN");
                                if (patient_name.length()==0) {
                                    myAlert.setPositiveButton("Re-Enter PSI portal PIN", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    myAlert.setNegativeButton("Continue without PSI portal PIN", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent pro = new Intent(PSIPinActivity.this, NewPatientActivity.class);
                                            pro.putExtra("id", patient_id);
                                            startActivity(pro);
                                            finish();
                                        }
                                    });
                                    myAlert.setCancelable(false);
                                    myAlert.show();
                                }else{
                                    dialog3 = new Dialog(PSIPinActivity.this);
                                    dialog3.setContentView(R.layout.dialog_invalid);
                                    dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog3.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.gravity = Gravity.CENTER;
                                    dialog3.show();
                                    dialog3.getWindow().setAttributes(lp);
                                    dialog3.setCancelable(false);

                                    TextView reEnter = dialog3.findViewById(R.id.reEnter);
                                    TextView BuySub = dialog3.findViewById(R.id.BuySub);
                                    TextView continueWithoutPSI = dialog3.findViewById(R.id.ContinueWithoutPSI);

                                    reEnter.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog3.dismiss();
                                        }
                                    });

                                    BuySub.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(PSIPinActivity.this, SubscribeActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    });

                                    continueWithoutPSI.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(PSIPinActivity.this, RecordActivity.class);
                                            i.putExtra("id2", patient_id);
                                            i.putExtra("name", patient_name);
                                            startActivity(i);
//                                            overridePendingTransition(0, 0);
                                            finish();
                                        }
                                    });

                                }
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
        TypeUpdate typeUpdate = new TypeUpdate("paid");
        Call<TypeUpdate> call = userService.putType(patient_id, typeUpdate);
        call.enqueue(new Callback<TypeUpdate>() {
            @Override
            public void onResponse(Call<TypeUpdate> call, retrofit2.Response<TypeUpdate> response) {
            }
            @Override
            public void onFailure(Call<TypeUpdate> call, Throwable t) {
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------


}

