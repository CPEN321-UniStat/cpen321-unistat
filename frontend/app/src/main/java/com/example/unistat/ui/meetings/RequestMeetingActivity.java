package com.example.unistat.ui.meetings;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.classes.IpConstants;
import com.example.unistat.R;
import com.example.unistat.classes.Meeting;
import com.example.unistat.classes.MeetingLog;
import com.example.unistat.ui.calendar.CalendarActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class RequestMeetingActivity extends AppCompatActivity {

    private static final String TAG = "RequestMeeting";
    private TextView startDateText;
    private TextView endDateText;

    private SimpleDateFormat dateFormat;

    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
    private TextInputLayout meetingTitleInput;
    private TextInputLayout paymentInput;

    private RequestQueue requestQueue;
    private String mentorEmail;
    private String mentorName;
    private String menteeName;

    CalendarConstraints.Builder endTimeConstraintsBuilder;
    MaterialDatePicker.Builder endMaterialDateBuilder;
    MaterialDatePicker endMaterialDatePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meeting);

        Intent intent = getIntent();
        mentorEmail = intent.getStringExtra("mentorEmail");
        mentorName = intent.getStringExtra("mentorName");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(RequestMeetingActivity.this);
        assert account != null;
        String userEmail = account.getEmail();

        menteeName = account.getDisplayName();

        requestQueue = Volley.newRequestQueue(RequestMeetingActivity.this);

        meetingTitleInput = findViewById(R.id.meeting_title_input);

        paymentInput = findViewById(R.id.payment_offer_input);

        Button bookMeetingButton = findViewById(R.id.book_meeting_button);
        bookMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String meetingTitle = meetingTitleInput.getEditText().getText().toString();
                String paymentOffer = paymentInput.getEditText().getText().toString();
                boolean meetingTitleValid = isMeetingTitleValid(meetingTitle);
                boolean paymentValid = isPaymentValid(paymentOffer);
                meetingTitleInput.setError(meetingTitleValid ? null : "Meeting name must not be empty");
                paymentInput.setError(paymentValid ? null : "Enter a valid number");

                bookMeetingIfValid(userEmail, meetingTitleValid, paymentValid, meetingTitle.trim(), paymentOffer);
            }
        });

        Calendar calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM d, yyyy");
        String date = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        startDateText = findViewById(R.id.start_date_text);
        startDateText.setText(date);

        endDateText = findViewById(R.id.end_date_text);
