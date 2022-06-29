package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private EditText userUnivName;
    private EditText userUnivMajor;
    private EditText userUnivGpa;
    private EditText userUnivEntranceScore;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userUnivName = findViewById(R.id.univNameInput);
        userUnivMajor = findViewById(R.id.univMajorInput);
        userUnivGpa = findViewById(R.id.univGpaInput);
        userUnivEntranceScore = findViewById(R.id.univEntranceScoreInput);

        nextButton = findViewById(R.id.nextUserProfileButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(userUnivName.getText())
                        || TextUtils.isEmpty(userUnivMajor.getText())
                        || TextUtils.isEmpty(userUnivGpa.getText())
                        || TextUtils.isEmpty(userUnivEntranceScore.getText())) {
                    Toast.makeText(UserProfileActivity.this, "All fields need to filled before continuing...", Toast.LENGTH_LONG).show();
                } else {
                    createStatInDB();
                    Intent openSignOut = new Intent(UserProfileActivity.this, SignOutActivity.class);
                    startActivity(openSignOut);
                }
            }
        });
    }

    private void createStatInDB() {
        RequestQueue requestQueue = Volley.newRequestQueue(UserProfileActivity.this);
        String URL = "http://10.0.2.2:8081/stats";

        JSONObject body = new JSONObject();
        Bundle bundle = getIntent().getExtras();
        String userEmailId = bundle.getString("userEmailId");
        try {
            body.put("userEmail", userEmailId);
            body.put("univName", userUnivName.getText());
            body.put("univMajor", userUnivMajor.getText());
            body.put("univGpa", userUnivGpa.getText());
            body.put("univEntranceScore", userUnivEntranceScore.getText());
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