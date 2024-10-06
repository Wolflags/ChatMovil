package com.pucmm.chatmovil.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {


    public static String currentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users2").document(currentUserId());
    }

    public static CollectionReference allUsers() {
        return FirebaseFirestore.getInstance().collection("users2");
    }

    public static DocumentReference getChatReference(String chatId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatId);
    }

    public static CollectionReference getChatMessageReference(String chatId) {
        return getChatReference(chatId).collection("chats");
    }

    public static String getChatId(String userId1, String userId2) {
        if(userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUsers().document(userIds.get(1));
        }else{
            return allUsers().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicReference(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(FirebaseUtil.currentUserId());
    }

    public static StorageReference getOtherProfilePicReference(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(otherUserId);
    }


}
