package com.example.unistat.ui.stats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.classes.CardAdapter;
import com.example.unistat.classes.StatsCards;
import com.example.unistat.ui.calendar.CalendarActivity;
import com.example.unistat.classes.IpConstants;
import com.example.unistat.R;
import com.example.unistat.ui.settings.SettingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.perf.metrics.AddTrace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

public class ViewStatsActivity extends AppCompatActivity {
    private static final String TAG = "ViewStatsActivity";
    private CardAdapter cardAdapter;
    private ArrayList<StatsCards> statsList;
    private RequestQueue requestQueue;
    private TextInputLayout filterSearchBar;
    private AutoCompleteTextView filterAutoComplete;
    private Chip sortByGpa;
    private Chip sortByEntranceScore;
    private Boolean isSortGpa;
    private Boolean isSortEntranceScore;
    private String searchText;
    private Boolean isMentor = null;
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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sortByGpa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sortStatsByGpa(b);
            }
        });

        sortByEntranceScore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sortStatsByEntranceScore(b);
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

        filterAutoComplete.setEnabled(true);
        filterAutoComplete.setFocusable(false);
        filterAutoComplete.setFocusableInTouchMode(false);

        filterAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Edit Text is clicked");
                filterAutoComplete.setFocusable(true);
                filterAutoComplete.setFocusableInTouchMode(true);
                filterAutoComplete.setEnabled(true);
                filterAutoComplete.requestFocus();
            }
        });

        // Get data on the basis of item clicked
        filterAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onClick: filterAutoComplete");
                searchText = adapter.getItem(i);

                // Only filter no sort
                if (!isSortGpa && !isSortEntranceScore) {
                    getCardData("statsByFilter", Request.Method.POST);
                }
                // Filter and sort
                else {
                    getCardData("statsByConfiguration", Request.Method.POST);
                }
            }
        });

        // Get all data when no filter and no sort config
        filterAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, String.valueOf(i));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, String.valueOf(i));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // If filter deleted and no sort remaining
                if (editable.length() == 0 && !isSortGpa && !isSortEntranceScore) {
                    adapter = new ArrayAdapter<>(ViewStatsActivity.this, R.layout.support_simple_spinner_dropdown_item, filterOptions);
                    filterAutoComplete.setAdapter(adapter);
                    getCardData("stats", Request.Method.GET);
                }

                // If filter deleted and only sort remaining
                else if (editable.length() == 0) {
                    adapter = new ArrayAdapter<>(ViewStatsActivity.this, R.layout.support_simple_spinner_dropdown_item, filterOptions);
                    filterAutoComplete.setAdapter(adapter);
                    getCardData("statsBySorting", Request.Method.POST);
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
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.calendar_activity:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(R.anim.zm_fade_in, R.anim.zm_fade_out);
                        return true;
                    case R.id.view_stats_activity:
                        return true;
                    case R.id.sign_out_activity:
                        Intent startSignOut = new Intent(getApplicationContext(), SettingsActivity.class);
                        Log.d(TAG, "IS_MENTOR: " + isMentor);
                        if (isMentor != null) {
                            startSignOut.putExtra("isMentor", isMentor);
                        }
                        startActivity(startSignOut);
                        overridePendingTransition(R.anim.zm_fade_in, R.anim.zm_fade_out);
                        return true;
                    default:
                        return false;
                }
            }
            });


        }


    /**
     * Clear focus on touch outside for all EditText inputs.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof AutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void sortStatsByEntranceScore(boolean b) {
        int filterTextLength = filterAutoComplete.getText().toString().length();
        if (b && filterTextLength > 0) { // Filter & sort
            isSortEntranceScore = true;
            sortByGpa.setEnabled(false);
            getCardData("statsByConfiguration", Request.Method.POST);
        } else if (b) { // Only sort
            isSortEntranceScore = true;
            sortByGpa.setEnabled(false);
            getCardData("statsBySorting", Request.Method.POST);
        } else if (filterTextLength > 0) { // Only filter no sort
            isSortEntranceScore = false;
            sortByGpa.setEnabled(true);
            getCardData("statsByFilter", Request.Method.POST);
        } else if (!isSortGpa) { // No filter no sort
            isSortEntranceScore = false;
            sortByGpa.setEnabled(true);
            getCardData("stats", Request.Method.GET);
        }
    }

    private void sortStatsByGpa(boolean b) {
        int filterTextLength = filterAutoComplete.getText().toString().length();
        if (b && filterTextLength > 0) { // Filter & sort
            sortByEntranceScore.setEnabled(false);
            isSortGpa = true;
            getCardData("statsByConfiguration", Request.Method.POST);
        } else if (b) { // Only sort
            sortByEntranceScore.setEnabled(false);
            isSortGpa = true;
            getCardData("statsBySorting", Request.Method.POST);
        } else if (filterTextLength > 0) { // Only filter no sort
            isSortGpa = false;
            sortByEntranceScore.setEnabled(true);
            getCardData("statsByFilter", Request.Method.POST);
        } else if (!isSortEntranceScore) { // No filter no sort
            isSortGpa = false;
            sortByEntranceScore.setEnabled(true);
            getCardData("stats", Request.Method.GET);
        }
    }

    private void initCardView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_card_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statsList = new ArrayList<>();

        cardAdapter = new CardAdapter(this, statsList);
        recyclerView.setAdapter(cardAdapter);

        statsList.clear();
        univNameStats.clear();
        univMajorStats.clear();
        filterOptions.clear();
        getCardData("stats", Request.Method.GET);
    }

    @AddTrace(name = "getStatsTrace", enabled = true)
    private void getCardData(String endPoint, int requestMethod) {

        //Pull from DB and store in statsList
        String URL = IpConstants.URL + endPoint;

        JSONObject body = new JSONObject();

        int filterTextLength = filterAutoComplete.getText().length();

        // Filter and Sort
        // Only Sort

        // Only Filter
        if (filterTextLength > 0) {
            if (!isSortGpa && !isSortEntranceScore) {
                try {
                    body.put(univNameStats.contains(searchText) ? "univName" : "univMajor", searchText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    body.put(univNameStats.contains(searchText) ? "univName" : "univMajor", searchText);
                    body.put(isSortGpa ? "univGpa" : "univEntranceScore", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if ((filterTextLength == 0) && (isSortGpa || isSortEntranceScore)) {
            try {
                body.put(isSortGpa ? "univGpa" : "univEntranceScore", "");
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
                            if (statArray.isNull(0)) {
                                isMentor = false;
                            }
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
                                if (endPoint.equals("stats"))
                                    checkIfMentor(userEmail);
                                statsList.add(new StatsCards(userEmail, userName, (String) userStat.get("univName"), (String) userStat.get("univMajor"), userStat.getDouble("univGpa"), userStat.getInt("univEntranceScore"), (String) userStat.get("univBio"), (String) userStat.get("userPhoto")));
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

    private void checkIfMentor(String userEmail) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String currUserEmail = account.getEmail();
        Log.d(TAG, "statEmail: " + userEmail + " | " + "currUserEmail: " + currUserEmail);
        if (isMentor == null || !isMentor) // Modify isMentor only if no mentor found yet, else keep changing the value
            isMentor = Objects.equals(currUserEmail, userEmail);
        Log.d(TAG, "any true? " + isMentor);
    }

    @Override
    public void onBackPressed() {
        Boolean shouldAllowBack = false;
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }

}