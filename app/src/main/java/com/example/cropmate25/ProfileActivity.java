package com.example.cropmate25;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";

    private TextView textViewUserName;
    private TextView textViewLocation;
    private TextView textViewWeather;
    private Button edit;
    private FirebaseFirestore database;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        textViewLocation = findViewById(R.id.text_location);
        textViewWeather = findViewById(R.id.text_weather);
        textViewUserName = findViewById(R.id.text_user_name);
        edit = findViewById(R.id.edit);


        edit.setOnClickListener(this);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        Intent intent = getIntent();
        String weatherInfo = intent.getStringExtra("weatherInfo");

        String location = UserData.getCity() + ", " + UserData.getDistrict() + " district, " + UserData.getProvince() + ", " + UserData.getCounty() + ".";
        textViewLocation.setText(location);
        textViewWeather.setText(weatherInfo);
        textViewUserName.setText(UserData.getName());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit) {
            showEditNameDialog();
        }
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        EditText newName = dialogView.findViewById(R.id.namebox);
        Button updateButton = dialogView.findViewById(R.id.update);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        updateButton.setOnClickListener(v -> {
            String NewName = newName.getText().toString().trim();

            if (TextUtils.isEmpty(NewName)) {
                Toast.makeText(this, "Fill the required Field", Toast.LENGTH_SHORT).show();
                return;
            }

            database.collection("User").document(UserData.getId())
                    .update("Name", NewName)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            textViewUserName.setText(NewName);
                            UserData.setName(NewName);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Failed to update Name", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
    }

}