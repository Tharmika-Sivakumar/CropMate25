package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecommendFragment extends AppCompatActivity {

    private TextView textViewLocation;
    private TextView textViewCrops;
    private TextView textViewSoil;
    private TextView textViewWeather;
    private TextView textViewAsso;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 10001;

    private String district;
    private String country;
    private String crops;
    private String soil_condition;
    private String weather_condition;
    private String nearestAssociation_name;
    private String nearestAssociation_address;
    private String nearestAssociation_phone;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_fragment);

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewCrops = findViewById(R.id.textViewCrops);
        textViewSoil = findViewById(R.id.textViewsoil);
        textViewWeather = findViewById(R.id.textViewWeather);
        textViewAsso = findViewById(R.id.textViewAssociation);

        Button buttonFetchRecommendations = findViewById(R.id.buttonFetchRecommendations);
        Button buttonViewOtherRecommendations = findViewById(R.id.buttonviewother);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        buttonFetchRecommendations.setOnClickListener(view -> getLocation());
        buttonViewOtherRecommendations.setOnClickListener(view ->{
            Intent intent = new Intent(this, recommend_search.class);
            startActivity(intent);
        });
        initializeFirebase();
    }

    private void initializeFirebase() {
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

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    getAddressFromLocation(latitude, longitude);
                } else {
                    Toast.makeText(RecommendFragment.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                district = address.getSubAdminArea();
                country = address.getCountryName();
                String addressText = address.getAddressLine(0);
                textViewLocation.setText("Location: " + addressText);
                if (district != null) {
                    textViewLocation.append("\nDistrict: " + district);
                    fetchRecommendations(district);
                }
                if (country != null) {
                    textViewLocation.append("\nCountry: " + country);
                }
            } else {
                textViewLocation.setText("Location: Unable to get address");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textViewLocation.setText("Location: Unable to get address");
        }
    }

    private void fetchRecommendations(String district) {
        try {
            String finalDistrict = district.substring(0, 1).toUpperCase() + district.substring(1).toLowerCase() + " District";
            DocumentReference docRef = firestore.collection("Recommendation").document(finalDistrict);
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

                            // text view for printing data
                            textViewCrops.setText(String.format("Recommended Crops: %s", crops));
                            textViewSoil.setText(String.format("Soil Condition: %s", soil_condition));
                            textViewWeather.setText(String.format("Weather Condition: %s", weather_condition));

                            textViewAsso.append(String.format("\nName: %s", nearestAssociation_name));
                            textViewAsso.append(String.format("\nAddress: %s", nearestAssociation_address));
                            textViewAsso.append(String.format("\nPhone: %s", nearestAssociation_phone));
                            Toast.makeText(RecommendFragment.this, "Data Received", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Log.e(TAG, "Error fetching recommendations: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
 }
}
}