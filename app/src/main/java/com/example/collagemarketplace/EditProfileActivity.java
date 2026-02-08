package com.example.collagemarketplace;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText nameEt, mobileEt, collegeEt, branchEt, yearEt;
    Button saveBtn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEt = findViewById(R.id.nameEt);
        mobileEt = findViewById(R.id.mobileEt);
        collegeEt = findViewById(R.id.collegeEt);
        branchEt = findViewById(R.id.branchEt);
        yearEt = findViewById(R.id.yearEt);
        saveBtn = findViewById(R.id.saveBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        saveBtn.setOnClickListener(v -> updateProfile());
    }

    private void loadUserData() {

        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        nameEt.setText(doc.getString("name"));
                        mobileEt.setText(doc.getString("mobile"));
                        collegeEt.setText(doc.getString("collegeName"));
                        branchEt.setText(doc.getString("branch"));
                        yearEt.setText(doc.getString("year"));
                    }
                });
    }

    private void updateProfile() {

        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        String name = nameEt.getText().toString().trim();
        String mobile = mobileEt.getText().toString().trim();
        String college = collegeEt.getText().toString().trim();
        String branch = branchEt.getText().toString().trim();
        String year = yearEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("mobile", mobile);
        updates.put("collegeName", college);
        updates.put("branch", branch);
        updates.put("year", year);

        db.collection("users")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
