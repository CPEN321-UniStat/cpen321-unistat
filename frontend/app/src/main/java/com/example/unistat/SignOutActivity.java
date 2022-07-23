package com.example.unistat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.pushnotifications.PushNotifications;
import com.example.unistat.statscardview.ViewStatsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class SignOutActivity extends AppCompatActivity {

    private static final String TAG = "signOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_Light);
        }
        else{
            setTheme(R.style.Theme_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        Button viewProfileButton = findViewById(R.id.view_profile_button);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignOutActivity.this, UserProfileActivity.class));
            }
        });

//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
//                if(isChecked){
//                    Log.d(TAG, "onChecked");
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                }
//                else{
//                    Log.d(TAG, "onUnchecked");
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                }
//                finish();
//                startActivity(new Intent(SignOutActivity.this, SignOutActivity.this.getClass()));
//                overridePendingTransition(0,0);
//            }
//        });
        //toggle switch for dark and light mode
//        toggleButton = findViewById(R.id.dark_light_mode_switch);


        //switchmaterial
        Button toggleButton = findViewById(R.id.dark_mode_button);

//        toggleButton.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                new Handler().post(new Runnable() {

                    @Override
                    public void run()
                    {
                        overridePendingTransition(0, 0);
                        finish();

                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                    }
                });
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.sign_out_activity);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.calendar_activity:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.view_stats_activity:
                        startActivity(new Intent(getApplicationContext(), ViewStatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.sign_out_activity:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void signOut() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "logged out");
                        Intent openMainActivity = new Intent(SignOutActivity.this, MainActivity.class);
                        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // clear firebase_token for correct notification behavior
                        updateRegistrationToken("", userEmail);

                        startActivity(openMainActivity);
                    }
                });
    }

    private void updateRegistrationToken(String token, String email) {
        //send firebase_token to users collection in DB
        //check if exists then replace/add update
        RequestQueue requestQueue = Volley.newRequestQueue(SignOutActivity.this);

        String URL = IpConstants.URL + "firebaseToken";

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("firebase_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateTokenRequest = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Server error: " + error);
                    }
                }
        );

        requestQueue.add(updateTokenRequest);
    }


    @Override
    public void onBackPressed() {
        Boolean shouldAllowBack = false;
        if (shouldAllowBack) {
            super.onBackPressed();
        }  //

    }

}





