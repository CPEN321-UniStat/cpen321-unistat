package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.unistat.StatsCardView.StatsCards;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.transform.sax.SAXResult;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewMentorProfileActivity extends AppCompatActivity {

    private static final String TAG = "MentorProfileActivity";
    private JSONObject currStat;
    private Button requestMeetingButton;
    private String mentorEmail;
    private String mentorName;
    private String mentorPhotoUrl;
    private String univName;
    private String univMajor;
    private String univGpa;
    private String univEntranceScore;
    private String univBio;
    private CircleImageView mentorPhoto;
    private TextView mentorNameText;
    private TextView univNameText;
    private TextView univMajorText;
    private TextView univGpaText;
    private TextView univEntranceScoreText;
    private TextView univBioText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mentor_profile);

        // Get data from card intent
        Intent cardIntent = getIntent();
        try {
            currStat = new JSONObject(cardIntent.getStringExtra("currStat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mentorEmail = currStat.getString("mentorEmail");
            mentorName = currStat.getString("mentorName");
            mentorPhotoUrl = currStat.getString("mentorPhoto");
            univName = currStat.getString("univName");
            univMajor = currStat.getString("univMajor");
            univGpa = currStat.getString("univGpa");
            univEntranceScore = currStat.getString("univEntranceScore");
            univBio = currStat.getString("univBio");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestMeetingButton = findViewById(R.id.requestMeetingButton);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        assert userEmail != null;
        if (userEmail.equals(mentorEmail)) {
            requestMeetingButton.setVisibility(View.GONE);
        }



        mentorPhoto = findViewById(R.id.mentorProfileImage);
        mentorNameText = findViewById(R.id.mentorNameText);
        univNameText = findViewById(R.id.univNameText);
        univMajorText = findViewById(R.id.univMajorText);
        univGpaText = findViewById(R.id.univGpaText);
        univEntranceScoreText = findViewById(R.id.univEntranceScoreText);
        univBioText = findViewById(R.id.uniBioText);

        mentorNameText.setText(mentorName);
        univNameText.setText(univName);
        univMajorText.setText(univMajor);
        univGpaText.setText(univGpa);
        univEntranceScoreText.setText(univEntranceScore);
        univBioText.setText(univBio);

        Picasso.get().load(mentorPhotoUrl).resize(135, 135).into(mentorPhoto);

    }

    public void requestMeeting(View view) {
        Intent viewEvent = new Intent(ViewMentorProfileActivity.this, RequestMeeting.class);
        viewEvent.putExtra("mentorEmail", mentorEmail);
        startActivity(viewEvent);
    }
}