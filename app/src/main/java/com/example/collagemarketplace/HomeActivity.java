package com.example.collagemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button addItemBtn, logoutBtn;
    TextView welcomeTv;
    FirebaseAuth auth;
    RecyclerView itemsRv;
    ArrayList<Item> itemList;
    ItemAdapter adapter;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addItemBtn = findViewById(R.id.addItemBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        welcomeTv = findViewById(R.id.welcomeTv);

        auth = FirebaseAuth.getInstance();
        itemsRv = findViewById(R.id.itemsRv);
        itemsRv.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        itemsRv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();


        welcomeTv.setText("Welcome!");

        addItemBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AddItemActivity.class))
        );
        loadItems();


        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }
        private void loadItems() {
            db.collection("items")
                    .get()
                    .addOnSuccessListener(query -> {
                        itemList.clear();
                        for (QueryDocumentSnapshot doc : query) {
                            Item item = doc.toObject(Item.class);
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    });
    }
}
