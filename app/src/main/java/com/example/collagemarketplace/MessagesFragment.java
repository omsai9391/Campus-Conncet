package com.example.collagemarketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private RecyclerView inboxRv;
    private ArrayList<ChatPreview> chatList;
    private ChatPreviewAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Use a Map to merge results from two queries and avoid duplicates
    private Map<String, ChatPreview> chatMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        inboxRv = view.findViewById(R.id.inboxRv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        inboxRv.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(inboxRv.getContext(), layoutManager.getOrientation());
        inboxRv.addItemDecoration(divider);

        chatList = new ArrayList<>();
        adapter = new ChatPreviewAdapter(chatList);
        inboxRv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadChats();

        return view;
    }

    private void loadChats() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        // 🔹 Strategy: We run two simple queries and merge them locally.
        // This avoids the "Missing Index" error and works instantly.
        
        // 1. Listen for chats where I am the SELLER
        db.collection("chats")
                .whereEqualTo("sellerId", uid)
                .addSnapshotListener((value, error) -> handleChatUpdate(value, error));

        // 2. Listen for chats where I am the BUYER
        db.collection("chats")
                .whereEqualTo("buyerId", uid)
                .addSnapshotListener((value, error) -> handleChatUpdate(value, error));
    }

    private void handleChatUpdate(com.google.firebase.firestore.QuerySnapshot value, com.google.firebase.firestore.FirebaseFirestoreException error) {
        if (!isAdded()) return;

        if (error != null) {
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (value != null) {
            for (DocumentSnapshot doc : value.getDocuments()) {
                ChatPreview chat = doc.toObject(ChatPreview.class);
                if (chat != null) {
                    chat.chatId = doc.getId();
                    chatMap.put(doc.getId(), chat); // Merge by ID
                }
            }
            updateUI();
        }
    }

    private void updateUI() {
        chatList.clear();
        chatList.addAll(chatMap.values());

        // Sort by timestamp descending (WhatsApp style: newest at top)
        Collections.sort(chatList, (c1, c2) -> {
            long t1 = (c1.timestamp != null) ? c1.timestamp : 0;
            long t2 = (c2.timestamp != null) ? c2.timestamp : 0;
            return Long.compare(t2, t1);
        });

        adapter.notifyDataSetChanged();
    }
}
