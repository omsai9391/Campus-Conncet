package com.example.collagemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    TextView chatTitleTv;
    EditText messageEt;
    Button sendBtn;
    RecyclerView messagesRv;
    ArrayList<Message> messageList;
    MessageAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String chatId;
    String itemId;
    String sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 🔹 Initialize Firebase FIRST
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 🔹 Get intent data
        itemId = getIntent().getStringExtra("itemId");
        sellerId = getIntent().getStringExtra("sellerId");
        String itemTitle = getIntent().getStringExtra("itemTitle");

        if (itemId == null || sellerId == null) {
            Toast.makeText(this, "Chat data missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String buyerId = auth.getCurrentUser().getUid();
        chatId = itemId + "_" + buyerId;

        // 🔹 Now initialize UI
        chatTitleTv = findViewById(R.id.chatTitleTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        messagesRv = findViewById(R.id.messagesRv);

        chatTitleTv.setText("Chat about: " + itemTitle);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        messagesRv.setLayoutManager(new LinearLayoutManager(this));
        messagesRv.setAdapter(adapter);

        loadMessages();

        sendBtn.setOnClickListener(v -> sendMessage());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatPreview chat = chatList.get(position);

        String currentUserId = FirebaseAuth.getInstance().getUid();

        String otherUserId;

        if (currentUserId.equals(chat.sellerId)) {
            otherUserId = chat.buyerId;
        } else {
            otherUserId = chat.sellerId;
        }

        // 🔹 Set last message
        holder.lastMessageTv.setText(chat.lastMessage);

        // 🔹 Set time
        holder.timeTv.setText(formatTime(chat.timestamp));

        // 🔹 Load other user name
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(otherUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.nameTv.setText(documentSnapshot.getString("name"));
                    }
                });

        // 🔹 Open chat on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("itemId", chat.itemId);
            intent.putExtra("sellerId", chat.sellerId);
            intent.putExtra("itemTitle", chat.itemTitle);
            v.getContext().startActivity(intent);
        });
    }


    private void sendMessage() {

        String message = messageEt.getText().toString().trim();

        if (message.isEmpty()) return;
        if (auth.getCurrentUser() == null) return;

        String buyerId = auth.getCurrentUser().getUid();

        // 🔹 Create chat metadata FIRST
        Map<String, Object> chatInfo = new HashMap<>();
        chatInfo.put("itemId", itemId);
        chatInfo.put("sellerId", sellerId);
        chatInfo.put("buyerId", buyerId);
        chatInfo.put("lastMessage", message);
        chatInfo.put("timestamp", System.currentTimeMillis());

        db.collection("chats")
                .document(chatId)
                .set(chatInfo);

        // 🔹 Then save message inside messages collection
        Map<String, Object> msg = new HashMap<>();
        msg.put("text", message);
        msg.put("senderId", buyerId);
        msg.put("timestamp", System.currentTimeMillis());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(msg)
                .addOnSuccessListener(doc -> messageEt.setText(""))
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

}
