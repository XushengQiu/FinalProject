package com.example.finalproject.utils;

import com.example.finalproject.models.User;

public class SessionDataManager {
    private static SessionDataManager instance;
    private User currentUser;
    private String firebaseToken;

    private SessionDataManager() {}

    public static SessionDataManager getInstance() {
        if (instance == null) {
            instance = new SessionDataManager();
        }
        return instance;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setFirebaseToken(String token) {
        this.firebaseToken = token;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void clear() {
        currentUser = null;
        firebaseToken = null;
    }
}