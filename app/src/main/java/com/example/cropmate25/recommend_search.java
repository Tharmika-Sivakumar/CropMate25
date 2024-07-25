package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class recommend_search extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private List<String> dataList;
    private ArrayAdapter<String> adapter;
    private String selectedValue;
    private static final int delay = 1000; // 1s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommend_search);
        initializeFirebase();
        dataList = new ArrayList<>();
        fetchDataFromFirestore();

        SearchView searchView = findViewById(R.id.search);
        ListView suggestionList = findViewById(R.id.suggestionList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        suggestionList.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Optionally handle query submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        suggestionList.setOnItemClickListener((parent, view, position, id) -> {
            selectedValue = adapter.getItem(position);
            Toast.makeText(recommend_search.this, "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Selected value: " + selectedValue);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(recommend_search.this, recommendSearchShow.class);
                intent.putExtra("selected_value", selectedValue);
                startActivity(intent);
            }, delay);
        });
    }

    private void initializeFirebase() {
        try {
            FirebaseApp secondaryApp;
            try {
                secondaryApp = FirebaseApp.getInstance("secondary");
                Log.d(TAG, "FirebaseApp 'secondary' already exists");
            } catch (IllegalStateException e) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setProjectId("shoppingcart-e3525")
                        .setApplicationId("1:669624840785:android:70295f617f188094577e70")
                        .setApiKey("AIzaSyDUSi3BMEa8KlYD1BXTvzBEgqctU8SEH2o")
                        .build();
                secondaryApp = FirebaseApp.initializeApp(this, options, "secondary");
                Log.d(TAG, "FirebaseApp 'secondary' initialized");
            }
            firestore = FirebaseFirestore.getInstance(secondaryApp);
            Toast.makeText(this, "Firebase initialized Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization error: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDataFromFirestore() {
        CollectionReference collectionRef = firestore.collection("Recommendation");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dataList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getId();
                    if (name != null && !name.isEmpty()) {
                        dataList.add(name);
                    } else {
                        Log.e("fetchdataError", "This Document ID is Null or Empty: " + document.getId());
                    }
                }
                Collections.sort(dataList); // Sort the data list alphabetically
                Log.d(TAG, "List is Sorted");
                adapter.notifyDataSetChanged(); // Notify the adapter of the data change
                Log.d(TAG, "Adapter notified");
            } else {
                Log.e("fetchdataError", "Cannot fetch the documents: " + task.getException().getMessage());
            }
        });
    }
}
