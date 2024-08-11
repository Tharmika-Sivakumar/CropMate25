package com.example.cropmate25;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class forumDetails extends AppCompatActivity {
    private static final String TAG = "forumDetails";
    private TextView question, title, whoPost, whenPost, messageText;
    private RelativeLayout questionLayout;
    private ImageView image;
    private String id, name, timeStamp;
    private String documentId;
    private String imageUrl;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseManager firebaseManager;
    private RecyclerView recyclerViewMessages;
    private forumDetailsAdapter adapter;
    private List<ForumChat> chatList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_details);

        title = findViewById(R.id.title);
        image = findViewById(R.id.imageShow);
        question = findViewById(R.id.questionShow);
        questionLayout = findViewById(R.id.imageContainer);
        recyclerViewMessages = findViewById(R.id.RecyclerView_chat);
        whenPost = findViewById(R.id.who);
        whoPost = findViewById(R.id.when);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerViewMessages.setLayoutManager(gridLayoutManager);

        FloatingActionButton sendMessage = findViewById(R.id.sendMessage);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        chatList = new ArrayList<>();
        adapter = new forumDetailsAdapter(forumDetails.this, chatList);
        recyclerViewMessages.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title.setText(bundle.getString("Title"));
            question.setText(bundle.getString("Question"));
            id = bundle.getString("ID");
            imageUrl = bundle.getString("Image");
            documentId = bundle.getString("key");
            name = bundle.getString("Name");
            timeStamp = bundle.getString("whenPost");



            if(imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(image);
            }
            else {
                Glide.with(this).load(R.drawable.no_image).into(image);
            }
            whoPost.setText("By: " + name);
            whenPost.setText(timeStamp);

        }
        fetchDataFromFireStore();

        questionLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (id.equals(UserData.getId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(forumDetails.this);
                    View dialogView = LayoutInflater.from(forumDetails.this).inflate(R.layout.edit_thread_confirm, null);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();

                    Button editButton = dialogView.findViewById(R.id.edit);
                    Button cancelButton = dialogView.findViewById(R.id.btnCancel);

                    editButton.setOnClickListener(view -> {
                        Intent intent = new Intent(forumDetails.this, ActivityUpdateThread.class);
                        intent.putExtra("Title", title.getText().toString());
                        intent.putExtra("Question", question.getText().toString());
                        intent.putExtra("Image", imageUrl);
                        intent.putExtra("key", documentId);
                        startActivity(intent);
                        dialog.dismiss();
                    });

                    cancelButton.setOnClickListener(view -> dialog.dismiss());
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    dialog.show();
                } else {
                    Toast.makeText(forumDetails.this, "You can Edit your own threads", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        sendMessage.setOnClickListener(v -> typeMessage());
    }

    private void typeMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_send_message, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button postButton = dialogView.findViewById(R.id.btnPost);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);
        final TextView messageText = dialogView.findViewById(R.id.message);

        postButton.setOnClickListener(view -> {
            String message = messageText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(forumDetails.this, "Please fill out the text field", Toast.LENGTH_SHORT).show();
            } else {
                sendData(message);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        // Customize dialog appearance
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();

    }


    private void sendData(String message){
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("name", UserData.getName());
        messageData.put("postedOn", DateTime.getTimeStamp());
        messageData.put("question", message);
        messageData.put("uId", UserData.getId());
        Log.d(TAG, "DocumentID 1 " + documentId);
        database.collection("Threads")
                .document(documentId)
                .collection("Message")
                .add(messageData)
                .addOnCompleteListener(feedbackTask -> {
                    if (feedbackTask.isSuccessful()) {
                        fetchDataFromFireStore();
                        Toast.makeText(forumDetails.this, "Thank You For Giving your Thoughts", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Data stored successfully");
                    }
                    else {
                        Toast.makeText(forumDetails.this, "Failed to store Message", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to store Message: " + feedbackTask.getException().getMessage());
                    }
                });
    }

        // this below for adaptor
    private void fetchDataFromFireStore() {
        if (documentId == null || documentId.isEmpty()) {
            Log.e(TAG, "Document ID is null or empty.");
            return;
        }

        database.collection("Threads")
                .document(documentId)
                .collection("Message")
                .orderBy("postedOn", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "Fetch complete");

                        if (task.isSuccessful()) {
                            chatList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String message = document.getString("question");
                                String timeStamp = document.getString("postedOn");
                                String userId = document.getString("uId");
                                String userName = document.getString("name");

                                ForumChat data = new ForumChat(message, timeStamp, userId, userName);
                                data.setKey(document.getId());
                                data.setThreadID(documentId);
                                chatList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Data fetched successfully.");
                        }
                        else {
                            Log.w(TAG, "Error getting messages.", task.getException());
                            Toast.makeText(forumDetails.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
