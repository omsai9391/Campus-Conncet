package com.example.collagemarketplace;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEt, mobileEt, collegeEt, branchEt, yearEt, addressEt, emailEt;
    private Button saveBtn;
    private ImageButton backBtn;
    private ImageView profileIv;
    private FloatingActionButton selectImageFab;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Uri imageUri;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Bind views
        nameEt = findViewById(R.id.nameEt);
        mobileEt = findViewById(R.id.mobileEt);
        collegeEt = findViewById(R.id.collegeEt);
        branchEt = findViewById(R.id.branchEt);
        yearEt = findViewById(R.id.yearEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        selectImageFab = findViewById(R.id.selectImageFab);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        backBtn.setOnClickListener(v -> finish());
        selectImageFab.setOnClickListener(v -> openGallery());
        
        loadUserData();

        saveBtn.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageAndSave();
            } else {
                updateProfile(profileImageUrl);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            profileIv.setImageURI(imageUri);
        }
    }

    private void loadUserData() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                nameEt.setText(doc.getString("name"));
                mobileEt.setText(doc.getString("mobile"));
                collegeEt.setText(doc.getString("collegeName"));
                branchEt.setText(doc.getString("branch"));
                yearEt.setText(doc.getString("year"));
                addressEt.setText(doc.getString("address"));
                emailEt.setText(doc.getString("email"));
                
                profileImageUrl = doc.getString("profileImage");
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this).load(profileImageUrl).into(profileIv);
                }
            }
        });
    }

    private void uploadImageAndSave() {
        saveBtn.setEnabled(false);
        saveBtn.setText("Uploading...");

        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storageRef.child("profile_images/" + fileName);

        ref.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateProfile(task.getResult().toString());
            } else {
                saveBtn.setEnabled(true);
                saveBtn.setText("Save Changes");
                Toast.makeText(this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile(String imageUrl) {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nameEt.getText().toString().trim());
        updates.put("mobile", mobileEt.getText().toString().trim());
        updates.put("collegeName", collegeEt.getText().toString().trim());
        updates.put("branch", branchEt.getText().toString().trim());
        updates.put("year", yearEt.getText().toString().trim());
        updates.put("address", addressEt.getText().toString().trim());
        if (imageUrl != null) updates.put("profileImage", imageUrl);

        db.collection("users").document(uid).update(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            saveBtn.setEnabled(true);
            saveBtn.setText("Save Changes");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
