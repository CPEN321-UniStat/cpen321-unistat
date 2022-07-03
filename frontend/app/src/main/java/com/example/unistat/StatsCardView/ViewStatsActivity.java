package com.example.unistat.StatsCardView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.CalendarActivity;
import com.example.unistat.R;
import com.example.unistat.SignOutActivity;
import com.example.unistat.UserProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewStatsActivity extends AppCompatActivity {
    private static final String TAG = "ViewStatsActivity";
    private Boolean shouldAllowBack = false;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private ArrayList<StatsCards> statsList;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

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

        getCardData();
    }

    private void getCardData() {

        //Pull from DB and store in statsList
        String URL = "http://10.0.2.2:8081/stats";

        JsonObjectRequest getUserStatRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        try {
                            JSONArray statArray = (JSONArray) response.get("statData");

                            for (int i = 0; i < statArray.length(); i++){
                                JSONObject userStat =  statArray.getJSONObject(i);
                                statsList.add(new StatsCards("Mentor Name Placeholder", (String) userStat.get("univName"), (String) userStat.get("univMajor"), (String) userStat.get("univGpa"), userStat.has("univEntranceScore") ? (String) userStat.get("univEntranceScore") : "", userStat.has("univBio") ? (String) userStat.get("univBio") : ""));
                            }

                            cardAdapter.notifyDataSetChanged();

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