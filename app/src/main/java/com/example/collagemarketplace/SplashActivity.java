package com.example.collagemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ImageView logoIv;
    private TextView appNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        logoIv = findViewById(R.id.logoIv);
        appNameTv = findViewById(R.id.appNameTv);

        // LinkedIn-style animation: Logo scale up and then Text fade in
        logoIv.setScaleX(0f);
        logoIv.setScaleY(0f);

        logoIv.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .withEndAction(() -> {
                    appNameTv.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start();
                })
                .start();

        // Delay for splash screen then transition
        new Handler().postDelayed(() -> {
            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 2500);
    }
}
