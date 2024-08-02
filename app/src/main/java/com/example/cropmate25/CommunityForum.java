package com.example.cropmate25;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityForum extends AppCompatActivity {
    private final String TAG = "CommunityForum";
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<DataClassForum> dataList;
    private MyAdapterForum adapter;
    private SearchView searchView;
    private FirebaseFirestore database;
    private AlertDialog dialog;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community_forum);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CommunityForum.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        firebaseManager = FirebaseManager.getInstance(TAG);
        database = firebaseManager.getDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityForum.this);
        builder.setCancelable(false).setView(R.layout.progress_layout);
        dialog = builder.create();

        dataList = new ArrayList<>();
        adapter = new MyAdapterForum(CommunityForum.this, dataList);
        recyclerView.setAdapter(adapter);

        fetchDataFromFireStore();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityForum.this, ActivityCreateThread.class);
                startActivity(intent);
            }
        });
    }

    private void fetchDataFromFireStore() {
        CollectionReference collectionRef = database.collection("Threads");
        if (!dialog.isShowing()) {
            dialog.show();
        }
        collectionRef.orderBy("postedOn", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "Fetch complete");
                if (task.isSuccessful()) {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String title = document.getString("title");
                        String question = document.getString("question");
                        String image = document.getString("imageUrl");
                        String timeStamp = document.getString("postedOn");
                        String userId = document.getString("uId");
                        String userName = document.getString("name");

                        DataClassForum data = new DataClassForum(title, question, image, timeStamp, userId, userName);
                        data.setKey(document.getId());
                        dataList.add(data);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Data fetched successfully.");
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(CommunityForum.this, "Error getting data", Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }

        });
    }

    public void searchList(String text) {
        ArrayList<DataClassForum> searchList = new ArrayList<>();
        for (DataClassForum dataClass : dataList) {
            if (dataClass.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }
    protected void onResume() {
        super.onResume();
        fetchDataFromFireStore();
    }
}