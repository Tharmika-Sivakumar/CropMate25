package com.example.cropmate25;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityUpdateThread extends AppCompatActivity {
    private final String TAG = "ActivityUpdateThread";
    private ImageView updateImage;
    private Button updateButton;
    private EditText updateQuestion, updateTitle;
    private String imageUrl;
    private Uri uri;
    private FirebaseManager firebaseManager;
    private FirebaseFirestore database;
    private FirebaseStorage storage;

    String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_update_forum);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();
        storage = firebaseManager.getStorage();

        updateButton = findViewById(R.id.update);
        updateQuestion = findViewById(R.id.messagedata);
        updateImage = findViewById(R.id.uploadImage);
        updateTitle = findViewById(R.id.uploadTitle);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            documentId = bundle.getString("key");

            if(bundle.getString("Image") != null) {
                Glide.with(ActivityUpdateThread.this).load(bundle.getString("Image")).into(updateImage);
            }
            updateTitle.setText(bundle.getString("Title"));
            updateQuestion.setText(bundle.getString("Question"));
        }
        else {
            Log.e(TAG, "No data passed in the intent");
        }

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(ActivityUpdateThread.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        updateImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        updateButton.setOnClickListener(view -> {
            if (uri != null) {
                updateImageAndUpdateData();
            }
            else {
                updateData();
            }

        });
    }

    private void updateImageAndUpdateData() {
        StorageReference storageReference = this.storage.getReference().child("Thread Images").child(Objects.requireNonNull(uri.getLastPathSegment()));

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUpdateThread.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    imageUrl = downloadUri.toString();
                    dialog.dismiss();
                    updateData();
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                    Toast.makeText(ActivityUpdateThread.this, "Failed to get URL", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.e(TAG, "Failed to upload image: " + e.getMessage());
            Toast.makeText(ActivityUpdateThread.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateData() {
        String title = updateTitle.getText().toString().trim();
        String question = updateQuestion.getText().toString().trim();

        if (title.isEmpty() || question.isEmpty() || (imageUrl == null && uri != null)) {
            Toast.makeText(this, "Please fill all fields and upload an image if needed", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("question", question);
        data.put("imageUrl", imageUrl != null ? imageUrl : null);
        data.put("postedOn", DateTime.getTimeStamp());

        database.collection("Threads").document(documentId).update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Thread data updated successfully.");
                    Intent intent = new Intent(ActivityUpdateThread.this, CommunityForum.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update thread data: " + e.getMessage());
                    Toast.makeText(ActivityUpdateThread.this, "Failed to update thread data", Toast.LENGTH_SHORT).show();
                });
    }
}