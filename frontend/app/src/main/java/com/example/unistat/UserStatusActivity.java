package com.example.unistat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class UserStatusActivity extends AppCompatActivity {

    private Boolean isHighSchoolStudent = false;
    private Boolean checked = false;
    private Button nextButton;
    private Boolean shouldAllowBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        nextButton = findViewById(R.id.nextUserStatusButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked) {
//                    Intent openSignOut = new Intent(UserStatusActivity.this, SignOutActivity.class);
                    Intent openUserProfile = new Intent(UserStatusActivity.this, UserProfileActivity.class);
                    Intent openViewStats = new Intent(UserStatusActivity.this, ViewStatsActivity.class);
                    if (isHighSchoolStudent) // Mentee
                        startActivity(openViewStats);
                    else { // Mentor
                        Bundle mainActivityBundle = getIntent().getExtras();
                        String userEmailId = mainActivityBundle.getString("userEmailId");
                        Bundle profileBundle = new Bundle();
                        profileBundle.putString("userEmailId", userEmailId);
                        openUserProfile.putExtras(profileBundle);
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
                    isHighSchoolStudent = true;
                break;
            case R.id.univStudentButton:
                if (checked)
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