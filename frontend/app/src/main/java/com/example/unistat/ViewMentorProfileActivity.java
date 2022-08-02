package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewMentorProfileActivity extends AppCompatActivity {
    private String mentorEmail;
    private String mentorName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mentor_profile);

        JSONObject currStat = null;
        String mentorPhotoUrl = null;
        String univName = null;
        String univMajor = null;
        String univGpa = null;
        String univEntranceScore = null;
        String univBio = null;

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

        Button requestMeetingButton = findViewById(R.id.requestMeetingButton);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        assert account != null;
        String userEmail = account.getEmail();
        assert userEmail != null;
        if (userEmail.equals(mentorEmail)) {
            requestMeetingButton.setVisibility(View.GONE);
        }


        CircleImageView mentorPhoto = findViewById(R.id.mentorProfileImage);
        TextView mentorNameText = findViewById(R.id.mentorNameText);
        TextView univNameText = findViewById(R.id.univNameText);
        TextView univMajorText = findViewById(R.id.univMajorText);
        TextView univGpaText = findViewById(R.id.univGpaText);
        TextView univEntranceScoreText = findViewById(R.id.univEntranceScoreText);
        TextView univBioText = findViewById(R.id.uniBioText);

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
        viewEvent.putExtra("mentorName", mentorName);
        startActivity(viewEvent);
        overridePendingTransition(R.anim.zm_enlarge_in, R.anim.zm_enlarge_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zm_tip_fadein, R.anim.zm_fade_out);
    }

}