package com.pucmm.chatmovil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private FirebaseFirestore db;
    private CollectionReference messagesRef;
    private MessageAdapter adapter;
    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView recyclerView;

    private String currentUserId;
    private String otherUserId;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("otherUserId");
        chatId = generateChatId(currentUserId, otherUserId);

        db = FirebaseFirestore.getInstance();
        messagesRef = db.collection("messages");

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendButton.setOnClickListener(v -> sendMessage());

        listenForMessages();
    }

    private String generateChatId(String userId1, String userId2) {
        // Asegúrate de que el chatId sea único y consistente para la misma pareja de usuarios
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Map<String, Object> message = new HashMap<>();
            message.put("chatId", chatId);
            message.put("content", messageText);
            message.put("senderId", currentUserId);
            message.put("receiverId", otherUserId);
            message.put("timestamp", FieldValue.serverTimestamp());

            messagesRef.add(message)
                    .addOnSuccessListener(documentReference -> {
                        messageEditText.setText("");
                        Log.d(TAG, "Message sent successfully");
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error sending message", e));
        }
    }

    private void listenForMessages() {
        messagesRef.whereEqualTo("chatId", chatId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    List<Message> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        Message message = doc.toObject(Message.class);
                        messages.add(message);
                    }
                    adapter.updateMessages(messages);
                    recyclerView.scrollToPosition(messages.size() - 1);
                });
    }
}

