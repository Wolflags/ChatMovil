package com.pucmm.chatmovil;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatModel {
    String chatId;
    List<String> users;
    Timestamp lastMessageTime;
    String lastMessageSenderId;

    public ChatModel() {
    }

    public ChatModel(String chatId, List<String> list, Timestamp now, String s) {
        this.chatId = chatId;
        this.users = list;
        this.lastMessageTime = now;
        this.lastMessageSenderId = s;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
