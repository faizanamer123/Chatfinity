package com.example.chatfinity;
import com.google.firebase.Timestamp;
public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    // Default constructor
    public UserModel() {
    }

    // Constructor with profilePicPath
    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    // Getter and Setter methods for phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter and Setter methods for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter methods for createdTimestamp
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    // Getter and Setter methods for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter methods for fcmToken
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
