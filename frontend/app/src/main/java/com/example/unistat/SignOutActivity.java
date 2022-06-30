package com.example.unistat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SignOutActivity extends AppCompatActivity {

    private static final String TAG = "signOutActivity";
    private Button signOutButton;
    private GoogleSignInClient mGoogleSignInClient;
    private Boolean shouldAllowBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.sign_out_activity);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.calendar_activity:
                        startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.view_stats_activity:
                        startActivity(new Intent(getApplicationContext(),ViewStatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.sign_out_activity:
                        return true;
                }
                return false;
            }
        });

    }

    private void signOut() {
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "logged out");
                        Intent openMainActivity = new Intent(SignOutActivity.this, MainActivity.class);
                        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(openMainActivity);
                    }
                });
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