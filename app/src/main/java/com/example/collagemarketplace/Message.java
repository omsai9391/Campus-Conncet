package com.example.collagemarketplace;

public class Message {

    public String text;
    public String senderId;
    public long timestamp;

    public Message() {}

    public Message(String text, String senderId, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
}
