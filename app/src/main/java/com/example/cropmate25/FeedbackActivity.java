package com.example.cropmate25;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    private static final String TAG = "FeedbackActivity";
    private EditText messageData;
    private Button btnSend;
    private FirebaseFirestore database;
    private FirebaseManager firebaseManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        messageData = findViewById(R.id.messagedata);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.RecyclerView);

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

        Map<String, Object> data = new HashMap<>();
        data.put("Name", UserData.getName());
        data.put("Living City", UserData.getCity());
        data.put("Living District", UserData.getDistrict());
        data.put("Living Province", UserData.getProvince());
        data.put("When Submitted", DateTime.getTimeStamp());
        data.put("Feedback", message);

        database.collection("Feedback").document(UserData.getId())
                .set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FeedbackActivity.this, "Thank You For your Feedback", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Data stored successfully");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(FeedbackActivity.this, HomeFragment.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000);
                        }
                        else {
                            Toast.makeText(FeedbackActivity.this, "Failed to store Data", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to store Data: " + task.getException().getMessage());
                        }
                    }
                });

    }

}