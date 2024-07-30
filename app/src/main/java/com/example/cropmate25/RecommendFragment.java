package com.example.cropmate25;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class RecommendFragment extends AppCompatActivity {
    private static final String TAG = "RecommendFragment";
    private TextView textViewLocation;
    private TextView textViewCrops;
    private TextView textViewSoil;
    private TextView textViewWeather;
    private TextView textViewAsso;

    private String crops;
    private String soil_condition;
    private String weather_condition;
    private String nearestAssociation_name;
    private String nearestAssociation_address;
    private String nearestAssociation_phone;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_fragment);

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewCrops = findViewById(R.id.textViewCrops);
        textViewSoil = findViewById(R.id.textViewsoil);
        textViewWeather = findViewById(R.id.textViewWeather);
        textViewAsso = findViewById(R.id.textViewAssociation);

        String district = UserData.getDistrict();

        FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        Button buttonFetchRecommendations = findViewById(R.id.buttonFetchRecommendations);
        Button buttonViewOtherRecommendations = findViewById(R.id.buttonviewother);

        buttonFetchRecommendations.setOnClickListener(view -> fetchRecommendations(district));
        buttonViewOtherRecommendations.setOnClickListener(view ->{
            Intent intent = new Intent(this, recommend_search.class);
            startActivity(intent);
        });
    }

    private void fetchRecommendations(String district) {
        try {
            String finalDistrict = district.substring(0, 1).toUpperCase() + district.substring(1).toLowerCase() + " District";
            DocumentReference docRef = database.collection("Recommendation").document(finalDistrict);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            crops = document.getString("crops");
                            soil_condition = document.getString("soil_condition");
                            weather_condition = document.getString("weather_condition");

                            Map<String, Object> nearestAssociation = (Map<String, Object>) document.get("nearest_association");

                            if (nearestAssociation != null) {
                                nearestAssociation_name = (String) nearestAssociation.get("name");
                                nearestAssociation_address = (String) nearestAssociation.get("address");
                                nearestAssociation_phone = (String) nearestAssociation.get("phone");
                            }


                            textViewLocation.setText("Location: ");
                            textViewLocation.append("\nCity: " + UserData.getCity());
                            textViewLocation.append("\nDistrict: " + district);
                            textViewCrops.setText(String.format("Recommended Crops: %s", crops));
                            textViewSoil.setText(String.format("Soil Condition: %s", soil_condition));
                            textViewWeather.setText(String.format("Weather Condition: %s", weather_condition));

                            textViewAsso.append(String.format("\nName: %s", nearestAssociation_name));
                            textViewAsso.append(String.format("\nAddress: %s", nearestAssociation_address));
                            textViewAsso.append(String.format("\nPhone: %s", nearestAssociation_phone));
                        }
                        else {
                            Toast.makeText(RecommendFragment.this, "No document found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.e(TAG, "Failed to fetch document: " + task.getException());
                        Toast.makeText(RecommendFragment.this, "Failed to fetch document", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
        catch (Exception e) {
            Log.e(TAG, "Error fetching recommendations: " + e.getMessage());
        }

    }

}
