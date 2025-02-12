package com.example.chatfinity.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfinity.Androidapp;
import com.example.chatfinity.ChatActivity;
import com.example.chatfinity.Firebaseapp;
import com.example.chatfinity.R;
import com.example.chatfinity.ChatMessage;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecycler extends FirestoreRecyclerAdapter<ChatMessage,ChatRecycler.ChatModelViewHolder> {
    Context context;

    public ChatRecycler(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessage model) {
         if(model.getSenderId().equals(Firebaseapp.currentUserId())){
             holder.leftChat.setVisibility(View.GONE);
             holder.rightChat.setVisibility(View.VISIBLE);
             holder.rightTv.setText(model.getMessage());
         }
         else{
             holder.rightChat.setVisibility(View.GONE);
             holder.leftChat.setVisibility(View.VISIBLE);
             holder.leftTv.setText(model.getMessage());
         }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChat , rightChat;
        TextView leftTv , rightTv;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChat = itemView.findViewById(R.id.left_chat_layout);
            rightChat = itemView.findViewById(R.id.right_chat_layout);
            leftTv = itemView.findViewById(R.id.left_chat_textview);
            rightTv = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}
