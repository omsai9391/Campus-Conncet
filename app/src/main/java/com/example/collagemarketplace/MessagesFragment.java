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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    RecyclerView inboxRv;
    ArrayList<ChatPreview> chatList;
    ChatPreviewAdapter adapter;


    FirebaseFirestore db;
    FirebaseAuth auth;

    public MessagesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        inboxRv = view.findViewById(R.id.inboxRv);
        inboxRv.setLayoutManager(new LinearLayoutManager(getContext()));

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

        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("chats")
                .whereIn("sellerId", java.util.Arrays.asList(currentUserId))
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    chatList.clear();

                    value.forEach(doc -> {
                        ChatPreview chat = doc.toObject(ChatPreview.class);
                        chatList.add(chat);
                    });

                    adapter.notifyDataSetChanged();
                });

        db.collection("chats")
                .whereEqualTo("buyerId", currentUserId)
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    value.forEach(doc -> {
                        ChatPreview chat = doc.toObject(ChatPreview.class);
                        if (!chatList.contains(chat)) {
                            chatList.add(chat);
                        }
                    });

                    adapter.notifyDataSetChanged();
                });
    }

}
