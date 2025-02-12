package com.example.chatfinity.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfinity.Androidapp;
import com.example.chatfinity.ChatActivity;
import com.example.chatfinity.Firebaseapp;
import com.example.chatfinity.R;
import com.example.chatfinity.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecycler extends FirestoreRecyclerAdapter<UserModel,SearchUserRecycler.UserModelViewHolder> {
    Context context;

    public SearchUserRecycler(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(Firebaseapp.currentUserId())){
            holder.usernameText.setText(model.getUsername()+" (Me)");
        }

        Firebaseapp.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        Androidapp.setProfilePic(context,uri,holder.profilePic);
                    }
                });

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, ChatActivity.class);
            Androidapp.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);

        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic);

        }
    }
}