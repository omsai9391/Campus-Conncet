package com.example.collagemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    TextView nameTv, emailTv, collegeTv;
    Button editProfileBtn, logoutBtn;
    RecyclerView myItemsRv;
    ArrayList<Item> myItemList;
    ItemAdapter itemAdapter;


    FirebaseAuth auth;
    FirebaseFirestore db;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        collegeTv = view.findViewById(R.id.collegeTv);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadProfile();
        editProfileBtn.setOnClickListener(v ->
                startActivity(new Intent(getContext(), EditProfileActivity.class))
        );


        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });
        myItemsRv = view.findViewById(R.id.myItemsRv);

        myItemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(myItemList);

        myItemsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        myItemsRv.setAdapter(itemAdapter);

        loadMyItems();


        return view;
    }

    private void loadProfile() {

        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {
                        nameTv.setText("Name: " + doc.getString("name"));
                        emailTv.setText("Email: " + doc.getString("email"));
                        collegeTv.setText("College: " + doc.getString("collegeName"));
                    }
                });
    }
    private void loadMyItems() {

        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("items")
                .whereEqualTo("sellerId", uid)
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    myItemList.clear();

                    value.forEach(doc -> {
                        Item item = doc.toObject(Item.class);
                        item.id = doc.getId();   // VERY IMPORTANT
                        myItemList.add(item);
                    });

                    itemAdapter.notifyDataSetChanged();
                });
    }

}
