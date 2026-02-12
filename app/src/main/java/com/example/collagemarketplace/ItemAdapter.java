package com.example.collagemarketplace;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    List<Item> itemList;

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

        // Load image using Glide
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.imageUrl)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Show SOLD overlay if item is sold
        if (item.sold) {
            holder.soldTv.setVisibility(View.VISIBLE);
            holder.chatBtn.setEnabled(false);
            holder.chatBtn.setText("Item Sold");
        } else {
            holder.soldTv.setVisibility(View.GONE);
            holder.chatBtn.setEnabled(true);
            holder.chatBtn.setText("Chat with Seller");
        }

        // Handle visibility based on Seller/Buyer role
        if (item.sellerId != null && item.sellerId.equals(currentUserId)) {
            // I am the Seller
            holder.chatBtn.setVisibility(View.GONE);
            holder.sellerActionLayout.setVisibility(View.VISIBLE);

            // Handle "Mark as Sold" button
            if (item.sold) {
                holder.soldBtn.setVisibility(View.GONE);
            } else {
                holder.soldBtn.setVisibility(View.VISIBLE);
                holder.soldBtn.setOnClickListener(v -> markItemAsSold(item, holder.getAdapterPosition()));
            }

            holder.deleteBtn.setOnClickListener(v -> deleteItem(item, holder.getAdapterPosition()));

        } else {
            // I am the Buyer
            holder.chatBtn.setVisibility(View.VISIBLE);
            holder.sellerActionLayout.setVisibility(View.GONE);

            holder.chatBtn.setOnClickListener(v -> {
                if (item.sold) {
                    Toast.makeText(v.getContext(), "Sorry, this item is already sold!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("itemId", item.id);
                intent.putExtra("sellerId", item.sellerId);
                intent.putExtra("itemTitle", item.title);
                v.getContext().startActivity(intent);
            });
        }
    }

    private void markItemAsSold(Item item, int position) {
        FirebaseFirestore.getInstance()
                .collection("items")
                .document(item.id)
                .update("sold", true)
                .addOnSuccessListener(aVoid -> {
                    item.sold = true;
                    notifyItemChanged(position);
                    Toast.makeText(FirebaseAuth.getInstance().getApp().getApplicationContext(), "Marked as Sold", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteItem(Item item, int position) {
        FirebaseFirestore.getInstance()
                .collection("items")
                .document(item.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (position != RecyclerView.NO_POSITION) {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView titleTv, priceTv, descTv, soldTv;
        Button chatBtn, deleteBtn, soldBtn;
        LinearLayout sellerActionLayout;

        ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            titleTv = itemView.findViewById(R.id.titleTv);
            priceTv = itemView.findViewById(R.id.priceTv);
            descTv = itemView.findViewById(R.id.descTv);
            soldTv = itemView.findViewById(R.id.soldTv);
            chatBtn = itemView.findViewById(R.id.chatBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            soldBtn = itemView.findViewById(R.id.soldBtn);
            sellerActionLayout = itemView.findViewById(R.id.sellerActionLayout);
        }
    }
}
