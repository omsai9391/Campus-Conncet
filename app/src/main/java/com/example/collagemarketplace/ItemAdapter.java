package com.example.collagemarketplace;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        List<Item> itemList;

        Button chatBtn;

        public ItemAdapter(List<Item> itemList) {
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Item item = itemList.get(position);
            holder.titleTv.setText(item.title);
            holder.priceTv.setText("₹ " + item.price);
            holder.descTv.setText(item.description);
            String currentUserId = FirebaseAuth.getInstance().getUid();

            if (item.sellerId != null && item.sellerId.equals(currentUserId)) {
                holder.chatBtn.setVisibility(View.GONE);
            } else {
                holder.chatBtn.setVisibility(View.VISIBLE);
            }

            holder.chatBtn.setOnClickListener(v -> {
                 Intent intent = new Intent(v.getContext(), ChatActivity.class);
                 intent.putExtra("itemId", item.id);
                 intent.putExtra("sellerId", item.sellerId);
                 intent.putExtra("itemTitle", item.title);
                 v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTv, priceTv, descTv;
            Button chatBtn;

            ViewHolder(View itemView) {
                super(itemView);
                titleTv = itemView.findViewById(R.id.titleTv);
                priceTv = itemView.findViewById(R.id.priceTv);
                descTv = itemView.findViewById(R.id.descTv);
                chatBtn = itemView.findViewById(R.id.chatBtn);
            }
        }
    }

