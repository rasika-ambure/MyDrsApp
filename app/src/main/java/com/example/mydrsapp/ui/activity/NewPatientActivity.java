package com.example.mydrsapp.ui.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mydrsapp.R;
import com.example.mydrsapp.model.UserUpdate;
import com.example.mydrsapp.services.UserService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewPatientActivity extends AppCompatActivity {
    EditText first_name;
    String patient_id, patient_name;
    Button b2;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        first_name =findViewById(R.id.l_ed1);
        b2=findViewById(R.id.continue_new_patient);

        first_name.addTextChangedListener(ContinueTextWatcher);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://65.2.3.41:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);

        Bundle bundle1 = getIntent().getExtras();
        if (bundle1!=null){
            patient_id = bundle1.getString("id");
            Log.i("Patient ID from NO: ", patient_id);
        }

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatePatient();
                AlertDialog.Builder myAlert2 = new AlertDialog.Builder(NewPatientActivity.this);
                myAlert2.setTitle("Saved Successfully");
                myAlert2.setMessage("Patient Name: " + first_name.getText().toString());

                patient_name = first_name.getText().toString();

                myAlert2.setNegativeButton("Edit Patient Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                myAlert2.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent i2 = new Intent(NewPatientActivity.this, NewProviderActivity.class);
                        i2.putExtra("id1", patient_id);
                        i2.putExtra("name", patient_name);
                        i2.putExtra("bottom_nav", false);
                        startActivity(i2);
                        finish();
                    }
                });
                myAlert2.setCancelable(false);
                myAlert2.show();
            }
        });
    }

    private TextWatcher ContinueTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String editInput = first_name.getText().toString().trim();
            b2.setEnabled(!editInput.isEmpty());
            if(editInput.length()==0){
                b2.setTextColor(Color.parseColor("#868E96"));
            }
            else {
                b2.setTextColor(Color.parseColor("#0272B9"));
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void updatePatient() {
        UserUpdate userUpdate = new UserUpdate(""+patient_id, Objects.requireNonNull(first_name.getText()).toString(),  false, "paid"  );
        Call<UserUpdate> call = userService.putPost(""+patient_id, userUpdate);
        call.enqueue(new Callback<UserUpdate>() {
            @Override
            public void onResponse(Call<UserUpdate> call, Response<UserUpdate> response) {
            }
            @Override
            public void onFailure(Call<UserUpdate> call, Throwable t) {
            }
        });
    }
}