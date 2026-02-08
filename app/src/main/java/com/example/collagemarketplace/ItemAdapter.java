package com.example.collagemarketplace;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // Hide chat button if seller is current user
        if (item.sellerId != null && item.sellerId.equals(currentUserId)) {

            // Seller view
            holder.chatBtn.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.VISIBLE);

            holder.deleteBtn.setOnClickListener(v -> {

                FirebaseFirestore.getInstance()
                        .collection("items")
                        .document(item.id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {

                            int currentPosition = holder.getAdapterPosition();

                            if (currentPosition != RecyclerView.NO_POSITION) {
                                itemList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, itemList.size());
                            }

                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(v.getContext(),
                                        "Delete failed",
                                        Toast.LENGTH_SHORT).show()
                        );
            });


        } else {

            // Buyer view
            holder.chatBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setVisibility(View.GONE);

            holder.chatBtn.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("itemId", item.id);
                intent.putExtra("sellerId", item.sellerId);
                intent.putExtra("itemTitle", item.title);
                v.getContext().startActivity(intent);
            });
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView titleTv, priceTv, descTv;
        Button chatBtn, deleteBtn;

        ViewHolder(View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            titleTv = itemView.findViewById(R.id.titleTv);
            priceTv = itemView.findViewById(R.id.priceTv);
            descTv = itemView.findViewById(R.id.descTv);
            chatBtn = itemView.findViewById(R.id.chatBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

}
