package com.example.unistat;

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
import com.example.unistat.meeting.Meeting;
import com.example.unistat.meeting.MeetingLog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RequestMeeting extends AppCompatActivity {

    private TextView startDateText;
    private TextView endDateText;

    private SimpleDateFormat dateFormat;

    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
    private TextInputLayout meetingTitleInput;
    private TextInputLayout paymentInput;

    private RequestQueue requestQueue;
    private String mentorEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meeting);

        Intent intent = getIntent();
        mentorEmail = intent.getStringExtra("mentorEmail");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(RequestMeeting.this);
        assert account != null;
        String userEmail = account.getEmail();

        requestQueue = Volley.newRequestQueue(RequestMeeting.this);

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

                if (meetingTitleValid && paymentValid) {
                    meetingTitle = meetingTitle.trim();
                    double payment = Double.parseDouble(paymentOffer.trim());
                    bookMeeting(meetingTitle, mentorEmail, userEmail, (Calendar) startTimeCalendar.clone(), (Calendar) endTimeCalendar.clone(), payment);
                    Toast.makeText(RequestMeeting.this, "Your meeting request was sent", Toast.LENGTH_LONG).show();
                    Intent viewCalendar = new Intent(RequestMeeting.this, CalendarActivity.class);
                    startActivity(viewCalendar);
                }
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

        TextView endTimeText = findViewById(R.id.end_time_text);
        Date curDate = calendar.getTime();
        curDate.setTime(curDate.getTime() + 3600000);
        String endDate = dateFormat.format(curDate);
        endDateText.setText(endDate);
        String endTime = timeFormat.format(curDate);
        endTimeText.setText(endTime);
        endTimeCalendar = Calendar.getInstance();
        endTimeCalendar.setTime(curDate);




        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        addDatePickerOnClickListener(materialDatePicker, startDateText);
        addDatePickerOnClickListener(materialDatePicker, endDateText);


        materialDatePicker.addOnPositiveButtonClickListener( new MaterialPickerOnPositiveButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Object selection) {

                startDateText.setText(materialDatePicker.getHeaderText());
                endDateText.setText(materialDatePicker.getHeaderText());

                Date date1;
                try {
                    date1 = dateFormat.parse(materialDatePicker.getHeaderText());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date1);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int year = cal.get(Calendar.YEAR);
                    startTimeCalendar.set(Calendar.MONTH, month);
                    startTimeCalendar.set(Calendar.DAY_OF_MONTH, day);
                    startTimeCalendar.set(Calendar.YEAR, year);
                    endTimeCalendar.set(Calendar.MONTH, month);
                    endTimeCalendar.set(Calendar.DAY_OF_MONTH, day);
                    endTimeCalendar.set(Calendar.YEAR, year);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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

    private boolean isMeetingTitleValid(String meetingTitle) {
        return !meetingTitle.trim().isEmpty();
    }

    private boolean isPaymentValid(String payment) {
        if (payment.trim().isEmpty())
            return false;
        return payment.trim().matches("-?\\d+(\\.\\d+)?");
    }

    private void addDatePickerOnClickListener(MaterialDatePicker materialDatePicker, TextView dateText) {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
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

    public void bookMeeting(String name, String mentorEmail, String menteeEmail, Calendar startTime, Calendar endTime, double payment) {
        long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        List<MeetingLog> logs = new LinkedList<>();
        Meeting meeting = new Meeting(id, name, startTime, endTime, mentorEmail, menteeEmail, payment, Meeting.Status.PENDING, logs);
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

            URL = IpConstants.URL + "sendMeetingRequest";
            JSONObject notificationBody = new JSONObject();
            notificationBody.put("email", mentorEmail);
            JsonObjectRequest postRequestNotification = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    notificationBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RequestMeetingNotif", "Server resp: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("RequestMeetingNotif", "Server error: " + error);
                        }
                    }
            );
            requestQueue.add(postRequestNotification);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

}