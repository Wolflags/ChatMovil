package com.pucmm.chatmovil.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pucmm.chatmovil.ChatActivity;
import com.pucmm.chatmovil.R;
import com.pucmm.chatmovil.models.ChatModel;
import com.pucmm.chatmovil.models.UserModel;
import com.pucmm.chatmovil.utils.AndroidUtil;
import com.pucmm.chatmovil.utils.FirebaseUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatModel model) {
    FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
            .get().addOnCompleteListener(task -> {
        if(task.isSuccessful()) {

            boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

            UserModel otherUserModel = task.getResult().toObject(UserModel.class);
            holder.username.setText(otherUserModel.getName());
            if(lastMessageSentByMe){
                holder.lastMessageText.setText("Tú: " + model.getLastMessage());
            }else{
                holder.lastMessageText.setText(model.getLastMessage());
            }

            // Formatear la hora del último mensaje
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(model.getLastMessageTime().toDate());
            holder.lastMessageTime.setText(formattedTime);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

        }
    });
}

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

}
