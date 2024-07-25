package com.example.cropmate25;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class HomeFragment extends AppCompatActivity implements View.OnClickListener {
    private String userId;
    private String weatherInfo;
    private String city;
    private String county;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_fragment);

        TextView dashboardTextView = findViewById(R.id.textView);
        dashboardTextView.setText("Dashboard");
        findViewById(R.id.imageView3).setOnClickListener(this);
        findViewById(R.id.marketplace_card).setOnClickListener(this);
        findViewById(R.id.recommendations_card).setOnClickListener(this);
        findViewById(R.id.community_card).setOnClickListener(this);
        findViewById(R.id.weather_card).setOnClickListener(this);
        findViewById(R.id.feedback_card).setOnClickListener(this);
        findViewById(R.id.news_card).setOnClickListener(this);

        Intent intent = getIntent();
        county = intent.getStringExtra("county");
        city = intent.getStringExtra("city");
        weatherInfo = intent.getStringExtra("weatherInfo");
        userId = intent.getStringExtra("userID");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        if (view.getId() == R.id.imageView3) {
            intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userID", userId);
            intent.putExtra("weatherInfo", weatherInfo);
            intent.putExtra("city", city);
            intent.putExtra("country", county);  // need to check

            Log.d("TAG", "Country: " + county);

            startActivity(intent);
        }else if (view.getId() == R.id.marketplace_card) {
            intent = new Intent(this, MarketPlaceFragment.class);
            startActivity(intent);
        } else if (view.getId() == R.id.recommendations_card) {
            intent = new Intent(this, RecommendFragment.class);
            startActivity(intent);
        } else if (view.getId() == R.id.community_card) {
            intent = new Intent(this, CommunityForum.class);
            startActivity(intent);
        } else if (view.getId() == R.id.weather_card) {
            intent = new Intent(this, WeatherFragment.class);
            startActivity(intent);
        } else if (view.getId() == R.id.feedback_card) {
            intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.news_card) {
            intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        }
    }
}
