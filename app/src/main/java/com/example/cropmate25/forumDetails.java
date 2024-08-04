package com.example.cropmate25;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class forumDetails extends AppCompatActivity {
    private static final String TAG = "forumDetails";
    TextView question, title;
    RelativeLayout questionLayout;
    ImageView image;
    String id;
    String documentId;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_details);

        title = findViewById(R.id.title);
        image = findViewById(R.id.imageShow);
        question = findViewById(R.id.questionShow);
        questionLayout = findViewById(R.id.imageContainer);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title.setText(bundle.getString("Title"));
            question.setText(bundle.getString("Question"));
            id = bundle.getString("ID");
            imageUrl = bundle.getString("Image");
            documentId =  bundle.getString("key");

            Glide.with(this).load(imageUrl).into(image);
        }

        Log.d(TAG, "Image URL: " + imageUrl);

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
    }
}
