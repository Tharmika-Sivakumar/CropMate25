package com.example.cropmate25;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityCreateThread extends AppCompatActivity {
    private final String TAG = "ActivityCreateThread";
    ImageView image;
    Button saveButton;
    EditText threadTitle, questionThread;
    String imageURL;
    Uri uri;
    FirebaseManager firebaseManager;
    FirebaseFirestore database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_create_forum);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();
        storage = firebaseManager.getStorage();
        image = findViewById(R.id.uploadImage);
        questionThread = findViewById(R.id.messagedata);
        threadTitle = findViewById(R.id.uploadTitle);
        saveButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = Objects.requireNonNull(data).getData();
                            image.setImageURI(uri);
                        }
                        else {
                            Toast.makeText(ActivityCreateThread.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    saveData();
                }
                else {
                    uploadData();
                }
            }
        });
    }

    public void saveData() {
        StorageReference storageReference = storage.getReference().child("Thread Image").child(Objects.requireNonNull(uri.getLastPathSegment()));


        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCreateThread.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();


        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        dialog.dismiss();
                        uploadData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Upload failed: " + e.getMessage());
                        dialog.dismiss();
                        Toast.makeText(ActivityCreateThread.this, "Failed to get URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.e(TAG, "store Thread Data: Successful" + e.getMessage());
                Toast.makeText(ActivityCreateThread.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void uploadData() {
        String title = threadTitle.getText().toString();
        String question = questionThread.getText().toString();

        if (title.isEmpty() || question.isEmpty() || imageURL == null && uri != null) {
            Toast.makeText(this, "Please fill all fields and upload an image if you need", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("question", question);
        data.put("uId", UserData.getId());
        data.put("name", UserData.getName());
        data.put("postedOn", DateTime.getTimeStamp());
        data.put("imageUrl", imageURL != null ? imageURL : null);

        database.collection("Threads").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "store Thread Data: Successful");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to store Thread Data: " + e.getMessage());
                        Toast.makeText(ActivityCreateThread.this,"Failed to store Thread Data", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}