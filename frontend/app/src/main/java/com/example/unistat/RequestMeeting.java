package com.example.unistat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class RequestMeeting extends AppCompatActivity {

    private TextView startDateText;
    private TextView startTimeText;
    private TextView endDateText;
    private TextView endTimeText;
    private TextView meetingTitle;

    private Calendar calendar;
    private SimpleDateFormat dateFormat, timeFormat;

    private static final String TAG = "RequestMeeting";
    private RequestQueue requestQueue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meeting);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM d, yyyy");
        String date = dateFormat.format(calendar.getTime());
        timeFormat = new SimpleDateFormat("h:mm a");

        startDateText = findViewById(R.id.start_date_text);
        startDateText.setText(date);

        endDateText = findViewById(R.id.end_date_text);
        endDateText.setText(date);


        startTimeText = findViewById(R.id.start_time_text);
        String startTime = timeFormat.format(calendar.getTime());
        startTimeText.setText(startTime);

        endTimeText = findViewById(R.id.end_time_text);
        Date curDate = calendar.getTime();
        curDate.setTime(curDate.getTime() + 3600000);
        String endTime = timeFormat.format(curDate);
        endTimeText.setText(endTime);

        requestQueue = Volley.newRequestQueue(RequestMeeting.this);


        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        addDatePickerOnClickListener(materialDatePicker, startDateText);
        addDatePickerOnClickListener(materialDatePicker, endDateText);


        materialDatePicker.addOnPositiveButtonClickListener( new MaterialPickerOnPositiveButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Object selection) {

                // if the user clicks on the positive
                // button that is ok button update the
                // selected date
                startDateText.setText(materialDatePicker.getHeaderText());
                endDateText.setText(materialDatePicker.getHeaderText());
                // in the above statement, getHeaderText
                // is the selected date preview from the
                // dialog
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


        addTimePickerOnPositiveClickListener(startTimeMaterialTimePicker, startTimeText);
        addTimePickerOnPositiveClickListener(endTimeMaterialTimePicker, endTimeText);

    }

    private void addDatePickerOnClickListener(MaterialDatePicker materialDatePicker, TextView dateText) {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
    }

    private void addTimePickerOnPositiveClickListener(MaterialTimePicker materialTimePicker, TextView timeText) {
        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hours = materialTimePicker.getHour();
                int minutes = materialTimePicker.getMinute();
                String time;

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

    public void sendMeetingRequest(View v) {
        String URL = "http://10.0.2.2:8081/meetings";

        JSONObject body = new JSONObject();
        try {
            body.put("status", "Pending");
            body.put("meetingName", "QuinnMeeting");
            body.put("mentorEmail", "quinn@gmail.com");
            body.put("menteeEmail", "vijeeth@gmail.com");
            body.put("paymentAmount", "21");
            body.put("status", "Pending");
            body.put("startTime", "2022-07-05T22:00:00.000Z");
            body.put("startTime", "2022-07-05T23:00:00.000Z");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest sendMeetingRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        Toast.makeText(RequestMeeting.this, "Your meeting request has been sent", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Server error: " + error);
                    }
                }
        );

        requestQueue.add(sendMeetingRequest);
    }

}