package com.example.cropmate25;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {

    private EditText nameData, emailData, messageData;
    private Button btnSend, btnDetails;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        // Initialize views
        nameData = findViewById(R.id.namedata);
        emailData = findViewById(R.id.emaildata);
        messageData = findViewById(R.id.messagedata);
        btnSend = findViewById(R.id.btn_send);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("feedback");

        // Set onClickListener for the send button
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
        String name = nameData.getText().toString().trim();
        String email = emailData.getText().toString().trim();
        String message = messageData.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            Toast.makeText(FeedbackActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Feedback feedback = new Feedback(id, name, email, message);

        databaseReference.child(id).setValue(feedback).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FeedbackActivity.this, "Feedback sent successfully", Toast.LENGTH_SHORT).show();
                nameData.setText("");
                emailData.setText("");
                messageData.setText("");
            } else {
                Toast.makeText(FeedbackActivity.this, "Failed to send feedback: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Define a Feedback class to store feedback data
    public static class Feedback {
        public String id;
        public String name;
        public String email;
        public String message;

        public Feedback() {
            // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
        }

        public Feedback(String id, String name, String email, String message) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.message = message;
        }
    }
}
