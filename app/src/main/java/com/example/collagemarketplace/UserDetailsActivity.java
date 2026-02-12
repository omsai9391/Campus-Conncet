package com.example.collagemarketplace;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView detailNameTv, detailCollegeTv, detailBranchTv, detailYearTv, detailMobileTv, detailEmailTv;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        detailNameTv = findViewById(R.id.detailNameTv);
        detailCollegeTv = findViewById(R.id.detailCollegeTv);
        detailBranchTv = findViewById(R.id.detailBranchTv);
        detailYearTv = findViewById(R.id.detailYearTv);
        detailMobileTv = findViewById(R.id.detailMobileTv);
        detailEmailTv = findViewById(R.id.detailEmailTv);

        db = FirebaseFirestore.getInstance();

        String userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            loadUserDetails(userId);
        } else {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserDetails(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        detailNameTv.setText(documentSnapshot.getString("name"));
                        detailCollegeTv.setText(documentSnapshot.getString("collegeName"));
                        detailBranchTv.setText(documentSnapshot.getString("branch"));
                        detailYearTv.setText(documentSnapshot.getString("year"));
                        detailMobileTv.setText(documentSnapshot.getString("mobile"));
                        detailEmailTv.setText(documentSnapshot.getString("email"));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
