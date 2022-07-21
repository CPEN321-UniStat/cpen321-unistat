package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private TextInputLayout editUserUnivName;
    private TextInputLayout editUserUnivMajor;
    private TextInputLayout editUserUnivGpa;
    private TextInputLayout editUserUnivEntranceScore;
    private TextInputLayout editUserBio;
    private String univName;
    private String univMajor;
    private String univGpa;
    private String univBio;
    private FloatingActionButton confirmChangesButton;
    private FloatingActionButton editProfileButton;
    private MaterialTextView coinsText;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editUserUnivName = findViewById(R.id.editUserUnivInput);
        editUserUnivMajor = findViewById(R.id.editUserMajorinput);
        editUserUnivGpa = findViewById(R.id.editUserGpaInput);
        editUserUnivEntranceScore = findViewById(R.id.editUserEntranceScoreinput);
        editUserBio = findViewById(R.id.editUserBioInput);

        editUserUnivName.setEnabled(false);
        editUserUnivMajor.setEnabled(false);
        editUserUnivGpa.setEnabled(false);
        editUserUnivEntranceScore.setEnabled(false);
        editUserBio.setEnabled(false);

        TextView userNameText = findViewById(R.id.userNameText);
        TextView userEmailText = findViewById(R.id.userEmailText);
        coinsText = findViewById(R.id.coins);
        CircleImageView userProfileImage = findViewById(R.id.userProfileImage);

        requestQueue = Volley.newRequestQueue(UserProfileActivity.this);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        Log.d(TAG, "User email: " + userEmail);
        getUserStats(userEmail);

        userNameText.setText(account.getDisplayName());
        String emailText = "Account for " + account.getEmail();
        userEmailText.setText(emailText);
        Picasso.get().load(account.getPhotoUrl()).resize(125, 125).into(userProfileImage);

        getCoinsByEmail(userEmail);

        confirmChangesButton = findViewById(R.id.confirmChangesButton);
        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserStats(userEmail);
                confirmChangesButton.setVisibility(View.GONE);
                editProfileButton.setVisibility(View.VISIBLE);
                editUserUnivName.setEnabled(false);
                editUserUnivMajor.setEnabled(false);
                editUserUnivGpa.setEnabled(false);
                editUserUnivEntranceScore.setEnabled(false);
                editUserBio.setEnabled(false);
            }
        });

        editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserUnivName.setEnabled(true);
                editUserUnivMajor.setEnabled(true);
                editUserUnivGpa.setEnabled(true);
                editUserUnivEntranceScore.setEnabled(true);
                editUserBio.setEnabled(true);
                editProfileButton.setVisibility(View.GONE);
                confirmChangesButton.setVisibility(View.VISIBLE);
            }
        });

    }

    private void updateUserStats(String userEmail) {
        String URL = IpConstants.URL + "stats";
        univName = editUserUnivName.getEditText().getText().toString().trim();
        univMajor = editUserUnivMajor.getEditText().getText().toString().trim();
        univGpa = editUserUnivGpa.getEditText().getText().toString().trim();
        String univEntranceScore = editUserUnivEntranceScore.getEditText().getText().toString().trim();
        univBio = editUserBio.getEditText().getText().toString().trim();

        Boolean allPassed = runChecks();

        if (!allPassed) {
            return;
        }
        
        JSONObject body = new JSONObject();
        try {
            body.put("userEmail", userEmail);
            body.put("univName", univName);
            body.put("univMajor", univMajor);
            body.put("univGpa", Double.parseDouble(univGpa));
            body.put("univEntranceScore", Integer.parseInt(univEntranceScore));
            body.put("univBio", univBio);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateUserStatRequest = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        Toast.makeText(UserProfileActivity.this, "Your Profile has been updated", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Server error: " + error);
                    }
                }
        );

        requestQueue.add(updateUserStatRequest);
    }

    private Boolean runChecks() {

        if (TextUtils.isEmpty(univName)
                || TextUtils.isEmpty(univMajor)
                || TextUtils.isEmpty(editUserUnivGpa.getEditText().getText().toString())
                || TextUtils.isEmpty(editUserUnivEntranceScore.getEditText().getText().toString())
                || TextUtils.isEmpty(univBio)) {
            Toast.makeText(UserProfileActivity.this, "All fields need to filled before continuing...", Toast.LENGTH_LONG).show();
            return false;
        } else if (!univName.matches("^[a-zA-Z ]*$")
                || !univMajor.matches("^[a-zA-Z ]*$")) {
            Toast.makeText(UserProfileActivity.this, "Please make sure your university name & major are valid.", Toast.LENGTH_LONG).show();
            return false;
        } else if (Double.parseDouble(univGpa) > 4.33) {
            Toast.makeText(UserProfileActivity.this, "Please make sure your GPA is valid.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }

    }

    private void getUserStats(String userEmail) {
        String URL = IpConstants.URL + "statsByFilter";

        JSONObject body = new JSONObject();
        try {
            body.put("userEmail", userEmail);
            Log.d(TAG, body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getUserStatRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        try {
                            JSONArray statArray = (JSONArray) response.get("statData");
                            JSONObject userStat;
                            if (statArray.length() <= 0) { // if mentee then not much to show
                                editUserUnivName.setVisibility(View.GONE);
                                editUserUnivMajor.setVisibility(View.GONE);
                                editUserUnivGpa.setVisibility(View.GONE);
                                editUserUnivEntranceScore.setVisibility(View.GONE);
                                editProfileButton.setVisibility(View.GONE);
                                editUserBio.setVisibility(View.GONE);
                                confirmChangesButton.setVisibility(View.GONE);
                            } else { // if mentor then show university stats
                                userStat = statArray.getJSONObject(0);
                                editUserUnivName.getEditText().setText((String) userStat.get("univName"), TextView.BufferType.EDITABLE);
                                editUserUnivMajor.getEditText().setText((String) userStat.get("univMajor"), TextView.BufferType.EDITABLE);
                                editUserUnivGpa.getEditText().setText(String.valueOf(userStat.get("univGpa")), TextView.BufferType.EDITABLE);
                                editUserUnivEntranceScore.getEditText().setText(String.valueOf(userStat.get("univEntranceScore")), TextView.BufferType.EDITABLE);
                                editUserBio.getEditText().setText((String) userStat.get("univBio"), TextView.BufferType.EDITABLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Server error: " + error);
                    }
                }
        );

        requestQueue.add(getUserStatRequest);
    }

    private void getCoinsByEmail(String userEmail) {
        String URL = IpConstants.URL + "coinsByUser";

        JSONObject body = new JSONObject();
        try {
            body.put("userEmail", userEmail);
            Log.d(TAG, body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getCoinsRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        try {
                            String coins = response.getString("coins");
                            coinsText.setText(coins);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Server error: " + error);
                    }
                }
        );

        requestQueue.add(getCoinsRequest);
    }
}