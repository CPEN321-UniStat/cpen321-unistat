package com.example.unistat.StatsCardView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.CalendarActivity;
import com.example.unistat.PushNotifications.PushNotifications;
import com.example.unistat.Meeting.Meeting;
import com.example.unistat.Meeting.MeetingLog;
import com.example.unistat.R;
import com.example.unistat.RequestMeeting;
import com.example.unistat.SignOutActivity;
import com.example.unistat.UserProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;

public class ViewStatsActivity extends AppCompatActivity {
    private static final String TAG = "ViewStatsActivity";
    private Boolean shouldAllowBack = false;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private ArrayList<StatsCards> statsList;
    private RequestQueue requestQueue;
    private AutoCompleteTextView filterAutoComplete;
    private Chip sortByGpa;
    private Chip sortByEntranceScore;
    private Boolean isSortGpa;
    private Boolean isSortEntranceScore;
    private String searchText;
    private ArrayList<String> filterOptions;
    private ArrayList<String> univNameStats;
    private ArrayList<String> univMajorStats;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

        // Initialize and Set up sorting chips
        isSortGpa = false;
        isSortEntranceScore = false;
        sortByGpa = findViewById(R.id.sortByGpaChip);
        sortByEntranceScore = findViewById(R.id.sortByEntranceScoreChip);

        sortByGpa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isSortGpa = true;

                    // If filter and sort or only sort
                    if (filterAutoComplete.getText().toString().length() > 0) {
                        getCardData("statsByConfiguration", true);
                    } else {
                        getCardData("statsBySorting", true);
                    }

                } else {
                    isSortGpa = false;

                    // If (no sort and only filter) or (no sort no filter)
                    if (filterAutoComplete.getText().toString().length() > 0) {
                        getCardData("statsByFilter", true);
                    } else if (!isSortEntranceScore) {
                        getCardData("stats", false);
                    }
                }
            }
        });

        sortByEntranceScore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isSortEntranceScore = true;

                    // If filter and sort or only sort
                    if (filterAutoComplete.getText().toString().length() > 0) {
                        getCardData("statsByConfiguration", true);
                    } else {
                        getCardData("statsBySorting", true);
                    }

                } else {
                    isSortEntranceScore = false;

                    // If (no sort and only filter) or (no sort no filter)
                    if (filterAutoComplete.getText().toString().length() > 0) {
                        getCardData("statsByFilter", true);
                    } else if (!isSortGpa) {
                        getCardData("stats", false);
                    }
                }
            }
        });


        // Initialize and Set up filter auto-complete
        univNameStats = new ArrayList<>();
        univMajorStats = new ArrayList<>();
        filterOptions = new ArrayList<>();
        adapter = new ArrayAdapter<>(ViewStatsActivity.this, R.layout.support_simple_spinner_dropdown_item, filterOptions);
        filterAutoComplete = findViewById(R.id.filterAutoComplete);
        filterAutoComplete.setDropDownVerticalOffset(10);
        filterAutoComplete.setAdapter(adapter);

        // Get data on the basis of item clicked
        filterAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchText = adapter.getItem(i);

                // Only filter no sort
                if (!isSortGpa && !isSortEntranceScore) {
                    getCardData("statsByFilter", true);
                }
                // Filter and sort
                if (isSortGpa || isSortEntranceScore) {
                    getCardData("statsByConfiguration", true);
                }
            }
        });

        // Get all data when no filter and no sort config
        filterAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // If filter deleted and no sort remaining
                if (editable.length() == 0 && !isSortGpa && !isSortEntranceScore) {
                    adapter = new ArrayAdapter<>(ViewStatsActivity.this, R.layout.support_simple_spinner_dropdown_item, filterOptions);
                    filterAutoComplete.setAdapter(adapter);
                    getCardData("stats", false);
                }

                // If filter deleted and only sort remaining
                if (editable.length() == 0 && (isSortGpa || isSortEntranceScore)) {
                    adapter = new ArrayAdapter<>(ViewStatsActivity.this, R.layout.support_simple_spinner_dropdown_item, filterOptions);
                    filterAutoComplete.setAdapter(adapter);
                    getCardData("statsBySorting", true);
                }
            }
        });

        requestQueue = Volley.newRequestQueue(ViewStatsActivity.this);
        initCardView();

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.view_stats_activity);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.calendar_activity:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.view_stats_activity:
                        return true;
                    case R.id.sign_out_activity:
                        startActivity(new Intent(getApplicationContext(), SignOutActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
                }
            });


        }

    private void initCardView() {
        recyclerView = findViewById(R.id.recycler_view_card_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statsList = new ArrayList<>();

        cardAdapter = new CardAdapter(this, statsList);
        recyclerView.setAdapter(cardAdapter);

        statsList.clear();
        univNameStats.clear();
        univMajorStats.clear();
        filterOptions.clear();
        getCardData("stats", false);
    }

    private void getCardData(String endPoint, Boolean isConfig) {

        //Pull from DB and store in statsList
        String URL = "http://10.0.2.2:8081/" + endPoint;

        int requestMethod = isConfig ? Request.Method.POST : Request.Method.GET;

        JSONObject body = new JSONObject();

        // Only Filter
        if (isConfig && (filterAutoComplete.getText().length() > 0) && !isSortGpa && !isSortEntranceScore) {
            try {
                if (univNameStats.contains(searchText)) {
                    body.put("univName", searchText);
                } else {
                    body.put("univMajor", searchText);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Filter and Sort
        if (isConfig && (filterAutoComplete.getText().length() > 0) && (isSortGpa || isSortEntranceScore)) {
            try {
                if (univNameStats.contains(searchText)) {
                    body.put("univName", searchText);
                } else {
                    body.put("univMajor", searchText);
                }
                if (isSortGpa) {
                    body.put("univGpa", "");
                } else {
                    body.put("univEntranceScore", "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Only Sort
        if (isConfig && (filterAutoComplete.getText().length() == 0) && (isSortGpa || isSortEntranceScore)) {
            try {
                if (isSortGpa) {
                    body.put("univGpa", "");
                } else {
                    body.put("univEntranceScore", "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Log.d(TAG, "filter req body: " + body.toString());

        JsonObjectRequest getUserStatRequest = new JsonObjectRequest(
                requestMethod,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());

                        // Clear all arraylists for updated values
                        statsList.clear();
                        univNameStats.clear();
                        univMajorStats.clear();
                        filterOptions.clear();

                        try {
                            JSONArray statArray = (JSONArray) response.get("statData");

                            for (int i = 0; i < statArray.length(); i++){
                                JSONObject userStat =  statArray.getJSONObject(i);
                                String univName = (String) userStat.get("univName");
                                String univMajor = (String) userStat.get("univMajor");
                                if (!univNameStats.contains(univName)) {
                                    univNameStats.add((String) userStat.get("univName"));
                                }
                                if (!univMajorStats.contains(univMajor)) {
                                    univMajorStats.add((String) userStat.get("univMajor"));
                                }
                                String userName = userStat.getString("userName");
                                String userEmail = userStat.getString("userEmail");
                                statsList.add(new StatsCards(userEmail, userName, (String) userStat.get("univName"), (String) userStat.get("univMajor"), (String) userStat.get("univGpa"), (String) userStat.get("univEntranceScore"), (String) userStat.get("univBio"), (String) userStat.get("userPhoto")));
                            }

                            filterOptions.addAll(univNameStats);
                            filterOptions.addAll(univMajorStats);

                            cardAdapter.notifyDataSetChanged();
                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "filter Options: " + filterOptions.toString());

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

    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        } else {
            //
        }
    }

}