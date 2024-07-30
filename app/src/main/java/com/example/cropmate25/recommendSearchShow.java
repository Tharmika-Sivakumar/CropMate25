package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class recommendSearchShow extends AppCompatActivity {
    private TextView textViewLocation;
    private TextView textViewCrops;
    private TextView textViewSoil;
    private TextView textViewWeather;
    private TextView textViewAsso;


    private FirebaseFirestore database;
    private String crops;
    private String soil_condition;
    private String weather_condition;
    private String nearestAssociation_name;
    private String nearestAssociation_address;
    private String nearestAssociation_phone;
    private String selectedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommend_search_show);
        selectedValue = getIntent().getStringExtra("selected_value");
        if (selectedValue == null){
            Log.e(TAG, "The value is not passed");
        }
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewCrops = findViewById(R.id.textViewCrops);
        textViewSoil = findViewById(R.id.textViewsoil);
        textViewWeather = findViewById(R.id.textViewWeather);
        textViewAsso = findViewById(R.id.textViewAssociation);

        FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        fetchRecommendations(selectedValue);

    }

    private void fetchRecommendations(String district) {
        try {
            DocumentReference docRef = database.collection("Recommendation").document(district);
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
                            else{
                                Log.e(TAG, "nearest Association rerurns Null");
                            }

                            // text view for printing data
                            textViewLocation.append("\nDistrict: " + selectedValue);
                            textViewLocation.append("\nCountry: " + "Sri Lanka");
                            textViewCrops.setText(String.format("Recommended Crops: %s", crops));
                            textViewSoil.setText(String.format("Soil Condition: %s", soil_condition));
                            textViewWeather.setText(String.format("Weather Condition: %s", weather_condition));
                            textViewAsso.append(String.format("\nName: %s", nearestAssociation_name));
                            textViewAsso.append(String.format("\nAddress: %s", nearestAssociation_address));
                            textViewAsso.append(String.format("\nPhone: %s", nearestAssociation_phone));
                        }
                        else {
                            Toast.makeText(recommendSearchShow.this, "No document found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.e(TAG, "Failed to fetch document: " + task.getException());
                        Toast.makeText(recommendSearchShow.this, "Failed to fetch document", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
        catch (Exception e) {
            Log.e(TAG, "Error fetching recommendations: " + e.getMessage());
        }
    }

}