package com.example.collagemarketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView itemsRv;
    ArrayList<Item> itemList;
    ItemAdapter adapter;
    FirebaseFirestore db;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        itemsRv = view.findViewById(R.id.itemsRv);
        itemsRv.setLayoutManager(new GridLayoutManager(getContext(),2 ));

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        itemsRv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadItems();

        return view;
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
