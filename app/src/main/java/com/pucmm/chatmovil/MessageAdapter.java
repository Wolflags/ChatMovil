package com.pucmm.chatmovil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_messages_recycler_row, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.senderNameTextView.setText(message.getSenderName());
        holder.timestampTextView.setText(sdf.format(message.getTimestamp().toDate()));

        if (message.getImageUrl() != null) {
            holder.messageContentTextView.setVisibility(View.GONE);
            holder.messageImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(message.getImageUrl()).into(holder.messageImageView);
        } else {
            holder.messageImageView.setVisibility(View.GONE);
            holder.messageContentTextView.setVisibility(View.VISIBLE);
            holder.messageContentTextView.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderNameTextView;
        TextView messageContentTextView;
        TextView timestampTextView;
        ImageView messageImageView;

        MessageViewHolder(View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            messageContentTextView = itemView.findViewById(R.id.messageContentTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
        }
    }
}