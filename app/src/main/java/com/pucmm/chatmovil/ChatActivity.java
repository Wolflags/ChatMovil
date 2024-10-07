package com.pucmm.chatmovil;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pucmm.chatmovil.adapter.ChatRecyclerAdapter;
import com.pucmm.chatmovil.models.ChatMessageModel;
import com.pucmm.chatmovil.models.ChatModel;
import com.pucmm.chatmovil.models.UserModel;
import com.pucmm.chatmovil.utils.AndroidUtil;
import com.pucmm.chatmovil.utils.FirebaseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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
    ImageView imageView;
    ImageButton imageSelectBtn;

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
        imageView = findViewById(R.id.profile_pic_image_view);
        imageSelectBtn = findViewById(R.id.image_select_btn);

        FirebaseUtil.getOtherProfilePicReference(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

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

        imageSelectBtn.setOnClickListener(view -> {
            ImagePicker.with(this).cropSquare().compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
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

    ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now(), null);
    FirebaseUtil.getChatMessageReference(chatId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
        @Override
        public void onComplete(@NonNull Task<DocumentReference> task) {
            messageInput.setText("");
            sendNotificationToUser(otherUser.getFcmToken(), "New Message", message);
        }
    });
}

private void sendNotificationToUser(String token, String title, String body) {
    new SendNotificationTask().execute(token, title, body);
}

private class SendNotificationTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        String token = params[0];
        String title = params[1];
        String body = params[2];

        try {
            Log.d(TAG, "TokenXDD: " + token);
            String url = "https://fcm.googleapis.com/v1/projects/chatfirebase-47aa5/messages:send";
            String serverKey = getAccessToken();

            JSONObject json = new JSONObject();
            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);
            message.put("token", token);
            message.put("notification", notification);
            json.put("message", message);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> Log.d(TAG, "Notification sent successfully"),
                error -> Log.e(TAG, "Error sending notification", error)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + serverKey);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
            requestQueue.add(jsonObjectRequest);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

    public static String getAccessToken() throws IOException {
        final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
        final String[] SCOPES = { MESSAGING_SCOPE };

        InputStream serviceAccountStream = ChatActivity.class
                .getClassLoader().getResourceAsStream("service-account.json");
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccountStream)
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
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
    ActivityResultLauncher<Intent> imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        uploadImageAndSendMessage(selectedImageUri);
                    }
                }
            }
    );

    private void uploadImageAndSendMessage(Uri imageUri) {
    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("chat_images").child(imageUri.getLastPathSegment());
    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            sendMessageWithImage(null, uri.toString());
        });
    });
}

private void sendMessageWithImage(String text, String imageUrl) {
    String senderId = FirebaseUtil.currentUserId();
    String senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    Timestamp timestamp = Timestamp.now();

    ChatMessageModel message = new ChatMessageModel(text, senderId, timestamp, imageUrl);
    FirebaseUtil.getChatMessageReference(chatId).add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
        @Override
        public void onComplete(@NonNull Task<DocumentReference> task) {
            sendNotificationToUser(otherUser.getFcmToken(), "New Image", "You have received a new image.");
        }
    });
}


}








