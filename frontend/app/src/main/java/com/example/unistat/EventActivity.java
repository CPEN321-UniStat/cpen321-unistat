package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
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
import com.example.unistat.Meeting.Meeting;
import com.example.unistat.Meeting.MeetingLog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingParameter;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";
    private Button acceptMeetingButton;
    private Button declineMeetingButton;
    private Button joinMeetingButton;
    private RequestQueue requestQueue;
    private Boolean isMentor = false;
    Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        requestQueue = Volley.newRequestQueue(EventActivity.this);
        getAndSetMeetingInfo();
        addButtonListeners();
        initZoom(EventActivity.this);

//        // Show dialog if mentor
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(EventActivity.this);
        if (account.getEmail().equals(meeting.getMentorEmail())) {
            String msg = "Please leave the meeting once the other user has left to get paid.";
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .setTitle("Meeting Information")
                    .setMessage(msg)
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    private void initZoom(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "fixUR7859EnwYCEw1NrAHGGwHF5CMbhmxMOO";
        params.appSecret = "x8xZ2PGMJ55Fqkk1bucvlyJ9WMdxJN5d5fxZ";
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
        List<MeetingLog> meetingLogs = new LinkedList<>();
        meeting.setMeetingLogs(meetingLogs);
    }

    private void joinZoomMeeting(Context context, String id, String password) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = account.getDisplayName();
        params.meetingNo = id;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        meetingService.addListener(new MeetingServiceListener() {
            @Override
            public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {
//                Log.d(TAG, "Meeting Logs" + meeting.getMeetingLogs().toString());
//                  System.out.println("Meeting Logs: " + meeting.getMeetingLogs().toString());

                for ( MeetingLog log : meeting.getMeetingLogs() ){
                    System.out.println(log.getTimestamp());
                    System.out.println(log.getUserEmail());
                    System.out.println(log.getAction());
                }


//                Log.d(TAG, String.format("[onMeetingStatusChanged] meetingStatus: %s", meetingStatus.toString()));
                if (meetingStatus.toString().equals("MEETING_STATUS_INMEETING")){
                    Log.d(TAG, "Meething JOIN logged");

                    Date now = Calendar.getInstance().getTime();
                    String date = formatter.format(now);
                    String userEmail = account.getEmail();
                    MeetingLog curLog = new MeetingLog(date, userEmail, isMentor, MeetingLog.Action.JOINED);
//                    List<MeetingLog> meetingLogs = meeting.getMeetingLogs();
//                    meetingLogs.add(curLog);
//                    meeting.setMeetingLogs(meetingLogs);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("timestamp", curLog.getTimestamp());
                        jsonObject.put("userEmail", curLog.getUserEmail());
                        jsonObject.put("action", curLog.getAction().name());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateMeetingLog(jsonObject);
                }
                else if (meetingStatus.toString().equals("MEETING_STATUS_DISCONNECTING")){
                    Log.d(TAG, "Meething EXIT logged");
                    Date now = Calendar.getInstance().getTime();
                    String date = formatter.format(now);
                    String userEmail = account.getEmail();

                    MeetingLog curLog = new MeetingLog(date, userEmail, isMentor, MeetingLog.Action.LEFT);
                    Log.d(TAG, "CURLOG: " + curLog);



                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("timestamp", curLog.getTimestamp());
                        jsonObject.put("userEmail", curLog.getUserEmail());
                        jsonObject.put("action", curLog.getAction().name());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateMeetingLog(jsonObject);
//                    List<MeetingLog> meetingLogs = meeting.getMeetingLogs();
//                    meetingLogs.add(curLog);
//                    meeting.setMeetingLogs(meetingLogs);

                }
            }

            @Override
            public void onMeetingParameterNotification(MeetingParameter meetingParameter) {

            }
        });
    }

    private void getAndSetMeetingInfo() {
        // 1. Get all parameters
        Intent intent = getIntent();
        String meetingJsonString = intent.getStringExtra("Meeting");

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        meeting = gson.fromJson(meetingJsonString, Meeting.class);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        if (userEmail.equals(meeting.getMentorEmail())) {
            isMentor = true;
            getAndSetUserName(meeting.getMenteeEmail());
        } else{
            getAndSetUserName(meeting.getMentorEmail());
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

    private void getAndSetUserName(String email) {

        TextView profileText = findViewById(R.id.name);
        String URL = Constants.URL + "userByEmail";

        JSONObject body = new JSONObject();

        try {
            body.put("userEmail", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getNameRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getString("userName");
                            profileText.setText(name);
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

        requestQueue.add(getNameRequest);
    }

    private void decideWhatsVisible(Meeting.Status status) {
        acceptMeetingButton = findViewById(R.id.acceptMeetingRequest);
        declineMeetingButton = findViewById(R.id.declineMeetingRequest);
        joinMeetingButton = findViewById(R.id.joinMeeting);

        Boolean showJoinMeeting = false;

        if (status.equals(Meeting.Status.ACCEPTED)) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
            showJoinMeeting = true;
        } else if (status.equals(Meeting.Status.REJECTED)) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
            showJoinMeeting = false;
        } else if (status.equals(Meeting.Status.PENDING)) {
            acceptMeetingButton.setVisibility(View.VISIBLE);
            declineMeetingButton.setVisibility(View.VISIBLE);
            showJoinMeeting = false;
        }
        if (!isMentor) {
            acceptMeetingButton.setVisibility(View.GONE);
            declineMeetingButton.setVisibility(View.GONE);
        }

        Date meetingStart = meeting.getStartTime().getTime();
        Date meetingEnd = meeting.getEndTime().getTime();
        Date now = Calendar.getInstance().getTime();
        if (showJoinMeeting && now.before(meetingEnd) && now.after(meetingStart)) {
            joinMeetingButton.setVisibility(View.VISIBLE);
        } else {
            joinMeetingButton.setVisibility(View.GONE);
        }
    }

    private void addButtonListeners() {
        // 3. Add onclick listeners for accept, decline, and join meeting

        acceptMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createZoomMeeting();
                acceptMeetingButton.setVisibility(View.GONE);
                declineMeetingButton.setVisibility(View.GONE);
            }
        });
        declineMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMeetingStatus(Meeting.Status.REJECTED, "", "");
                acceptMeetingButton.setVisibility(View.GONE);
                declineMeetingButton.setVisibility(View.GONE);
                joinMeetingButton.setVisibility(View.GONE);
            }
        });
        joinMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getZoomMeetingInfo();
            }
        });
    }

    private void getZoomMeetingInfo() {

        String URL = Constants.URL + "meetingsById";

        JSONObject body = new JSONObject();

        try {
            body.put("mId", meeting.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getMeetingByIdRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray meetingInfo = response.getJSONArray("meeting");
                            JSONObject meeting = meetingInfo.getJSONObject(0);
                            String zoomId = meeting.getString("zoomId");
                            String zoomPassword = meeting.getString("zoomPassword");
                            Log.d(TAG, "zoom info: " + zoomId + " and " + zoomPassword);
                            joinZoomMeeting(EventActivity.this, zoomId, zoomPassword);
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

        requestQueue.add(getMeetingByIdRequest);
    }

    private void createZoomMeeting() {
        String URL = Constants.URL + "createZoomMeeting";

        JSONObject body = new JSONObject();

        Date startMeetingDate = meeting.getStartTime().getTime();
        Date endMeetingDate = meeting.getStartTime().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String meetingStartTime = formatter.format(startMeetingDate);
        String meetingEndTime = formatter.format(endMeetingDate);

        Log.d(TAG, "Start meeting time: " + meetingStartTime);
        Log.d(TAG, "End meeting time: " + meetingEndTime);

        try {
            body.put("meetingTopic", meeting.getName());
            body.put("meetingStartTime", meetingStartTime);
            body.put("meetingEndTime", meetingEndTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest createZoomMeetingRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Server resp: " + response.toString());
                        try {
                            JSONObject zoomMeetingData = response.getJSONObject("status");
                            String zoomMeetingId = zoomMeetingData.getString("id");
                            String zoomMeetingPassword = zoomMeetingData.getString("password");
                            updateMeetingStatus(Meeting.Status.ACCEPTED, zoomMeetingId, zoomMeetingPassword);
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

        requestQueue.add(createZoomMeetingRequest);
    }

    private void updateMeetingStatus(Meeting.Status status, String zoomId, String zoomPassword) {
        meeting.setStatus(status);
        System.out.println(status.name() + " " + meeting.getColor());

        String URL = Constants.URL + "meetings";

        JSONObject body = new JSONObject();
        try {
            body.put("mId", meeting.getId());
            body.put("status", meeting.getStatus().name());
            body.put("mColor", meeting.getColor());
            body.put("zoomId", zoomId);
            body.put("zoomPassword", zoomPassword);
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
                        if (status == Meeting.Status.ACCEPTED) {
                            schedulePayment();
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

        requestQueue.add(updateMeetingRequest);


        URL = Constants.URL + "sendMeetingResponse";
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

//        if (status == Meeting.Status.ACCEPTED) {
//            schedulePayment();
//        }
    }


    private void updateMeetingLog(JSONObject message) {
        String URL = Constants.URL + "updateMeetingLog";

        JSONObject body = new JSONObject();
        try {
            body.put("mId", meeting.getId());
            body.put("meetingLog", message);
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
    }

    private void schedulePayment() {
        String URL = Constants.URL + "schedulePayment";

        Calendar endTime = meeting.getEndTime();


        JSONObject endTimeObject = new JSONObject();
        try {
            endTimeObject.put("year", endTime.get(Calendar.YEAR));
            endTimeObject.put("month", endTime.get(Calendar.MONTH));
            endTimeObject.put("dayOfMonth", endTime.get(Calendar.DAY_OF_MONTH));
            endTimeObject.put("hourOfDay", endTime.get(Calendar.HOUR_OF_DAY));
            endTimeObject.put("minute", endTime.get(Calendar.MINUTE));
            endTimeObject.put("second", endTime.get(Calendar.SECOND));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject body = new JSONObject();
        try {
            body.put("mId", meeting.getId());
            body.put("mEndTime", endTimeObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest schedulePaymentRequest = new JsonObjectRequest(
                Request.Method.POST,
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

        requestQueue.add(schedulePaymentRequest);
    }

}