package com.example.mydrsapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mydrsapp.utils.ApiClient;
import com.example.mydrsapp.R;
import com.example.mydrsapp.model.UserRequest;
import com.example.mydrsapp.model.UserResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    String email_id, pid, name, msg;
    Boolean setup;

    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(Color.parseColor("#0272B9"));
            getWindow().setStatusBarColor(Color.parseColor("#FDE583"));
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

            }
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            saveUser(createRequest());
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("message", e.toString());
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
                String msg1 = "logged in";

                if (msg.equals(msg1)) {
                    if (!setup) {
                        if (name == null) {
                            Intent jump = new Intent(SignInActivity.this, PSIValidationActivity.class);
                            jump.putExtra("id", userResponse.getData().getId());
                            startActivity(jump);
                            finish();
                        } else {
                            Intent i2 = new Intent(SignInActivity.this, NewProviderActivity.class);
                            i2.putExtra("id1", userResponse.getData().getId());
                            i2.putExtra("name", userResponse.getData().getFirst_name());
                            i2.putExtra("bottom_nav", false);
                            startActivity(i2);
                            finish();
                        }
                    } else {
                        Intent i = new Intent(SignInActivity.this, RecordActivity.class);
                        i.putExtra("id2", userResponse.getData().getId());
                        i.putExtra("name", userResponse.getData().getFirst_name());
                        startActivity(i);
                        finish();
                    }
                }else {
                    Intent jump = new Intent(SignInActivity.this, PSIValidationActivity.class);
                    jump.putExtra("id", userResponse.getData().getId());
                    startActivity(jump);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
            }
        });
    }

    public void Link(View view) {
        Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patientservicesinc.org/wp-content/uploads/2021/06/Privacy-Policy-MyDrsOrders.pdf"));
        startActivity(link);

    }

    public void link2(View view) {
        Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patientservicesinc.org/wp-content/uploads/2021/06/Terms-and-Conditions-MyDrsOrders.pdf"));
        startActivity(link);
    }
}