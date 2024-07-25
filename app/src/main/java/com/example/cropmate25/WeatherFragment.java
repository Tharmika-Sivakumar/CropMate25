package com.example.cropmate25;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends AppCompatActivity {

    TextView cityName;
    Button search;
    TextView show;
    String url;

    /**
     * @noinspection deprecation
     */
    @SuppressLint("StaticFieldLeak")
    class GetWeather extends AsyncTask<String, Void, String> {
        /**
         * @noinspection deprecation
         */
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                return null;
            }
        }

        /**
         * @noinspection deprecation, deprecation
         */
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject main = jsonObject.getJSONObject("main");

                    double temp = main.getDouble("temp");
                    double feelsLike = main.getDouble("feels_like");
                    double tempMax = main.getDouble("temp_max");
                    double tempMin = main.getDouble("temp_min");
                    int pressure = main.getInt("pressure");
                    int humidity = main.getInt("humidity");
                    int seaLevel = main.has("sea_level") ? main.getInt("sea_level") : 0;
                    int grndLevel = main.has("grnd_level") ? main.getInt("grnd_level") : 0;

                    String weatherInfo = "Temperature: " + formatTemperature(temp) + "\n" +
                            "Feels Like: " + formatTemperature(feelsLike) + "\n" +
                            "Temperature Max: " + formatTemperature(tempMax) + "\n" +
                            "Temperature Min: " + formatTemperature(tempMin) + "\n" +
                            "Pressure: " + pressure + " hPa\n" +
                            "Humidity: " + humidity + "%\n" +
                            "Sea Level: " + seaLevel + " hPa\n" +
                            "Ground Level: " + grndLevel + " hPa";

                    show.setText(weatherInfo);
                } else {
                    show.setText("Cannot find the weather");
                }
            } catch (Exception e) {
                e.printStackTrace();
                show.setText("Error parsing weather data");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_fragment);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        search.setOnClickListener(new View.OnClickListener() {
            /** @noinspection deprecation*/
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherFragment.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                if (!city.isEmpty()) {
                    //noinspection SpellCheckingInspection
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=24991040bc1c17696cc24c8b99268672";
                    try {
                        GetWeather task = new GetWeather();
                        task.execute(url);
                    } catch (Exception e) {
                        //noinspection CallToPrintStackTrace
                        e.printStackTrace();
                        show.setText("Error fetching weather data");
                    }
                } else {
                    Toast.makeText(WeatherFragment.this, "Enter City", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @SuppressLint("DefaultLocale")
    private String formatTemperature(double temperature) {
        return String.format("%.1f°C", temperature - 273.15); // Convert Kelvin to Celsius and format
    }
}
