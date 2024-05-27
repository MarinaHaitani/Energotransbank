package com.example.energotransbank;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class Firebase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
