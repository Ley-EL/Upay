package com.example.upay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upay.UserConnections.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // initialize firebase variable
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // initialize handler
        handler = new Handler();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            CheckStatusUser();
        }
    };

    private void CheckStatusUser() {
        // check if user is already connected
        Intent intent;
        if (user != null) {
            // if user is already logging, redirect to main activity
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        } else {
            // otherwise redirect to Log In page
            intent = new Intent(SplashScreenActivity.this, LoginActivity.class);

        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}