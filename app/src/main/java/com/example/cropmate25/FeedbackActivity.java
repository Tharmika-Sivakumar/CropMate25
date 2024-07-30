package com.example.cropmate25;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    private static final String TAG = "FeedbackActivity";
    private EditText messageData;
    private Button btnSend;
    private FirebaseFirestore database;
    private FirebaseManager firebaseManager;
    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        messageData = findViewById(R.id.messagedata);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.RecyclerView);

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(feedbackAdapter);
        fetchFeedbackData();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendData() {
        String message = messageData.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(FeedbackActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", UserData.getName());
        userData.put("Living City", UserData.getCity());
        userData.put("Living District", UserData.getDistrict());
        userData.put("Living Province", UserData.getProvince());


        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("When Submitted", DateTime.getTimeStamp());
        feedbackData.put("Feedback", message);


        database.collection("Feedback")
                .document(UserData.getId())
                .set(userData)
                .addOnCompleteListener(userDataTask -> {
                    if (userDataTask.isSuccessful()) {
                        database.collection("Feedback")
                                .document(UserData.getId())
                                .collection("UserFeedback")
                                .add(feedbackData)
                                .addOnCompleteListener(feedbackTask -> {
                                    if (feedbackTask.isSuccessful()) {
                                        Toast.makeText(FeedbackActivity.this, "Thank You For your Feedback", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Data stored successfully");
                                        new Handler().postDelayed(() -> {
                                            Intent intent = new Intent(FeedbackActivity.this, HomeFragment.class);
                                            startActivity(intent);
                                            finish();
                                        }, 1000);
                                    } else {
                                        Toast.makeText(FeedbackActivity.this, "Failed to store Feedback", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Failed to store Feedback: " + feedbackTask.getException().getMessage());
                                    }
                                });
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Failed to store User Data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to store User Data: " + userDataTask.getException().getMessage());
                    }
                });
    }



    private void fetchFeedbackData() {
        database.collection("Feedback")
                .document(UserData.getId())
                .collection("UserFeedback")
                .orderBy("When Submitted", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        feedbackList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String feedback = document.getString("Feedback");
                            String timestamp = document.getString("When Submitted");
                            Feedback feedbackItem = new Feedback(timestamp, feedback);
                            feedbackList.add(feedbackItem);
                        }
                        feedbackAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }




}