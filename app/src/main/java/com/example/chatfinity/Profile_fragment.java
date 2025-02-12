package com.example.chatfinity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.chatfinity.Androidapp;
import com.example.chatfinity.Firebaseapp;
import com.example.chatfinity.SplashActivity;
import com.example.chatfinity.UserModel;
import com.example.chatfinity.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Profile_fragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public Profile_fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            saveImageLocally(selectedImageUri);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);
        getUserData();
        updateProfileBtn.setOnClickListener(v -> updateBtnClick());
        logoutBtn.setOnClickListener(v -> logout());

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });
        return view;
    }

    private void saveImageLocally(Uri imageUri) {
        try {
            File profileDir = new File(getContext().getFilesDir(), "profile_images");
            if (!profileDir.exists()) {
                profileDir.mkdir();
            }

            String fileName = "profile_picture.jpg";
            File profileImageFile = new File(profileDir, fileName);
            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(profileImageFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();

            // Set the local profile image to ImageView
            Androidapp.setProfilePic(getContext(), Uri.fromFile(profileImageFile), profilePic);
            Toast.makeText(getContext(), "Profile picture saved locally", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);
        updateToFirestore();
    }

    void updateToFirestore() {
        Firebaseapp.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Androidapp.showToast(getContext(), "Updated successfully");
                    } else {
                        Androidapp.showToast(getContext(), "Update failed");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);
        // Retrieve local profile image from storage and set it
        File profileImageFile = new File(getContext().getFilesDir(), "profile_images/profile_picture.jpg");
        if (profileImageFile.exists()) {
            Androidapp.setProfilePic(getContext(), Uri.fromFile(profileImageFile), profilePic);
        }

        Firebaseapp.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                usernameInput.setText(currentUserModel.getUsername());
                phoneInput.setText(currentUserModel.getPhone());
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    private void logout() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Firebaseapp.logout();
                Intent intent = new Intent(getContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
