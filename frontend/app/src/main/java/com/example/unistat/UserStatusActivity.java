package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unistat.StatsCardView.ViewStatsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserStatusActivity extends AppCompatActivity {

    private Boolean isHighSchoolStudent = false;
    private Boolean checked = false;
    private FloatingActionButton nextButton;
    private Boolean shouldAllowBack = false;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        lottieAnimationView = findViewById(R.id.questionAnimation);

        nextButton = findViewById(R.id.nextUserStatusButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked) {
//                    Intent openSignOut = new Intent(UserStatusActivity.this, SignOutActivity.class);
                    Intent openUserProfile = new Intent(UserStatusActivity.this, CreateUserProfileActivity.class);
                    Intent openViewStats = new Intent(UserStatusActivity.this, ViewStatsActivity.class);
                    if (isHighSchoolStudent) // Mentee
                        startActivity(openViewStats);
                    else { // Mentor
                        startActivity(openUserProfile);
                    }

                } else {
                    Toast.makeText(UserStatusActivity.this, "Please select an option to continue", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * This method is an event handler which is triggered whenever a radio button,
     * receives an onClick event.
     * @param view
     */
    public void onRadioButtonChecked(View view) {
        checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.hsStudentButton:
                if (checked)
                    lottieAnimationView.setAnimation(R.raw.school);
                    lottieAnimationView.playAnimation();
                    isHighSchoolStudent = true;
                break;
            case R.id.univStudentButton:
                if (checked)
                    lottieAnimationView.setAnimation(R.raw.graduation);
                    lottieAnimationView.playAnimation();
                    isHighSchoolStudent = false;
                break;
        }
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