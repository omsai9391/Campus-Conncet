package com.example.collagemarketplace;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddItemFragment extends Fragment {

    EditText titleEt, priceEt, descriptionEt;
    Button postBtn, selectImageBtn;
    ImageView itemImage;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;

    Uri imageUri;

    public AddItemFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Initialize views properly
        titleEt = view.findViewById(R.id.titleEt);
        priceEt = view.findViewById(R.id.priceEt);
        descriptionEt = view.findViewById(R.id.descriptionEt);
        postBtn = view.findViewById(R.id.postBtn);
        selectImageBtn = view.findViewById(R.id.selectImageBtn);
        itemImage = view.findViewById(R.id.itemImage);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        selectImageBtn.setOnClickListener(v -> openGallery());
        postBtn.setOnClickListener(v -> postItem());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {
            imageUri = data.getData();
            itemImage.setImageURI(imageUri);
        }
    }

    private void postItem() {

        String title = titleEt.getText().toString().trim();
        String price = priceEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(price)) {
            Toast.makeText(getContext(), "Fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) return;

        String sellerId = auth.getCurrentUser().getUid();

        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("price", price);
        item.put("description", description);
        item.put("sellerId", sellerId);
        item.put("timestamp", System.currentTimeMillis());

        // Only add imageUrl if image selected
        if (imageUri != null) {
            item.put("imageUrl", imageUri.toString());
        }

        db.collection("items")
                .add(item)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(getContext(), "Item Posted", Toast.LENGTH_SHORT).show();
                    titleEt.setText("");
                    priceEt.setText("");
                    descriptionEt.setText("");
                    itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    imageUri = null;
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }


}

