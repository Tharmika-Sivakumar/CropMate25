package com.example.cropmate25;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class viewReply extends AppCompatActivity {
    private static final String TAG = "viewReply";
    private TextView chat, whoPost, whenPost;
    private String threadId;
    private String messageId;
    private RecyclerView recyclerViewReply;
    private Context context;
    private FirebaseFirestore database;
    private FirebaseManager firebaseManager;
    private ReplyChatAdapter adapter;
    private List<ReplyChat> replyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_reply);

        FloatingActionButton sendMessage = findViewById(R.id.sendMessage);

        chat = findViewById(R.id.questionShow);
        recyclerViewReply = findViewById(R.id.RecyclerView_reply);
        whenPost = findViewById(R.id.when);
        whoPost = findViewById(R.id.who);

        context = this;
        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();
        replyList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chat.setText(bundle.getString("MESSAGE"));
            threadId = bundle.getString("THREAD_ID");
            messageId = bundle.getString("MESSAGE_ID");
            whoPost.setText(bundle.getString("WHO_POSTED"));
            whenPost.setText(bundle.getString("WHEN_POSTED"));
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerViewReply.setLayoutManager(gridLayoutManager);

        adapter = new ReplyChatAdapter(this, replyList);
        recyclerViewReply.setAdapter(adapter);

        fetchDataFromFireStore();

        sendMessage.setOnClickListener(v -> typeMessage());
    }

    private void typeMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_send_reply, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button postButton = dialogView.findViewById(R.id.btnPost);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);
        final TextView messageText = dialogView.findViewById(R.id.message);

        postButton.setOnClickListener(view -> {
            String message = messageText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(context, "Please fill out the text field", Toast.LENGTH_SHORT).show();
            } else {
                sendData(threadId, messageId, message);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }

    private void sendData(String threadID, String messageID, String replyText) {
        Map<String, Object> replyData = new HashMap<>();
        replyData.put("reply", replyText);
        replyData.put("name", UserData.getName());
        replyData.put("uId", UserData.getId());
        replyData.put("postedOn", DateTime.getTimeStamp());

        database.collection("Threads")
                .document(threadID)
                .collection("Message")
                .document(messageID)
                .collection("Replies")
                .add(replyData)
                .addOnSuccessListener(documentReference -> {
                    fetchDataFromFireStore();
                    Log.d(TAG, "Reply successfully added!");
                    Toast.makeText(context, "Reply added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding reply: ", e);
                    Toast.makeText(context, "Error adding reply", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchDataFromFireStore() {
        database.collection("Threads")
                .document(threadId)
                .collection("Message")
                .document(messageId)
                .collection("Replies")
                .orderBy("postedOn", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "Fetch complete");

                        if (task.isSuccessful() && task.getResult() != null) {
                            replyList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String message = document.getString("reply");
                                String timeStamp = document.getString("postedOn");
                                String userId = document.getString("uId");
                                String userName = document.getString("name");

                                ReplyChat data = new ReplyChat(message, timeStamp, userId, userName);
                                data.setKey(document.getId());
                                data.setMessageID(messageId);
                                data.setThreadID(threadId);
                                replyList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Data fetched successfully.");
                        } else {
                            Log.w(TAG, "Error getting messages.", task.getException());
                            Toast.makeText(viewReply.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
