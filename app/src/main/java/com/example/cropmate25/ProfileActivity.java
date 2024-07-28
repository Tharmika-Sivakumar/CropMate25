package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewUserName;
    private TextView textViewLocation;
    private TextView textViewWeather;
    private FirebaseFirestore firestore;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        textViewLocation = findViewById(R.id.text_location);
        textViewWeather = findViewById(R.id.text_weather);
        textViewUserName = findViewById(R.id.text_user_name);

        

        Intent intent = getIntent();
        String weatherInfo = intent.getStringExtra("weatherInfo");

        String location = UserData.getCity() + ", " + UserData.getDistrict() + " district, " + UserData.getProvince() + ", " + UserData.getCounty() + ".";
        textViewLocation.setText(location);
        textViewWeather.setText(weatherInfo);
        textViewUserName.setText(UserData.getName());

        // Set welcome message
        TextView dashboardTextView = findViewById(R.id.textView);
        dashboardTextView.setText("Welcome");
    }

    @Override
    public void onClick(View view) {
        // Handle view clicks if necessary
    }
}