package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unistat.statscardview.ViewStatsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserStatusActivity extends AppCompatActivity {

    private Boolean isHighSchoolStudent = false;
    private Boolean checked = false;
    private LottieAnimationView questionAnimation;
    private LottieAnimationView schoolAnimation;
    private LottieAnimationView gradAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        questionAnimation = findViewById(R.id.questionAnimation);
        schoolAnimation = findViewById(R.id.schoolAnimation);
        gradAnimation = findViewById(R.id.graduationAnimation);

        FloatingActionButton nextButton = findViewById(R.id.nextUserStatusButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked) {
//                    Intent openSignOut = new Intent(UserStatusActivity.this, SignOutActivity.class);
                    Intent openUserProfile = new Intent(UserStatusActivity.this, CreateUserProfileActivity.class);
                    Intent openViewStats = new Intent(UserStatusActivity.this, ViewStatsActivity.class);
                    if (isHighSchoolStudent) {
                        startActivity(openViewStats);
//                        overridePendingTransition(R.anim.zm_slide_in_right, R.anim.zm_slide_out_left);
                        overridePendingTransition(0, 0);
                    }
                    else { // Mentor
                        startActivity(openUserProfile);
//                        overridePendingTransition(R.anim.zm_slide_in_right, R.anim.zm_slide_out_left);
                        overridePendingTransition(0, 0);
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
                if (checked) {
                    questionAnimation.setVisibility(View.GONE);
                    schoolAnimation.setVisibility(View.VISIBLE);
                    gradAnimation.setVisibility(View.GONE);
                }
                isHighSchoolStudent = true;
                break;
            case R.id.univStudentButton:
                if (checked) {
                    questionAnimation.setVisibility(View.GONE);
                    schoolAnimation.setVisibility(View.GONE);
                    gradAnimation.setVisibility(View.VISIBLE);
                }
                isHighSchoolStudent = false;
                break;
            default:
                //
        }
    }

    @Override
    public void onBackPressed() {
        Boolean shouldAllowBack = false;
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }
}