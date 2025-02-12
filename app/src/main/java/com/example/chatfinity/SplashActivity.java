package com.example.chatfinity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_acitivty);

        // Delay splash screen visibility
        new Handler().postDelayed(() -> {
            if (getIntent().getExtras() != null) {
                // Handle notification payload if any
                handleNotificationIntent();
            } else {
                // Regular splash screen navigation logic
                navigateAfterSplash();
            }
        }, SPLASH_DELAY);
    }
    private void handleNotificationIntent() {
        String userId = getIntent().getExtras().getString("userId");

        // Validate userId
        if (userId == null || userId.isEmpty()) {
            Log.e("SplashActivity", "userId is null or empty. Redirecting to MainActivity.");
            navigateToMainActivity();
            return;
        }

        // Fetch user data from Firestore
        Firebaseapp.allUserCollectionReference().document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // User data exists
                        UserModel model = task.getResult().toObject(UserModel.class);

                        // Navigate to ChatActivity with user data
                        Intent chatIntent = new Intent(this, ChatActivity.class);
                        Androidapp.passUserModelAsIntent(chatIntent, model);
                        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(chatIntent);
                    } else {
                        Log.e("SplashActivity", "Failed to fetch user document or document does not exist.");
                        // Fallback to MainActivity
                        navigateToMainActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SplashActivity", "Firestore fetch failed: " + e.getMessage());
                    // Fallback to MainActivity
                    navigateToMainActivity();
                });
    }

    private void navigateAfterSplash() {
        if (Firebaseapp.isLoggedIn()) {
            navigateToMainActivity();
        } else {
            // Navigate to login screen
            startActivity(new Intent(SplashActivity.this, LoginPhone.class));
            finish();
        }
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
