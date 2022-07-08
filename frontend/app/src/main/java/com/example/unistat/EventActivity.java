package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.app.Activity;
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
import com.example.unistat.Meeting.Meeting;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

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
    Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initZoom(this);

        getAndSetMeetingInfo();
        addButtonListeners();
    }

    private void initZoom(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "ABj2ki7HAdTM9dFc44KdbMnjjlo5v26P46MY";
        params.appSecret = "1ImANl0EeqLEorNO98DptPpNBn0t6DySJtXH";
        params.domain = "zoom.us";
        params.enableLog = true;

        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

            }

            @Override
            public void onZoomAuthIdentityExpired() {

            }
        };
        sdk.initialize(context, listener, params);
    }

    private void joinZoomMeeting(Context context) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = "Kush Arora";
        params.meetingNo = "9419977292";
        params.password = "5bCwMU";
        meetingService.joinMeetingWithParams(context, params, options);
    }

    private void getAndSetMeetingInfo() {
        // 1. Get all parameters
        Intent intent = getIntent();
        String meetingJsonString = intent.getStringExtra("Meeting");

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        meeting = gson.fromJson(meetingJsonString, Meeting.class);

        TextView profileText = findViewById(R.id.name);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        if (userEmail.equals(meeting.getMentorEmail())) {
            isMentor = true;
            profileText.setText(meeting.getMenteeEmail());
        } else{
            profileText.setText(meeting.getMentorEmail());
        }

        // 2. Set all parameters
        TextView eventNameText = findViewById(R.id.eventName);
        eventNameText.setText(meeting.getName());

        TextView paymentAmountText = findViewById(R.id.money);
        paymentAmountText.setText(String.valueOf(meeting.getPaymentAmount()));

        TextView timeText = findViewById(R.id.time);
        DateFormat df = new SimpleDateFormat("h:mm a");
        String startTimeString = df.format(meeting.getStartTime().getTime());
        String endTimeString = df.format(meeting.getEndTime().getTime());
        timeText.setText(startTimeString + " - " + endTimeString);

        df = new SimpleDateFormat("MMM d, yyyy");
        String date = df.format(meeting.getStartTime().getTime());
        TextView dateText = findViewById(R.id.date);
        dateText.setText(date);

        decideWhatsVisible(meeting.getStatus());
    }

    private void decideWhatsVisible(Meeting.Status status) {
        acceptMeetingButton = findViewById(R.id.acceptMeetingRequest);
        declineMeetingButton = findViewById(R.id.declineMeetingRequest);
        joinMeetingButton = findViewById(R.id.joinMeeting);
        makePaymentButton = findViewById(R.id.makePayment);

        if (status.equals(Meeting.Status.ACCEPTED)) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
            joinMeetingButton.setVisibility(View.VISIBLE);
            makePaymentButton.setVisibility(View.VISIBLE);
        } else if (status.equals(Meeting.Status.REJECTED)) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
            joinMeetingButton.setVisibility(View.GONE);
            makePaymentButton.setVisibility(View.GONE);
        } else if (status.equals(Meeting.Status.PENDING)) {
            acceptMeetingButton.setVisibility(View.VISIBLE);
            declineMeetingButton.setVisibility(View.VISIBLE);
            joinMeetingButton.setVisibility(View.GONE);
            makePaymentButton.setVisibility(View.GONE);
        }
        if (!isMentor) {
            acceptMeetingButton.setVisibility(View.VISIBLE);
            declineMeetingButton.setVisibility(View.VISIBLE);
        } else {
            makePaymentButton.setVisibility(View.GONE);
        }
    }

    private void addButtonListeners() {
        // 3. Add onclick listeners for accept, decline, and join meeting
        requestQueue = Volley.newRequestQueue(EventActivity.this);

        acceptMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMeetingStatus(Meeting.Status.ACCEPTED);
                acceptMeetingButton.setVisibility(View.GONE);
                declineMeetingButton.setVisibility(View.GONE);
                joinMeetingButton.setVisibility(View.VISIBLE);
                joinMeetingButton.setClickable(false);
                makePaymentButton.setVisibility(View.VISIBLE);
            }
        });
        declineMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMeetingStatus(Meeting.Status.REJECTED);
                acceptMeetingButton.setVisibility(View.GONE);
                declineMeetingButton.setVisibility(View.GONE);
                joinMeetingButton.setVisibility(View.GONE);
            }
        });
        joinMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinZoomMeeting(EventActivity.this);
            }
        });
        makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void updateMeetingStatus(Meeting.Status status) {
        meeting.setStatus(status);
        System.out.println(status.name() + " " + meeting.getColor());

        String URL = "http://10.0.2.2:8081/meetings";

        JSONObject body = new JSONObject();
        try {
            body.put("mId", meeting.getId());
            body.put("status", meeting.getStatus().name());
            body.put("mColor", meeting.getColor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest updateMeetingRequest = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
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


        URL = "http://10.0.2.2:8081/sendMeetingResponse";
        JSONObject responseNotificationBody = new JSONObject();
        try {
            responseNotificationBody.put("email", meeting.getMenteeEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest sendMeetingResponseNotification = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                responseNotificationBody,
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
        requestQueue.add(sendMeetingResponseNotification);
    }
}