//        endDateText.setText(date);


        TextView startTimeText = findViewById(R.id.start_time_text);
        String startTime = timeFormat.format(calendar.getTime());
        startTimeText.setText(startTime);
        startTimeCalendar = Calendar.getInstance();

        Calendar startTimeCalendarClone = (Calendar) startTimeCalendar.clone();
        startTimeCalendarClone.set(Calendar.HOUR_OF_DAY, 0);
        startTimeCalendarClone.set(Calendar.MINUTE, 0);
        startTimeCalendarClone.set(Calendar.SECOND, 0);
        startTimeCalendarClone.set(Calendar.MILLISECOND, 0);
        startTimeCalendarClone.setTimeZone(TimeZone.getTimeZone("UTC"));
        TextView endTimeText = findViewById(R.id.end_time_text);
        Date curDate = calendar.getTime();
        curDate.setTime(curDate.getTime() + 3600000);
        String endDate = dateFormat.format(curDate);
        endDateText.setText(endDate);
        String endTime = timeFormat.format(curDate);
        endTimeText.setText(endTime);
        endTimeCalendar = Calendar.getInstance();
        endTimeCalendar.setTime(curDate);

        System.out.println("Time cal " + startTimeCalendar.getTimeInMillis());
        System.out.println("time " + System.currentTimeMillis());

        CalendarConstraints.Builder startTimeConstraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder startMaterialDateBuilder = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(startTimeConstraintsBuilder.build());

        startMaterialDateBuilder.setTitleText("SELECT START DATE");
        final MaterialDatePicker startMaterialDatePicker = startMaterialDateBuilder.build();

        endTimeConstraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(startTimeCalendarClone.getTimeInMillis()));

        endMaterialDateBuilder = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(endTimeConstraintsBuilder.build());

        endMaterialDateBuilder.setTitleText("SELECT END DATE");
        endMaterialDatePicker = endMaterialDateBuilder.build();

        addDatePickerOnClickListener(startMaterialDatePicker, startDateText);
        addDatePickerOnClickListener(endMaterialDatePicker, endDateText);


        startMaterialDatePicker.addOnPositiveButtonClickListener( new MaterialPickerOnPositiveButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Object selection) {

                startDateText.setText(startMaterialDatePicker.getHeaderText());

                Date date1;
                try {
                    date1 = dateFormat.parse(startMaterialDatePicker.getHeaderText());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date1);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int year = cal.get(Calendar.YEAR);
                    startTimeCalendar.set(Calendar.MONTH, month);
                    startTimeCalendar.set(Calendar.DAY_OF_MONTH, day);
                    startTimeCalendar.set(Calendar.YEAR, year);

                    Calendar startTimeCalendarClone = (Calendar) startTimeCalendar.clone();
                    startTimeCalendarClone.set(Calendar.HOUR_OF_DAY, 0);
                    startTimeCalendarClone.set(Calendar.MINUTE, 0);
                    startTimeCalendarClone.set(Calendar.SECOND, 0);
                    startTimeCalendarClone.set(Calendar.MILLISECOND, 0);
                    startTimeCalendarClone.setTimeZone(TimeZone.getTimeZone("UTC"));

                    endTimeConstraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(startTimeCalendarClone.getTimeInMillis()));

                    endMaterialDateBuilder = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(endTimeConstraintsBuilder.build());

                    endMaterialDateBuilder.setTitleText("SELECT END DATE");
                    endMaterialDatePicker = endMaterialDateBuilder.build();
                    addDatePickerOnClickListener(endMaterialDatePicker, endDateText);
                    addEndDatePickerOnClickListener();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        addEndDatePickerOnClickListener();

        MaterialTimePicker startTimeMaterialTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT START TIME")
                .build();

        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeMaterialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
            }
        });

        MaterialTimePicker endTimeMaterialTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("SELECT END TIME")
                .build();

        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimeMaterialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
            }
        });


        addTimePickerOnPositiveClickListener(startTimeMaterialTimePicker, startTimeText, startTimeCalendar);
        addTimePickerOnPositiveClickListener(endTimeMaterialTimePicker, endTimeText, endTimeCalendar);

    }

    private void bookMeetingIfValid(String userEmail, boolean meetingTitleValid, boolean paymentValid, String meetingTitle, String paymentOffer) {
        long timeNow = System.currentTimeMillis();
        String URL = IpConstants.URL + "statsByFilter";
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
                            JSONArray statArray = (JSONArray) response.get("statData");
                            JSONObject userStat = statArray.getJSONObject(0);
                            double balance = userStat.getDouble("coins");
                            if (meetingTitleValid && paymentValid) {
                                double payment = Double.parseDouble(paymentOffer.trim());
                                if (payment > balance) {
                                    Toast.makeText(RequestMeetingActivity.this, "Not enough balance for payment " + payment, Toast.LENGTH_LONG).show();
                                } else if (startTimeCalendar.getTimeInMillis() > endTimeCalendar.getTimeInMillis()) {
                                    Toast.makeText(RequestMeetingActivity.this, "Start time cannot be after end time", Toast.LENGTH_LONG).show();
                                } else if (startTimeCalendar.getTimeInMillis() < timeNow || endTimeCalendar.getTimeInMillis() < timeNow) {
                                    Toast.makeText(RequestMeetingActivity.this, "Start time or end time cannot be in the past", Toast.LENGTH_LONG).show();
                                } else {
                                    bookMeeting(meetingTitle, mentorEmail, menteeName, mentorName, userEmail, (Calendar) startTimeCalendar.clone(), (Calendar) endTimeCalendar.clone(), payment);
                                    Toast.makeText(RequestMeetingActivity.this, "Your meeting request was sent", Toast.LENGTH_LONG).show();
                                    Intent viewCalendar = new Intent(RequestMeetingActivity.this, CalendarActivity.class);
                                    startActivity(viewCalendar);
                                    overridePendingTransition(R.anim.zm_slide_in_left, R.anim.zm_slide_out_right);
                                }
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

        requestQueue.add(getCoinsRequest);
    }

    private boolean isMeetingTitleValid(String meetingTitle) {
        return !meetingTitle.trim().isEmpty();
    }

    private boolean isPaymentValid(String payment) {
        if (payment.trim().isEmpty())
            return false;
        return payment.trim().matches("\\d+(\\.\\d+)?");
    }

    private void addDatePickerOnClickListener(MaterialDatePicker materialDatePicker, TextView dateText) {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
    }

    private void addEndDatePickerOnClickListener() {
        endMaterialDatePicker.addOnPositiveButtonClickListener( new MaterialPickerOnPositiveButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Object selection) {

                endDateText.setText(endMaterialDatePicker.getHeaderText());

                Date date1;
                try {
                    date1 = dateFormat.parse(endMaterialDatePicker.getHeaderText());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date1);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int year = cal.get(Calendar.YEAR);
                    endTimeCalendar.set(Calendar.MONTH, month);
                    endTimeCalendar.set(Calendar.DAY_OF_MONTH, day);
                    endTimeCalendar.set(Calendar.YEAR, year);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addTimePickerOnPositiveClickListener(MaterialTimePicker materialTimePicker, TextView timeText, Calendar calendar) {
        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hours = materialTimePicker.getHour();
                int minutes = materialTimePicker.getMinute();
                String time;

                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);

                if (hours > 12) {
                    if (minutes < 10) {
                        time = String.format("%d:0%d PM", hours - 12, minutes);
                    }
                    else {
                        time = String.format("%d:%d PM", hours - 12, minutes);
                    }
                }
                else if (hours == 12) {
                    if (minutes < 10) {
                        time = String.format("%d:0%d PM", hours, minutes);
                    }
                    else {
                        time = String.format("%d:%d PM", hours, minutes);
                    }
                }
                else if (hours == 0) {
                    if (minutes < 10) {
                        time = String.format("%d:0%d AM", hours + 12, minutes);
                    }
                    else {
                        time = String.format("%d:%d AM", hours + 12, minutes);
                    }
                }
                else {
                    if (minutes < 10) {
                        time = String.format("%d:0%d AM", hours, minutes);
                    }
                    else {
                        time = String.format("%d:%d AM", hours, minutes);
                    }
                }
                timeText.setText(time);

            }
        });
    }

    public void bookMeeting(String name, String mentorEmail, String menteeName, String mentorName, String menteeEmail, Calendar startTime, Calendar endTime, double payment) {
        long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        List<MeetingLog> logs = new LinkedList<>();
        Meeting.User mentee = new Meeting.User(menteeName, menteeEmail);
        Meeting.User mentor = new Meeting.User(mentorName, mentorEmail);
        Meeting meeting = new Meeting(id, name, mentee, mentor, startTime, endTime, payment, Meeting.Status.PENDING, logs);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String jsonBody = gson.toJson(meeting);

        String URL = IpConstants.URL + "meetings";
        try {
            JSONObject jsonObject = new JSONObject(jsonBody);
            JsonObjectRequest postMeetingsRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RequestMeeting", "Server resp: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("RequestMeeting", "Server error: " + error);
                        }
                    }
            );
            requestQueue.add(postMeetingsRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zm_tip_fadein, R.anim.zm_fade_out);
    }
}