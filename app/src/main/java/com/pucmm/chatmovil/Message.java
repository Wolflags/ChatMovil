package com.pucmm.chatmovil;

import com.google.firebase.Timestamp;

public class Message {
    private String content;
    private String senderId;
    private String senderName;
    private Timestamp timestamp;

    // Constructor vacío necesario para Firestore
    public Message() {}

    public Message(String content, String senderId, String senderName, Timestamp timestamp) {
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}