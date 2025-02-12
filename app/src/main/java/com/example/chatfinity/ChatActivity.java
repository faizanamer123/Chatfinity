package com.example.chatfinity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.chatfinity.adapter.ChatRecycler;
import com.example.chatfinity.adapter.SearchUserRecycler;
import com.example.chatfinity.ChatMessage;
import com.example.chatfinity.chatting;
import com.example.chatfinity.UserModel;
import com.example.chatfinity.Androidapp;
import com.example.chatfinity.Firebaseapp;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.rpc.context.AttributeContext;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    String chatroomId;
    chatting chatroomModel;
    ChatRecycler adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = Androidapp.getUserModelFromIntent(getIntent());
        chatroomId = Firebaseapp.getChatroomId(Firebaseapp.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic);

        Firebaseapp.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        Androidapp.setProfilePic(this, uri, imageView);
                    }
                });

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        Query query = Firebaseapp.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).build();

        adapter = new ChatRecycler(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message) {

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(Firebaseapp.currentUserId());
        chatroomModel.setLastMessage(message);
        Firebaseapp.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessage chatMessageModel = new ChatMessage(message, Firebaseapp.currentUserId(), Timestamp.now());
        Firebaseapp.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                        }
                    }
                });
    }

    void getOrCreateChatroomModel() {
        Firebaseapp.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(chatting.class);
                if (chatroomModel == null) {
                    chatroomModel = new chatting(
                            chatroomId,
                            Arrays.asList(Firebaseapp.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    Firebaseapp.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}
