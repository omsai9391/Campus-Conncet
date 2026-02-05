package com.example.collagemarketplace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTv, priceTv, descTv;

            ViewHolder(View itemView) {
                super(itemView);
                titleTv = itemView.findViewById(R.id.titleTv);
                priceTv = itemView.findViewById(R.id.priceTv);
                descTv = itemView.findViewById(R.id.descTv);
            }
        }
    }

