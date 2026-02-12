package com.example.collagemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEt, ageEt, mobileEt, collegeEt, branchEt, yearEt, rollEt, addressEt, emailEt, passwordEt, confirmPasswordEt;
    RadioGroup genderRg;
    Button registerBtn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEt = findViewById(R.id.nameEt);
        ageEt = findViewById(R.id.ageEt);
        mobileEt = findViewById(R.id.mobileEt);
        collegeEt = findViewById(R.id.collegeEt);
        branchEt = findViewById(R.id.branchEt);
        yearEt = findViewById(R.id.yearEt);
        rollEt = findViewById(R.id.rollEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        genderRg = findViewById(R.id.genderRg);
        registerBtn = findViewById(R.id.registerBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = nameEt.getText().toString().trim();
        String age = ageEt.getText().toString().trim();
        String mobile = mobileEt.getText().toString().trim();
        String college = collegeEt.getText().toString().trim();
        String branch = branchEt.getText().toString().trim();
        String year = yearEt.getText().toString().trim();
        String roll = rollEt.getText().toString().trim();
        String address = addressEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Name, email, and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mobile.length() != 10) {
            mobileEt.setError("Mobile number must be 10 digits");
            mobileEt.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEt.setError("Passwords do not match");
            confirmPasswordEt.requestFocus();
            return;
        }

        int selectedGenderId = genderRg.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedGenderRb = findViewById(selectedGenderId);
        String gender = selectedGenderRb.getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("age", age);
                        user.put("gender", gender);
                        user.put("mobile", mobile);
                        user.put("collegeName", college);
                        user.put("branch", branch);
                        user.put("year", year);
                        user.put("rollNumber", roll);
                        user.put("address", address);
                        user.put("email", email);

                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
