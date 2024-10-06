package com.pucmm.chatmovil;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.pucmm.chatmovil.adapter.ChatRecyclerAdapter;
import com.pucmm.chatmovil.adapter.SearchUserRecyclerAdapter;
import com.pucmm.chatmovil.models.ChatMessageModel;
import com.pucmm.chatmovil.models.ChatModel;
import com.pucmm.chatmovil.models.UserModel;
import com.pucmm.chatmovil.utils.AndroidUtil;
import com.pucmm.chatmovil.utils.FirebaseUtil;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatId;
    ChatModel chatModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton messageSendBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatId = FirebaseUtil.getChatId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        messageSendBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener((v) -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        otherUsername.setText(otherUser.getName());

        messageSendBtn.setOnClickListener((v) -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message);
        });

        getOrCreateChat();
        setupChatRecyclerView();
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatMessageReference(chatId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();


        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    void sendMessageToUser(String message) {

        chatModel.setLastMessageTime(Timestamp.now());
        chatModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatModel.setLastMessage(message);
        FirebaseUtil.getChatReference(chatId).set(chatModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatMessageReference(chatId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                messageInput.setText("");
            }
        });
    }

    private void getOrCreateChat() {

        FirebaseUtil.getChatReference(chatId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatModel = task.getResult().toObject(ChatModel.class);
                    if (chatModel == null) {
                        chatModel = new ChatModel(chatId, Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()), Timestamp.now(), "");
                        FirebaseUtil.getChatReference(chatId).set(chatModel);
                    }
                }
            }
        });

    }


}
