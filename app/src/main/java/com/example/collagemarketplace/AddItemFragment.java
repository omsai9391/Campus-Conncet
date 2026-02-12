package com.example.collagemarketplace;

import android.app.Activity;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

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
        postBtn.setOnClickListener(v -> uploadImageAndPost());

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
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            itemImage.setImageURI(imageUri);
        }
    }

    private void uploadImageAndPost() {
        String title = titleEt.getText().toString().trim();
        String price = priceEt.getText().toString().trim();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(price)) {
            Toast.makeText(getContext(), "Title and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            postItem(null); 
        } else {
            postBtn.setEnabled(false);
            postBtn.setText("Uploading...");
            
            String fileName = UUID.randomUUID().toString();
            final StorageReference ref = storageRef.child("item_images/" + fileName);

            UploadTask uploadTask = ref.putFile(imageUri);
            
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    if (task.getException() != null) {
                        throw task.getException();
                    }
                }
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        postItem(downloadUri.toString());
                    }
                } else {
                    postBtn.setEnabled(true);
                    postBtn.setText("Post Item");
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(getContext(), "Upload failed: " + error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void postItem(String downloadUrl) {
        if (auth.getCurrentUser() == null) return;
        String sellerId = auth.getCurrentUser().getUid();

        Map<String, Object> item = new HashMap<>();
        item.put("title", titleEt.getText().toString().trim());
        item.put("price", priceEt.getText().toString().trim());
        item.put("description", descriptionEt.getText().toString().trim());
        item.put("sellerId", sellerId);
        item.put("timestamp", System.currentTimeMillis());
        item.put("sold", false);
        if (downloadUrl != null) item.put("imageUrl", downloadUrl);

        db.collection("items").add(item).addOnSuccessListener(doc -> {
            Toast.makeText(getContext(), "Item Posted Successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        }).addOnFailureListener(e -> {
            postBtn.setEnabled(true);
            postBtn.setText("Post Item");
            Toast.makeText(getContext(), "Post failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void clearFields() {
        titleEt.setText("");
        priceEt.setText("");
        descriptionEt.setText("");
        itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        imageUri = null;
        postBtn.setEnabled(true);
        postBtn.setText("Post Item");
    }
}
