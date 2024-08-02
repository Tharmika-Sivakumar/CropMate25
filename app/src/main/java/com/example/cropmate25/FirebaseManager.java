package com.example.cropmate25;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private FirebaseApp secondaryApp;
    private String activityName;

    private FirebaseManager(String activityName) {
        this.activityName = activityName;
        initializeFirebase();
    }

    public static synchronized FirebaseManager getInstance(String activityName) {
        if (instance == null) {
            instance = new FirebaseManager(activityName);
        }
        return instance;
    }

    private void initializeFirebase() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("shoppingcart-e3525")
                    .setApplicationId("1:669624840785:android:70295f617f188094577e70")
                    .setApiKey("AIzaSyDUSi3BMEa8KlYD1BXTvzBEgqctU8SEH2o")
                    .setStorageBucket("shoppingcart-e3525.appspot.com")
                    .build();
            Context context = FirebaseApp.getInstance().getApplicationContext();
            secondaryApp = FirebaseApp.initializeApp(context, options, "secondary");
            if (secondaryApp != null) {
                database = FirebaseFirestore.getInstance(secondaryApp);
                storage = FirebaseStorage.getInstance(secondaryApp);
                Log.e(TAG, activityName + ": Firebase initialized Successfully");
            } else {
                Log.e(TAG, activityName + ": FirebaseApp initialization failed");
            }
        } catch (Exception e) {
            Log.e(TAG, activityName + ": Firebase initialization error: " + e.getMessage());
        }
    }

    public FirebaseFirestore getDatabase() {
        return database;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }
}