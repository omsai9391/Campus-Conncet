package com.example.collagemarketplace;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEt, codeEt, newPasswordEt, confirmPasswordEt;
    private Button actionBtn;
    private ImageButton backBtn;
    private LinearLayout resetFieldsLayout;

    private FirebaseAuth mAuth;
    private boolean isCodeSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Bind views
        emailEt = findViewById(R.id.emailEt);
        codeEt = findViewById(R.id.codeEt);
        newPasswordEt = findViewById(R.id.newPasswordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        actionBtn = findViewById(R.id.actionBtn);
        backBtn = findViewById(R.id.backBtn);
        resetFieldsLayout = findViewById(R.id.resetFieldsLayout);

        mAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(v -> finish());

        actionBtn.setOnClickListener(v -> {
            if (!isCodeSent) {
                sendResetEmail();
            } else {
                Toast.makeText(this, "Please use the link in your email to reset your password.", Toast.LENGTH_LONG).show();
                // Note: Standard Firebase Auth doesn't support direct password resets via 'code' in the app
                // It requires the user to click the link in the email.
            }
        });
    }

    private void sendResetEmail() {
        String email = emailEt.getText().toString().trim();

        if (email.isEmpty()) {
            emailEt.setError("Email is required!");
            emailEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Please provide valid email!");
            emailEt.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    isCodeSent = true;
                    Toast.makeText(ForgotPasswordActivity.this, "Reset link sent! Please check your email inbox.", Toast.LENGTH_LONG).show();
                    
                    // Show password fields to guide the user
                    resetFieldsLayout.setVisibility(View.VISIBLE);
                    actionBtn.setText("I have reset my password");
                    actionBtn.setOnClickListener(v -> finish()); // Allow them to go back to login
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
