package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.example.unistat.statscardview.ViewStatsActivity;
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
    private String univName;
    private String univMajor;
    private String univGpa;
    private String univEntranceScore;
    private String univBio;
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

        FloatingActionButton nextButton = findViewById(R.id.nextUserProfileButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                univName = userUnivName.getEditText().getText().toString().trim();
                univMajor = userUnivMajor.getEditText().getText().toString().trim();
                univGpa = userUnivGpa.getEditText().getText().toString().trim();
                univEntranceScore = userUnivEntranceScore.getEditText().getText().toString().trim();
                univBio = userUnivBio.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(univName)
                        || TextUtils.isEmpty(univMajor)
                        || TextUtils.isEmpty(univGpa)
                        || TextUtils.isEmpty(univEntranceScore)
                        || TextUtils.isEmpty(univBio)) {
                    Toast.makeText(CreateUserProfileActivity.this, "All fields need to be filled before continuing...", Toast.LENGTH_LONG).show();
                } else if (!univName.matches("^[a-zA-Z ]*$+")
                        || !univMajor.matches("^[a-zA-Z ]*$")) {
                    Toast.makeText(CreateUserProfileActivity.this, "Please make sure your university name & major are valid.", Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(univGpa) > 4.33) {
                    Toast.makeText(CreateUserProfileActivity.this, "Please make sure your GPA is valid.", Toast.LENGTH_LONG).show();
                } else {
                    createStatInDB();
                    Intent openViewStats = new Intent(CreateUserProfileActivity.this, ViewStatsActivity.class);
                    startActivity(openViewStats);
                }
            }
        });
    }

    private void createStatInDB() {
        RequestQueue requestQueue = Volley.newRequestQueue(CreateUserProfileActivity.this);
        String URL = IpConstants.URL + "stats";

        JSONObject body = new JSONObject();

        try {
            body.put("userEmail", account.getEmail());
            body.put("userPhoto", account.getPhotoUrl());
            body.put("userName", account.getDisplayName());
            body.put("univName", univName);
            body.put("univMajor", univMajor);
            body.put("univGpa", Double.parseDouble(String.valueOf(univGpa)));
            body.put("univEntranceScore", Integer.parseInt(String.valueOf(univEntranceScore)));
            body.put("univBio", univBio);
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