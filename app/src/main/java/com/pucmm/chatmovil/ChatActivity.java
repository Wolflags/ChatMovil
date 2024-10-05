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
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        userName = intent.getStringExtra("name");


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

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Map<String, Object> message = new HashMap<>();
            message.put("content", messageText);
            message.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.put("senderName", userName);
            message.put("timestamp", com.google.firebase.Timestamp.now());

            messagesRef.add(message)
                    .addOnSuccessListener(documentReference -> {
                        messageEditText.setText("");
                        Log.d(TAG, "Message sent successfully");
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error sending message", e));
        }
    }

    private void listenForMessages() {
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
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

