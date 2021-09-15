package com.example.mydrsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PSI_Validation extends AppCompatActivity {
//    Button b1;
//    GoogleSignInClient mGoogleSignInClient;
    String patient_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psi_validation);
        Bundle b2 = getIntent().getExtras();
        if (b2!=null){
            patient_id = b2.getString("id");
            Log.i("PatientID from SignIn: ", patient_id);
        }

//        b1 = (Button)findViewById(R.id.btn1);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//        }

    }
//    private void signOut() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(PSI_Validation.this,"Signed Out Successfully",Toast.LENGTH_LONG).show();
//                        Intent psi = new Intent(PSI_Validation.this, SignIn.class);
//                        startActivity(psi);
//                    }
//                });
//    }

//    public void onClick(View view) {
//        signOut();
//    }
    public void Yes(View view) {
        Intent psi = new Intent(PSI_Validation.this, PSI_Valid_PIN.class);
        psi.putExtra("id", patient_id);
        startActivity(psi);
//        finish();
    }

    public void NO(View view) {
        Intent psi = new Intent(PSI_Validation.this, New_Patient.class);
        psi.putExtra("id", patient_id);
        startActivity(psi);
//        finish();
    }

}

