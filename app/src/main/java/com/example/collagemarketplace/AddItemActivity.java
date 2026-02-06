package com.example.collagemarketplace;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    EditText titleEt, priceEt, descriptionEt;
    Button postBtn;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        titleEt = findViewById(R.id.titleEt);
        priceEt = findViewById(R.id.priceEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        postBtn = findViewById(R.id.postBtn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        postBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Post button clicked", Toast.LENGTH_SHORT).show();
            postItem();
        });

    }

    private void postItem() {
        String title = titleEt.getText().toString().trim();
        String price = priceEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();


        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("price", price);
        item.put("description", description);
        item.put("sellerId", userId);
        item.put("timestamp", System.currentTimeMillis());

        db.collection("items")
                .add(item)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Item posted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
