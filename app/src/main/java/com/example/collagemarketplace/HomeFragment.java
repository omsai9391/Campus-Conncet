package com.example.collagemarketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView itemsRv;
    ArrayList<Item> itemList;
    ItemAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    TextView userNameTv, userCollegeTv, userBranchTv, userYearTv;
    ImageView userProfileIv;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userNameTv = view.findViewById(R.id.userNameTv);
        userCollegeTv = view.findViewById(R.id.userCollegeTv);
        userBranchTv = view.findViewById(R.id.userBranchTv);
        userYearTv = view.findViewById(R.id.userYearTv);
        userProfileIv = view.findViewById(R.id.userProfileIv);

        itemsRv = view.findViewById(R.id.itemsRv);
        itemsRv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        itemsRv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadUserData();
        loadItems();

        return view;
    }

    private void loadUserData() {
        String uid = mAuth.getUid();
        if (uid != null) {
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String college = documentSnapshot.getString("collegeName");
                    String branch = documentSnapshot.getString("branch");
                    String year = documentSnapshot.getString("year");
                    String profileImg = documentSnapshot.getString("profileImage");

                    userNameTv.setText(name);
                    userCollegeTv.setText(college);
                    userBranchTv.setText(branch);
                    userYearTv.setText(year);

                    if (profileImg != null && !profileImg.isEmpty()) {
                        if (isAdded()) {
                            Glide.with(this).load(profileImg).into(userProfileIv);
                        }
                    }
                }
            });
        }
    }

    private void loadItems() {
        db.collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    itemList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Item item = doc.toObject(Item.class);
                        item.id = doc.getId();
                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
