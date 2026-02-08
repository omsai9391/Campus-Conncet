package com.example.collagemarketplace;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {

    List<ChatPreview> chatList;

    public ChatPreviewAdapter(List<ChatPreview> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_preview_row, parent, false);
        return new ViewHolder(v);
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

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv, lastMessageTv, timeTv;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date(timestamp));
    }
}

