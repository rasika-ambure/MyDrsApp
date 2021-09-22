package com.example.mydrsapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mydrsapp.R;

public class PSIValidationActivity extends AppCompatActivity {
//    Button b1;
//    GoogleSignInClient mGoogleSignInClient;
    String patient_id, patient_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psi_validation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(Color.parseColor("#FDE583"));
        }

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
        Intent psi = new Intent(PSIValidationActivity.this, PSIPinActivity.class);
        psi.putExtra("id", patient_id);
        String name = "";
        psi.putExtra("name", name);
        startActivity(psi);
//        finish();
    }

    public void NO(View view) {
        Intent psi = new Intent(PSIValidationActivity.this, NewPatientActivity.class);
        psi.putExtra("id", patient_id);
        startActivity(psi);
//        finish();
    }

}

