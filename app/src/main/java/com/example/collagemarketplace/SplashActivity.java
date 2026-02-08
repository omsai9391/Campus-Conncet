package com.example.collagemarketplace;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // User already logged in
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User not logged in
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}
