package com.example.collagemarketplace;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message message = messageList.get(position);

        holder.messageTv.setText(message.text);

        String currentUserId = FirebaseAuth.getInstance().getUid();

        if (message.senderId != null && message.senderId.equals(currentUserId)) {
            // My message (Right side)
            holder.messageLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
            holder.messageLayout.setGravity(android.view.Gravity.END);
            holder.messageTv.setBackgroundResource(R.drawable.chat_right_bg);
        } else {
            // Other message (Left side)
            holder.messageLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
            holder.messageLayout.setGravity(android.view.Gravity.START);
            holder.messageTv.setBackgroundResource(R.drawable.chat_left_bg);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageTv;
        LinearLayout messageLayout;

        ViewHolder(View itemView) {
            super(itemView);
            messageTv = itemView.findViewById(R.id.messageTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}

