package com.pucmm.chatmovil.models;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message;
    private String senderIc;
    private Timestamp timestamp;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderIc, Timestamp timestamp) {
        this.message = message;
        this.senderIc = senderIc;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderIc() {
        return senderIc;
    }

    public void setSenderIc(String senderIc) {
        this.senderIc = senderIc;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
