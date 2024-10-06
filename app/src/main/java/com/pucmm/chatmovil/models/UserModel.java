package com.pucmm.chatmovil.models;

public class UserModel {
    private String name;
    private String email;
    private String profilePicUrl;
    private String userId;

    public UserModel() {
        // Constructor vacío necesario para Firestore
    }

    public UserModel(String username, String email, String profilePicUrl, String userId) {
        this.name = username;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}