package com.example.chatfinity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatfinity.adapter.RecentChatRecycler;
import com.example.chatfinity.adapter.SearchUserRecycler;
import com.example.chatfinity.chatting;
import com.example.chatfinity.UserModel;
import com.example.chatfinity.Firebaseapp;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Chat_fragment extends Fragment {
    RecyclerView recyclerView;
    RecentChatRecycler adapter;
    public Chat_fragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        setupRecyclerView();
        return view;
    }

    void setupRecyclerView(){

        Query query = Firebaseapp.allChatroomCollectionReference()
                .whereArrayContains("userIds",Firebaseapp.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatting> options = new FirestoreRecyclerOptions.Builder<chatting>()
                .setQuery(query,chatting.class).build();

        adapter = new RecentChatRecycler(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}