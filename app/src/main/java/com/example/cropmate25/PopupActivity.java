package com.example.cropmate25;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class PopupActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private ImageView imageViewAddBtn;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        editTextTitle = findViewById(R.id.popup_title);
        editTextDescription = findViewById(R.id.popup_description);
        imageViewAddBtn = findViewById(R.id.popup_addbtn);
        progressBar = findViewById(R.id.popup_progressBar);

        // Initialize Firebase Auth and Database
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("data");

        imageViewAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToFirebase();
            }
        });
    }

    private void addDataToFirebase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userName = user.getDisplayName();
            String id = databaseReference.push().getKey();
            Data data = new Data(id, title, description, userName);

            assert id != null;
            databaseReference.child(id).setValue(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(PopupActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                    sendNotificationToAllUsers(title, description, userName);
                } else {
                    Toast.makeText(PopupActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            });
        }
    }

    private void sendNotificationToAllUsers(String title, String description, String userName) {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String credentialsPath = "D:\\Application\\Application\\CropMate25\\CropMate25\\app\\serviceAccountKey.json"; // Update this to the correct path
                        NotificationSender.sendNotification(
                                credentialsPath,
                                "New Post from " + userName,
                                title + ": " + description
                        );
                    }
                });
    }

    public static class Data {
        public String id;
        public String title;
        public String description;
        public String userName;

        public Data() {
            // Default constructor required for calls to DataSnapshot.getValue(Data.class)
        }

        public Data(String id, String title, String description, String userName) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.userName = userName;
        }
    }
}
