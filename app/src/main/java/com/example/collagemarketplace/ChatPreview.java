package com.example.collagemarketplace;

public class ChatPreview {

    public String chatId;
    public String itemId;
    public String itemTitle;
    public String sellerId;
    public String buyerId;
    public String sellerName;
    public String buyerName;
    public String lastMessage;
    public Long timestamp; // Use Long wrapper to avoid primitive defaults

    public ChatPreview() {
        // Required for Firestore
    }
}
