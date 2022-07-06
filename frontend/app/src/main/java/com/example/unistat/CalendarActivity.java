package com.example.unistat;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.Meeting.MeetingLog;
import com.example.unistat.StatsCardView.StatsCards;
import com.example.unistat.StatsCardView.ViewStatsActivity;
import com.example.unistat.Meeting.Meeting;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 6;
    private static final int TYPE_WEEK_VIEW = 3;
    final static String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSZ";
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private ArrayList<Meeting> meetings;
    private RequestQueue requestQueue;

    private Boolean shouldAllowBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        meetings = new ArrayList<Meeting>();
        requestQueue = Volley.newRequestQueue(CalendarActivity.this);

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

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.calendar_activity);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.calendar_activity:
                        return true;
                    case R.id.view_stats_activity:
                        startActivity(new Intent(getApplicationContext(),ViewStatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.sign_out_activity:
                        startActivity(new Intent(getApplicationContext(),SignOutActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void viewEvent(WeekViewEvent event) throws Exception {
        Meeting meeting = null;
        Long eventID = event.getId();
        Log.d("MEETINGS", String.valueOf(meetings.size()));
        for (int i = 0; i < meetings.size(); i++ ) {
            Meeting currMeeting = meetings.get(i);
            Log.d("CURRMEETING", String.valueOf(currMeeting.getId()));
            Log.d("NEXTMEETING", (String.valueOf(eventID)));
            if (String.valueOf(currMeeting.getId()).equals(String.valueOf(eventID))) {
                meeting = currMeeting;
                break;
            }
        }
        if (meeting == null) {
            throw new Exception("There is not a corresponding meeting variable that the backend has provided");
        }

        DateFormat df = new SimpleDateFormat("h:mm a");

        Intent viewEvent = new Intent(CalendarActivity.this, EventActivity.class);
        Bundle params = new Bundle();
        params.putLong("meetingID", event.getId());
        params.putString("meetingName", meeting.getName());
        params.putString("mentorEmail", meeting.getMentorEmail());
        params.putString("menteeEmail", meeting.getMenteeEmail());
        params.putDouble("paymentAmount", (Double) meeting.getPaymentAmount());
        params.putString("status", String.valueOf(meeting.getStatus()));
        params.putString("startTime", df.format(event.getStartTime().getTime()));
        params.putString("endTime", df.format(event.getEndTime().getTime()));
        params.putString("date", String.valueOf(event.getEndTime().get(Calendar.YEAR)) + "/" + String.valueOf(event.getEndTime().get(Calendar.MONTH)) + "/" + String.valueOf(event.getEndTime().get(Calendar.DAY_OF_MONTH)));

        viewEvent.putExtras(params);
        startActivity(viewEvent);
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
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();

        // Populate the week view with some events.

        // List <Strings> getMeetingIds(emailAddress)
        // return a list of meeting ids for the current user. Fetches from the userDB
        // [123, 1234, 125]


        return getAllMeetingsByEmail(userEmail, newMonth, newYear);
    }

    private List<WeekViewEvent> getAllMeetingsByEmail(String userEmail, int newMonth, int newYear) {
        String URL = "http://10.0.2.2:8081/meetings";

        JSONObject body = new JSONObject();
        try {
            body.put("mentorEmail", userEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList events = new ArrayList<WeekViewEvent>();
        JsonObjectRequest getAllMeetingsRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CALENDAR", "Server resp: " + response.toString());
                        try {
                            JSONArray meetingArray = (JSONArray) response.get("meetings");
                            for (int i = 0; i < meetingArray.length(); i++){
                                JSONObject meeting =  meetingArray.getJSONObject(i);
                                Toast.makeText(CalendarActivity.this, "inside", Toast.LENGTH_LONG).show();

                                String meetingName = (String) meeting.get("meetingName");
                                Long meetingID = Long.parseLong((String) meeting.get("meetingID"));
                                String mentorEmail = (String) meeting.get("mentorEmail");
                                String menteeEmail = (String) meeting.get("menteeEmail");
                                Double paymentAmount = Double.parseDouble((String) meeting.get("paymentAmount"));
                                String status = (String) meeting.get("status");
                                String startTimeString = (String) meeting.get("startTime");
                                String endTimeString = (String) meeting.get("endTime");

                                Calendar startTime = ISO8601.toCalendar(startTimeString);
                                Calendar endTime = ISO8601.toCalendar(endTimeString);

                                WeekViewEvent event = new WeekViewEvent(meetingID, meetingName, startTime, endTime);
                                event.setColor((status == "Pending") ? getResources().getColor(R.color.grey) : (status == "Accepted") ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
                                events.add(event);
                                meetings.add(new Meeting(meetingID, meetingName, startTime, endTime, mentorEmail, menteeEmail, paymentAmount, status, new ArrayList<MeetingLog>()));
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CALENDAR", "Server error: " + error);
                    }
                }
        );
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        endTime.set(Calendar.MONTH, newMonth-1);
        WeekViewEvent event = new WeekViewEvent(1, "McChicken time", startTime, endTime);
        event.setColor(getResources().getColor(R.color.purple_200));
        events.add(event);
        meetings.add(new Meeting(1, "McChicken time", startTime, endTime, "quinncarroll810@gmail.com", "vijeeth@gmail.com", 21, "Pending", new ArrayList<MeetingLog>()));


        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 8);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        endTime.set(Calendar.MONTH, newMonth-1);
        event = new WeekViewEvent(12345, "Just a test meeting by Quinn", startTime, endTime);
        event.setColor(getResources().getColor(R.color.purple_200));
        events.add(event);
        meetings.add(new Meeting(12345, "Just a test meeting by Quinn", startTime, endTime, "quinncarroll810@gmail.com", "vijeeth@gmail.com", 21, "Pending", new ArrayList<MeetingLog>()));

        requestQueue.add(getAllMeetingsRequest);
        return events;
    }



    @Override
    public void onEmptyViewLongPress(Calendar time) {
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
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



/**
 * Helper class for handling a most common subset of ISO 8601 strings
 * (in the following format: "2008-03-01T13:00:00+01:00"). It supports
 * parsing the "Z" timezone, but many other less-used features are
 * missing.
 */
final class ISO8601 {
    /** Transform Calendar to ISO 8601 string. */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /** Get current date and time formatted as ISO 8601 string. */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /** Transform ISO 8601 string to Calendar. */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        calendar.setTime(date);
        return calendar;
    }
}