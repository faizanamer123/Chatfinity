package com.example.chatfinity.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfinity.ChatActivity;
import com.example.chatfinity.R;
import com.example.chatfinity.chatting;
import com.example.chatfinity.UserModel;
import com.example.chatfinity.Androidapp;
import com.example.chatfinity.Firebaseapp;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecycler extends FirestoreRecyclerAdapter<chatting, RecentChatRecycler.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecycler(@NonNull FirestoreRecyclerOptions<chatting> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull chatting model) {
        Firebaseapp.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(Firebaseapp.currentUserId());
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        Firebaseapp.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri  = t.getResult();
                                        Androidapp.setProfilePic(context,uri,holder.profilePic);
                                    }
                                });

                        holder.usernameText.setText(otherUserModel.getUsername());
                        if(lastMessageSentByMe)
                            holder.lastMessageText.setText("You : "+model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(Firebaseapp.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatActivity.class);
                            Androidapp.passUserModelAsIntent(intent,otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_image_view);
        }
    }
}