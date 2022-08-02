package com.example.unistat.ui.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import com.example.unistat.classes.IpConstants;
import com.example.unistat.R;
import com.example.unistat.ui.meetings.EventActivity;
import com.example.unistat.ui.settings.SettingsActivity;
import com.example.unistat.ui.stats.ViewStatsActivity;
import com.example.unistat.classes.Meeting;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener, WeekView.ScrollListener {
    final static String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSZ";
    private WeekView mWeekView;

    private Boolean shouldAllowBack = false;
    private static HttpURLConnection connection;

    private FloatingActionButton showOptimalMeetings;
    private boolean optimal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        showOptimalMeetings = findViewById(R.id.showOptimalMeetings);
        showOptimalMeetings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                optimal = !optimal;
                mWeekView.notifyDatasetChanged();
                if (!optimal) {
                    showOptimalMeetings.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_attach_money_24));
                    Toast.makeText(CalendarActivity.this, "Displaying All meetings...", Toast.LENGTH_LONG).show();
                } else {
                    showOptimalMeetings.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_money_off_24));
                    Toast.makeText(CalendarActivity.this, "Displaying Optimal meetings...", Toast.LENGTH_LONG).show();
                }
                Log.d("Response",  "Optimal Meetings");
            }
        });

        mWeekView = findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                try {
                    viewEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        mWeekView.setEventLongPressListener(this);

        mWeekView.setScrollListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.calendar_activity);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.calendar_activity:
                        return true;
                    case R.id.view_stats_activity:
                        startActivity(new Intent(getApplicationContext(),ViewStatsActivity.class));
                        overridePendingTransition(R.anim.zm_fade_in, R.anim.zm_fade_out);
                        return true;
                    case R.id.sign_out_activity:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(R.anim.zm_fade_in, R.anim.zm_fade_out);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resuming to calendar");
        mWeekView.notifyDatasetChanged();
    }


    private void viewEvent(WeekViewEvent event) throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        Meeting meeting = (Meeting) event;
        String meetingJsonString = gson.toJson(meeting);

        Intent viewEvent = new Intent(CalendarActivity.this, EventActivity.class);

        viewEvent.putExtra("Meeting", meetingJsonString);
        startActivity(viewEvent);
        overridePendingTransition(R.anim.zm_enlarge_in, R.anim.zm_enlarge_out);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
        Log.d("FirstVisibleDayChanged", "Day changed");
        if (optimal) {
            mWeekView.notifyDatasetChanged();
        }
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
//        System.out.println("Loading MEETINGS in calendar");
        Log.d("Response","Loading MEETINGS in calendar");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();

        return getMeetingsByEmail(userEmail, newMonth-1, newYear, optimal);
    }

    private List<Meeting> getMeetingsByEmail(String userEmail, int month, int year, boolean optimal) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        String URL = "";
        if (optimal) {
            URL = IpConstants.URL + "optimalMeetings/" + userEmail;
        }
        else{
            URL = IpConstants.URL + "meetings/" + userEmail;
        }
//        String URL = IpConstants.URL + "meetings/" + userEmail;

        List<Meeting> events = new ArrayList<>();

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            
            //Request setup
            if (optimal){
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                Calendar start = mWeekView.getFirstVisibleDay();
                Calendar end = (Calendar) start.clone();
                end.add(Calendar.DAY_OF_MONTH, mWeekView.getNumberOfVisibleDays()-1);

                Log.d("HEADER",  String.valueOf(start.get(Calendar.MONTH)));
                Log.d("HEADER",  String.valueOf(start.get(Calendar.DATE)));
                Log.d("HEADER",  String.valueOf(end.get(Calendar.MONTH)));
                Log.d("HEADER",  String.valueOf(end.get(Calendar.DATE)));
                Log.d("HEADER",  String.valueOf(year));

                connection.setRequestProperty("startmonth", String.valueOf(start.get(Calendar.MONTH)));
                connection.setRequestProperty("startday", String.valueOf(start.get(Calendar.DATE)));
                connection.setRequestProperty("endmonth", String.valueOf(end.get(Calendar.MONTH)));
                connection.setRequestProperty("endday", String.valueOf(end.get(Calendar.DATE)));
                connection.setRequestProperty("weekloadermonth", String.valueOf(month));
                connection.setRequestProperty("year", String.valueOf(year));
            }
            else{
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("month", String.valueOf(month));
                connection.setRequestProperty("year", String.valueOf(year));
            }

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            Log.d("Response",  responseContent.toString());
//            System.out.println(responseContent.toString());
            JSONObject response = new JSONObject(responseContent.toString());
            JSONArray meetingArray = (JSONArray) response.get("meetings");
            for (int i = 0; i < meetingArray.length(); i++){
                JSONObject meeting =  meetingArray.getJSONObject(i);
                String jsonString = meeting.toString();
                Meeting meetingObj = gson.fromJson(jsonString, Meeting.class);
                events.add(meetingObj);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        System.out.println("Length of events: " + events.size());
        return events;
    }

    public static Calendar getCalendarFromISO(String datestring) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()) ;
        SimpleDateFormat dateformat = new SimpleDateFormat(ISO8601DATEFORMAT, Locale.getDefault());
        try {
            Date date = dateformat.parse(datestring);
            date.setHours(date.getHours()-1);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return calendar;
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        // TODO: when empty space is long pressed
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        // TODO: when event is clicked
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        // TODO: when event is long pressed
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }

}