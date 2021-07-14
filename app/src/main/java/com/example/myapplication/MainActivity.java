package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    String stringURL;
    private EditText cityTextField;
    private Button forecastButton;
    Bitmap image;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextField = findViewById(R.id.cityTextField);
        forecastButton = findViewById(R.id.forecastButton);

        forecastButton.setOnClickListener(v -> {
            String city = cityTextField.getText().toString();

            try {
                stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(city, "UTF-8")
                        + "&appid=7e943c97096a9784391a981c4d878b22&Units=Metric";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Executor newThread = Executors.newSingleThreadExecutor();

            newThread.execute(() -> {
                /* This runs in a separate thread */
                try {
                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String text = (new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    JSONObject theDocument = new JSONObject(text);
                    JSONObject coord = theDocument.getJSONObject("coord");
                    JSONArray weatherArray = theDocument.getJSONArray ( "weather" );
                    int vis = theDocument.getInt("visibility");
                    String name = theDocument.getString( "name" );

                    JSONObject position0 = weatherArray.getJSONObject(0);
                    String description = position0.getString("description");
                    String iconName = position0.getString("icon");

                    JSONObject mainObject = theDocument.getJSONObject("main");
                    double current = mainObject.getDouble("temp");
                    double min = mainObject.getDouble("temp_min");
                    double max = mainObject.getDouble("temp_max");
                    double humidity = mainObject.getDouble("humidity");

                    File file = new File(getFilesDir(), iconName + ".png");
                    if (file.exists()) {
                        image = BitmapFactory.decodeFile(getFilesDir() + "/" + iconName + ".png");
                    } else {
                        // Download image
                        URL imgUrl = new URL( "https://openweathermap.org/img/w/" + iconName + ".png" );
                        HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            image = BitmapFactory.decodeStream(connection.getInputStream());

                            // Store image in the memory
                            FileOutputStream fOut = null;
                            try {
                                fOut = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    runOnUiThread( (  )  -> {
                        // Showing values on UI
                        TextView tempTextView = findViewById(R.id.tempTextView);
                        tempTextView.setText("The current temperature is: " + current);

                        TextView tempMaxTextView = findViewById(R.id.tempMaxTextView);
                        tempMaxTextView.setText("The max temperature is: " + max);

                        TextView tempMinTextView = findViewById(R.id.tempMinTextView);
                        tempMinTextView.setText("The min temperature is: " + min);

                        TextView humidityTextView = findViewById(R.id.humidityTextView);
                        humidityTextView.setText("Humidity is: " + humidity);

                        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
                        descriptionTextView.setText("Description: " + description);

                        ImageView weatherImage = findViewById(R.id.weatherImage);
                        weatherImage.setImageBitmap(image);
                    });








                } catch (IOException | JSONException e) {
                    Log.e("Connection error:", e.getMessage());
                }
            });

        });

    }

}