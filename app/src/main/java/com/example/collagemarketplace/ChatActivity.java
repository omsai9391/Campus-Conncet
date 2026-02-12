package com.example.collagemarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private TextView chatNameTv, chatItemTv;
    private EditText messageEt;
    private ImageButton sendBtn, backBtn;
    private RecyclerView messagesRv;
    private LinearLayout headerInfoLayout, inputLayout;

    private ArrayList<Message> messageList;
    private MessageAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String chatId, itemId, itemTitle, sellerId, buyerId, sellerName, buyerName, currentUserId, otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        currentUserId = auth.getCurrentUser().getUid();

        itemId = getIntent().getStringExtra("itemId");
        sellerId = getIntent().getStringExtra("sellerId");
        itemTitle = getIntent().getStringExtra("itemTitle");
        sellerName = getIntent().getStringExtra("sellerName");
        buyerName = getIntent().getStringExtra("buyerName");
        buyerId = getIntent().getStringExtra("buyerId");
        if (buyerId == null) {
            buyerId = currentUserId;
        }

        if (itemId == null || sellerId == null) {
            Toast.makeText(this, "Error: Chat data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (sellerId.compareTo(buyerId) < 0) {
            chatId = itemId + "_" + sellerId + "_" + buyerId;
        } else {
            chatId = itemId + "_" + buyerId + "_" + sellerId;
        }

        // Determine other user's ID for profile view
        otherUserId = currentUserId.equals(sellerId) ? buyerId : sellerId;

        chatNameTv = findViewById(R.id.chatNameTv);
        chatItemTv = findViewById(R.id.chatItemTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);
        messagesRv = findViewById(R.id.messagesRv);
        headerInfoLayout = findViewById(R.id.headerInfoLayout);
        inputLayout = findViewById(R.id.inputLayout);

        String otherUserName = currentUserId.equals(sellerId) ? buyerName : sellerName;
        chatNameTv.setText(otherUserName != null ? otherUserName : "Chat");
        chatItemTv.setText(itemTitle != null ? "Item: " + itemTitle : "");

        backBtn.setOnClickListener(v -> finish());
        
        // Click header to see user profile
        headerInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, UserDetailsActivity.class);
            intent.putExtra("userId", otherUserId);
            startActivity(intent);
        });

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        messagesRv.setLayoutManager(manager);
        messagesRv.setAdapter(adapter);

        loadMessages();
        checkItemStatus(); // Monitor if item is sold or deleted
        sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void checkItemStatus() {
        db.collection("items").document(itemId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) return;
                    
                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        // Item was deleted
                        disableChat("This item has been removed by the seller.");
                        return;
                    }
                    
                    Boolean isSold = documentSnapshot.getBoolean("sold");
                    if (isSold != null && isSold) {
                        // Item was marked as sold
                        disableChat("This item is sold. Chat disabled.");
                    }
                });
    }

    private void disableChat(String message) {
        messageEt.setEnabled(false);
        messageEt.setHint(message);
        sendBtn.setEnabled(false);
        sendBtn.setAlpha(0.5f);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadMessages() {
        db.collection("chats").document(chatId).collection("messages").orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value == null) return;
                    messageList.clear();
                    value.forEach(doc -> {
                        Message message = doc.toObject(Message.class);
                        if (message != null) messageList.add(message);
                    });
                    adapter.notifyDataSetChanged();
                    if (!messageList.isEmpty()) {
                        messagesRv.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = messageEt.getText().toString().trim();
        if (text.isEmpty()) return;

        long time = System.currentTimeMillis();

        Map<String, Object> msg = new HashMap<>();
        msg.put("text", text);
        msg.put("senderId", currentUserId);
        msg.put("timestamp", time);

        db.collection("chats").document(chatId).collection("messages").add(msg);

        Map<String, Object> chatData = new HashMap<>();
        chatData.put("chatId", chatId);
        chatData.put("sellerId", sellerId);
        chatData.put("buyerId", buyerId);
        chatData.put("sellerName", sellerName);
        chatData.put("buyerName", buyerName);
        chatData.put("itemId", itemId);
        chatData.put("itemTitle", itemTitle);
        chatData.put("lastMessage", text);
        chatData.put("timestamp", time);

        db.collection("chats").document(chatId).set(chatData, SetOptions.merge());

        messageEt.setText("");
    }
}
