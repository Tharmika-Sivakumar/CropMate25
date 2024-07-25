package com.example.cropmate25;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    TextView detailDesc, detailTitle;
    TextView detailLoc, detailPrice;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);
        detailPrice = findViewById(R.id.detailPrice);
        detailLoc = findViewById(R.id.detailLoc);
        detailDesc = findViewById(R.id.detailDesc);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailTitle.setText(bundle.getString("Title"));
            detailDesc.setText(bundle.getString("Description"));
            detailPrice.setText(bundle.getString("Price"));
            detailLoc.setText(bundle.getString("Location"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");

            Glide.with(this).load(imageUrl).into(detailImage);
        }

        Log.d(TAG, "Image URL: " + imageUrl);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Price", detailPrice.getText().toString())
                        .putExtra("Location", detailLoc.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }

    private void deleteRecord() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Images");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Log.d(TAG, "Attempting to delete image URL: " + imageUrl);

        // Extract the file path from the imageUrl
        Uri uri = Uri.parse(imageUrl);
        String path = uri.getPath();
        if (path != null && path.contains("o/")) {
            path = path.substring(path.indexOf("o/") + 2, path.indexOf("?alt="));
        } else {
            Log.e(TAG, "Image URL format is incorrect: " + imageUrl);
            Toast.makeText(DetailActivity.this, "Failed to delete image: incorrect URL format", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Extracted path: " + path);

        StorageReference storageReference = storage.getReference().child(path);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Image deleted successfully.");
                reference.child(key).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Record deleted successfully.");
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MarketplaceActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "Failed to delete record: " + task.getException());
                        Toast.makeText(DetailActivity.this, "Failed to delete record", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to delete image: " + e.getMessage());
                Toast.makeText(DetailActivity.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
