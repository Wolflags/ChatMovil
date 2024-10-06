package com.pucmm.chatmovil.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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


}
