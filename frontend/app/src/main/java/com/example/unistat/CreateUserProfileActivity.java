package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.StatsCardView.ViewStatsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateUserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private TextInputLayout userUnivName;
    private TextInputLayout userUnivMajor;
    private TextInputLayout userUnivGpa;
    private TextInputLayout userUnivEntranceScore;
    private TextInputLayout userUnivBio;
    private FloatingActionButton nextButton;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);

        userUnivName = findViewById(R.id.univNameInput);
        userUnivMajor = findViewById(R.id.univMajorInput);
        userUnivGpa = findViewById(R.id.univGpaInput);
        userUnivEntranceScore = findViewById(R.id.univEntranceScoreInput);
        userUnivBio = findViewById(R.id.univUserBio);

        account = GoogleSignIn.getLastSignedInAccount(this);

        nextButton = findViewById(R.id.nextUserProfileButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(userUnivName.getEditText().getText())
                        || TextUtils.isEmpty(userUnivMajor.getEditText().getText())
                        || TextUtils.isEmpty(userUnivGpa.getEditText().getText())
                        || TextUtils.isEmpty(userUnivEntranceScore.getEditText().getText())
                        || TextUtils.isEmpty(userUnivBio.getEditText().getText())) {
                    Toast.makeText(CreateUserProfileActivity.this, "All fields need to filled before continuing...", Toast.LENGTH_LONG).show();
                } else {
                    createStatInDB();
//                    Intent openSignOut = new Intent(UserProfileActivity.this, SignOutActivity.class);
                    Intent openViewStats = new Intent(CreateUserProfileActivity.this, ViewStatsActivity.class);
                    startActivity(openViewStats);
                }
            }
        });
    }

    private void createStatInDB() {
        RequestQueue requestQueue = Volley.newRequestQueue(CreateUserProfileActivity.this);
        String URL = "http://10.0.2.2:8081/stats";

        JSONObject body = new JSONObject();

        try {
            body.put("userEmail", account.getEmail());
            body.put("userPhoto", account.getPhotoUrl());
            body.put("userName", account.getDisplayName());
            body.put("univName", userUnivName.getEditText().getText());
            body.put("univMajor", userUnivMajor.getEditText().getText());
            body.put("univGpa", userUnivGpa.getEditText().getText());
            body.put("univEntranceScore", userUnivEntranceScore.getEditText().getText());
            body.put("univBio", userUnivBio.getEditText().getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postUserStatRequest = new JsonObjectRequest(
                Request.Method.POST,
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

        requestQueue.add(postUserStatRequest);
    }
}