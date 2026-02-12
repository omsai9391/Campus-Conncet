package com.example.collagemarketplace;

public class Item {
    public String id, title, price, description, sellerId;
    public String imageUrl;
    public boolean sold = false; // Add this line

    public Item() {
        // required for Firebase
    }
}
