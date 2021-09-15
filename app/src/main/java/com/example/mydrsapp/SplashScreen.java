package com.example.mydrsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    Animation rotate;
    ImageView image;
    String email_id, pid, name, msg;
    Boolean setup;
    public static int splashdelay = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        image = findViewById(R.id.img);
        image.setAnimation(rotate);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(SplashScreen.this);
                if (act == null) {
                    Intent i = new Intent(SplashScreen.this, SignIn.class);
                    startActivity(i);
                    finish();
                } else {
                    saveUser(createRequest());
                }
            }
        }, splashdelay);

        if (!isConnected(this)){
            AlertDialog.Builder myAlert = new AlertDialog.Builder(SplashScreen.this);
            myAlert.setTitle("No Internet Connection");
            myAlert.setMessage("Check your internet connectivity");
            myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            myAlert.setCancelable(false);
            myAlert.show();
        }

    }

    public UserRequest createRequest() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            email_id = account.getEmail();
        }
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail_id(email_id);
        return userRequest;
    }

    public void saveUser(UserRequest userRequest) {
        Call<UserResponse> userResponseCall = ApiClient.getUserService().saveUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                UserResponse userResponse = response.body();
                pid = userResponse.getData().getId();
                name = userResponse.getData().getFirst_name();
                setup = userResponse.getData().getSetup_done();
                msg = userResponse.getMessage();
                GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(SplashScreen.this);

                Log.i("msg on splash: ", msg);

                email_id = act.getEmail();

                if (!setup) {
                    if (name == null) {
                        Intent jump = new Intent(SplashScreen.this, PSI_Validation.class);
                        jump.putExtra("id", userResponse.getData().getId());
                        startActivity(jump);
                        finish();
                    } else {
                        Intent i2 = new Intent(SplashScreen.this, New_Provider_Page.class);
                        i2.putExtra("id1", userResponse.getData().getId());
                        i2.putExtra("name", userResponse.getData().getFirst_name());
                        i2.putExtra("bottom_nav", false);
                        startActivity(i2);
                        finish();
                    }
                } else {
                    Intent i = new Intent(SplashScreen.this, Record.class);
                    i.putExtra("id2", userResponse.getData().getId());
                    i.putExtra("name", userResponse.getData().getFirst_name());
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
            }
        });
    }

    private boolean isConnected(SplashScreen splashScreen){
        ConnectivityManager connectivityManager = (ConnectivityManager) splashScreen.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mob_data = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi != null && wifi.isConnected()) || (mob_data != null && mob_data.isConnected());
    }

}