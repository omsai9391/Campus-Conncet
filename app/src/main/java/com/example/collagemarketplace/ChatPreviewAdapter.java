package com.example.collagemarketplace;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {

    private List<ChatPreview> chatList;
    private String currentUserId;
    
    private final int[] circleColors = {
        0xFF00BFA5, 0xFF00796B, 0xFF3F51B5, 0xFFE91E63, 0xFF9C27B0, 0xFFF44336
    };

    public ChatPreviewAdapter(List<ChatPreview> chatList) {
        this.chatList = chatList;
        this.currentUserId = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatPreview chat = chatList.get(position);
        if (chat == null) return;

        String displayName;
        // If I am the buyer, show the seller's name
        if (currentUserId != null && currentUserId.equals(chat.buyerId)) {
            displayName = (chat.sellerName != null && !chat.sellerName.isEmpty()) ? chat.sellerName : "Seller";
        } 
        // If I am the seller, show the buyer's name
        else if (currentUserId != null && currentUserId.equals(chat.sellerId)) {
            displayName = (chat.buyerName != null && !chat.buyerName.isEmpty()) ? chat.buyerName : "Buyer";
        }
        else {
            displayName = "Chat";
        }

        holder.nameTv.setText(displayName);
        holder.itemTitleTv.setText(chat.itemTitle != null ? "Item: " + chat.itemTitle : "");
        holder.lastMessageTv.setText(chat.lastMessage != null ? chat.lastMessage : "No messages yet");
        holder.timeTv.setText(formatTime(chat.timestamp));

        int colorIndex = Math.abs(displayName.hashCode()) % circleColors.length;
        holder.profileInitial.getBackground().setColorFilter(circleColors[colorIndex], PorterDuff.Mode.SRC_IN);
        
        if (!displayName.isEmpty()) {
            holder.profileInitial.setText(displayName.substring(0, 1).toUpperCase());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("itemId", chat.itemId);
            intent.putExtra("sellerId", chat.sellerId);
            intent.putExtra("buyerId", chat.buyerId);
            intent.putExtra("itemTitle", chat.itemTitle);
            intent.putExtra("sellerName", chat.sellerName);
            intent.putExtra("buyerName", chat.buyerName);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv, lastMessageTv, timeTv, profileInitial, itemTitleTv;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            profileInitial = itemView.findViewById(R.id.profileInitial);
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
        }
    }

    private String formatTime(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
