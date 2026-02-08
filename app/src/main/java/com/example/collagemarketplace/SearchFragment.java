package com.example.collagemarketplace;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    EditText searchEt, minPriceEt, maxPriceEt;
    Button filterBtn;
    RecyclerView searchRv;
    ImageView searchBtn;
    TextView noResultsTv;


    ArrayList<Item> itemList;
    ArrayList<Item> filteredList;
    ItemAdapter adapter;

    FirebaseFirestore db;

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEt = view.findViewById(R.id.searchEt);
        minPriceEt = view.findViewById(R.id.minPriceET);
        maxPriceEt = view.findViewById(R.id.maxPriceEt);
        filterBtn = view.findViewById(R.id.filterBtn);
        searchRv = view.findViewById(R.id.searchRv);
        searchBtn = view.findViewById(R.id.searchBtn);
        noResultsTv = view.findViewById(R.id.noResultsTv);

        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new ItemAdapter(filteredList);
        searchRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchRv.setAdapter(adapter);



        db = FirebaseFirestore.getInstance();

        loadItems();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        searchBtn.setOnClickListener(v ->
                filterByTitle(searchEt.getText().toString())
        );
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            filterByTitle(searchEt.getText().toString());
            return true;
        });

        filteredList.clear();
        adapter.notifyDataSetChanged();


        filterBtn.setOnClickListener(v -> filterByPrice());

        return view;
    }

    private void loadItems() {
        db.collection("items")
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    itemList.clear();

                    value.forEach(doc -> {
                        Item item = doc.toObject(Item.class);
                        item.id = doc.getId();
                        itemList.add(item);
                    });

                });
    }

    private void filterByTitle(String query) {

        filteredList.clear();

        for (Item item : itemList) {
            if (item.title != null &&
                    item.title.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            noResultsTv.setVisibility(View.VISIBLE);
        } else {
            noResultsTv.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }


    private void filterByPrice() {

        String minStr = minPriceEt.getText().toString();
        String maxStr = maxPriceEt.getText().toString();

        int min = minStr.isEmpty() ? 0 : Integer.parseInt(minStr);
        int max = maxStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxStr);

        filteredList.clear();

        for (Item item : itemList) {
            try {
                int price = Integer.parseInt(item.price);
                if (price >= min && price <= max) {
                    filteredList.add(item);
                }
            } catch (Exception ignored) {}
        }

        adapter.notifyDataSetChanged();
        if (filteredList.isEmpty()) {
            noResultsTv.setVisibility(View.VISIBLE);
        } else {
            noResultsTv.setVisibility(View.GONE);
        }

    }
}
