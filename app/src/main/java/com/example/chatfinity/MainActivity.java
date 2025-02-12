package com.example.chatfinity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    ImageButton searchbtn;
    BottomNavigationView navigationView;
    Chat_fragment chatFragment;
    Profile_fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       chatFragment = new Chat_fragment();
       profileFragment = new Profile_fragment();
       navigationView = findViewById(R.id.bottom_nav);
       searchbtn = findViewById(R.id.main_search);

        searchbtn.setOnClickListener((v)->{
            startActivity(new Intent(MainActivity.this,SearchUser.class));
        });

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,profileFragment).commit();
                }
                return true;
            }
        });
        navigationView.setSelectedItemId(R.id.menu_chat);
        getFCMToken();
    }

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Firebaseapp.currentUserDetails().update("fcmToken",token);
            }
        });
    }

}