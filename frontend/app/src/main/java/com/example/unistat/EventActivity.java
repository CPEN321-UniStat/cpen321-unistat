package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unistat.Payment.CheckoutActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";
    private WeekViewEvent event;
    private Button acceptMeetingButton;
    private Button declineMeetingButton;
    private Button joinMeetingButton;
    private Button makePaymentButton;
    private RequestQueue requestQueue;
    private Long meetingID;
    private Boolean isMentor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getAndSetMeetingInfo();

        addButtonListeners();
    }

    private void getAndSetMeetingInfo() {
        // 1. Get all parameters
        Bundle params = getIntent().getExtras();
        meetingID = params.getLong("meetingID");
        String meetingName = params.getString("meetingName");
        String mentorEmail = params.getString("mentorEmail");
        String menteeEmail = params.getString("menteeEmail");
        Double paymentAmount = params.getDouble("paymentAmount");
        String status = params.getString("status");
        String startTime = params.getString("startTime");
        String endTime = params.getString("endTime");
        String date = params.getString("date");

        TextView profileText = findViewById(R.id.name);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        if (userEmail.equals(mentorEmail)) {
            isMentor = true;
            profileText.setText(menteeEmail);
        } else{
            profileText.setText(mentorEmail);
        }

        // 2. Set all parameters
        TextView eventNameText = findViewById(R.id.eventName);
        eventNameText.setText(meetingName);

        TextView paymentAmountText = findViewById(R.id.money);
        paymentAmountText.setText(String.valueOf(paymentAmount));

        TextView timeText = findViewById(R.id.time);
        timeText.setText(startTime + " - " + endTime);

        TextView dateText = findViewById(R.id.date);
        dateText.setText(date);

        decideWhatsVisible(status);
    }

    private void decideWhatsVisible(String status) {
        acceptMeetingButton = findViewById(R.id.acceptMeetingRequest);
        declineMeetingButton = findViewById(R.id.declineMeetingRequest);
        joinMeetingButton = findViewById(R.id.joinMeeting);
        makePaymentButton = findViewById(R.id.makePayment);

        if (status.equals("Accepted")) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
            joinMeetingButton.setVisibility(View.VISIBLE);
            makePaymentButton.setVisibility(View.VISIBLE);
        } else if (status.equals("Declined")) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
            joinMeetingButton.setVisibility(View.GONE);
            makePaymentButton.setVisibility(View.GONE);
        } else if (status.equals("Pending")) {
            acceptMeetingButton.setVisibility(View.VISIBLE);
            declineMeetingButton.setVisibility(View.VISIBLE);
            joinMeetingButton.setVisibility(View.GONE);
            makePaymentButton.setVisibility(View.GONE);
        }
    }

    private void addButtonListeners() {
        // 3. Add onclick listeners for accept, decline, and join meeting
        requestQueue = Volley.newRequestQueue(EventActivity.this);

        acceptMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMeetingStatus("Accepted", meetingID);
                acceptMeetingButton.setVisibility(View.GONE);
                declineMeetingButton.setVisibility(View.GONE);
                joinMeetingButton.setVisibility(View.VISIBLE);
                makePaymentButton.setVisibility(View.VISIBLE);
            }
        });
        declineMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMeetingStatus("Declined", meetingID);
                acceptMeetingButton.setVisibility(View.GONE);
                declineMeetingButton.setVisibility(View.GONE);
                joinMeetingButton.setVisibility(View.GONE);
            }
        });
        joinMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinMeeting = new Intent(EventActivity.this, ZoomMeetingActivity.class);
                startActivity(joinMeeting);
            }
        });
        makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkoutActivity = new Intent(EventActivity.this, CheckoutActivity.class);
                startActivity(checkoutActivity);
            }
        });
    }

    private void updateMeetingStatus(String newStatus, Long meetingID) {
        String URL = "http://10.0.2.2:8081/meetings";

        JSONObject body = new JSONObject();
        try {
            body.put("meetingID", meetingID);
            body.put("status", newStatus);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateMeetingRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        Toast.makeText(EventActivity.this, "Your meeting response has been sent", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Server error: " + error);
                    }
                }
        );

        requestQueue.add(updateMeetingRequest);
    }

    // Payment
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }
}