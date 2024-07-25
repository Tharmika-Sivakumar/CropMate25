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
    private String userName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize views
        textViewLocation = findViewById(R.id.text_location);
        textViewWeather = findViewById(R.id.text_weather);
        textViewUserName = findViewById(R.id.text_user_name);

        // Get intent extras
        Intent intent = getIntent();
        String county = intent.getStringExtra("country");
        String city = intent.getStringExtra("city");
        String weatherInfo = intent.getStringExtra("weatherInfo");
        String UId = intent.getStringExtra("userID");

        if (county == null) {
            Log.e(TAG, "Missing intent extra: county");
            Toast.makeText(this, "Missing county data", Toast.LENGTH_SHORT).show();
        }

        if (city == null) {
            Log.e(TAG, "Missing intent extra: city");
            Toast.makeText(this, "Missing city data", Toast.LENGTH_SHORT).show();
        }

        if (weatherInfo == null) {
            Log.e(TAG, "Missing intent extra: weatherInfo");
            Toast.makeText(this, "Missing weather info data", Toast.LENGTH_SHORT).show();
        }

        if (UId == null) {
            Log.e(TAG, "Missing intent extra: userID");
            Toast.makeText(this, "Missing user ID data", Toast.LENGTH_SHORT).show();
        }

        // End the activity if any required data is missing
        if (county == null || city == null || weatherInfo == null || UId == null) {
            finish();
            return;
        }


        // Initialize Firebase
        try { // Check the FirebaseApp with the name "secondary" already there or not, because of the error we faced
            FirebaseApp secondaryApp;
            try { // check it is there ot not
                secondaryApp = FirebaseApp.getInstance("secondary");
                Log.e(TAG, "FirebaseApp 'secondary' already exists");
            }
            catch (IllegalStateException e) { // if not initialized
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setProjectId("shoppingcart-e3525")
                        .setApplicationId("1:669624840785:android:70295f617f188094577e70")
                        .setApiKey("AIzaSyDUSi3BMEa8KlYD1BXTvzBEgqctU8SEH2o")
                        .build();
                secondaryApp = FirebaseApp.initializeApp(this, options, "secondary");
                Log.e(TAG, "FirebaseApp 'secondary' initialized" + e.getMessage());
            }
            firestore = FirebaseFirestore.getInstance(secondaryApp);
            Toast.makeText(this, "Firebase initialized Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e(TAG, "Firebase initialization error: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed", Toast.LENGTH_SHORT).show();
        }

        // Fetch user data from Firestore
        DocumentReference docRef = firestore.collection("User").document(UId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName = document.getString("Name");
                        if (userName != null) {
                            textViewUserName.setText(userName);
                        } else {
                            textViewUserName.setText("Unknown User");
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "No document found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Error getting document: ", task.getException());
                    Toast.makeText(ProfileActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set location and weather info
        String location = county + ", " + city;
        textViewLocation.setText(location);
        textViewWeather.setText(weatherInfo);

        // Set welcome message
        TextView dashboardTextView = findViewById(R.id.textView);
        dashboardTextView.setText("Welcome");
    }

    @Override
    public void onClick(View view) {
        // Handle view clicks if necessary
    }
}
