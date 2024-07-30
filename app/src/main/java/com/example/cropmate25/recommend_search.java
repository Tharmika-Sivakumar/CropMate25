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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class recommend_search extends AppCompatActivity {

    private List<String> dataList;
    private ArrayAdapter<String> adapter;
    private String selectedValue;
    private FirebaseFirestore database;
    private static final int delay = 1000; // 1s


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommend_search);

        FirebaseManager firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        dataList = new ArrayList<>();
        fetchDataFromFirestore();

        SearchView searchView = findViewById(R.id.search);
        ListView suggestionList = findViewById(R.id.suggestionList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        suggestionList.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
            Log.d(TAG, "Selected value: " + selectedValue);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(recommend_search.this, recommendSearchShow.class);
                intent.putExtra("selected_value", selectedValue);
                startActivity(intent);
            }, delay);
        });
    }

    private void fetchDataFromFirestore() {
        CollectionReference collectionRef = database.collection("Recommendation");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dataList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getId();
                    if (name != null && !name.isEmpty()) {
                        dataList.add(name);
                    }
                    else {
                        Log.e("fetchdataError", "This Document ID is Null or Empty: " + document.getId());
                    }
                }
                Collections.sort(dataList);
                Log.d(TAG, "List is Sorted");
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter notified");
            }
            else {
                Toast.makeText(recommend_search.this, "Can Not Fetch Data", Toast.LENGTH_SHORT).show();
                Log.e("fetch data Error", "Cannot fetch the documents: " + task.getException().getMessage());
            }
        });

    }

}