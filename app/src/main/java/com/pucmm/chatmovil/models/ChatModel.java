package com.pucmm.chatmovil.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatModel {
    String chatId;
    List<String> userIds;
    Timestamp lastMessageTime;
    String lastMessageSenderId;
    String lastMessage;

    public ChatModel() {
    }

    public ChatModel(String chatId, List<String> list, Timestamp now, String s) {
        this.chatId = chatId;
        this.userIds = list;
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

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
