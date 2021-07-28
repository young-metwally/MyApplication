package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

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
    Bitmap image;
    ImageView weatherImage;
    float oldSize = 14;
    private EditText cityTextField;
    private Button forecastButton;
    private TextView tempTextView, tempMaxTextView, tempMinTextView, humidityTextView, descriptionTextView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });

        cityTextField = findViewById(R.id.cityTextField);
        forecastButton = findViewById(R.id.forecastButton);

        tempTextView = findViewById(R.id.tempTextView);
        tempMaxTextView = findViewById(R.id.tempMaxTextView);
        tempMinTextView = findViewById(R.id.tempMinTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        weatherImage = findViewById(R.id.weatherImage);

        forecastButton.setOnClickListener(v -> {
            String city = cityTextField.getText().toString();
            myToolbar.getMenu().add( 0, 5, 0, city).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            runForecast(city);
        });

    }

    private void runForecast(String city) {
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
                JSONArray weatherArray = theDocument.getJSONArray("weather");
                int vis = theDocument.getInt("visibility");
                String name = theDocument.getString("name");

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
                    URL imgUrl = new URL("https://openweathermap.org/img/w/" + iconName + ".png");
                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());

                        // Store image in the memory
                        FileOutputStream fOut = null;
                        try {
                            fOut = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Showing values on UI
                runOnUiThread(() -> {
                    tempTextView.setText("The current temperature is: " + current);
                    tempMaxTextView.setText("The max temperature is: " + max);
                    tempMinTextView.setText("The min temperature is: " + min);
                    humidityTextView.setText("Humidity is: " + humidity);
                    descriptionTextView.setText("Description: " + description);
                    weatherImage.setImageBitmap(image);
                });


            } catch (IOException | JSONException e) {
                Log.e("Connection error:", e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.hide_views:
                cityTextField.setVisibility(View.INVISIBLE);
                forecastButton.setVisibility(View.INVISIBLE);
                tempTextView.setVisibility(View.INVISIBLE);
                tempMaxTextView.setVisibility(View.INVISIBLE);
                tempMinTextView.setVisibility(View.INVISIBLE);
                humidityTextView.setVisibility(View.INVISIBLE);
                descriptionTextView.setVisibility(View.INVISIBLE);
                weatherImage.setVisibility(View.INVISIBLE);
                break;
            case R.id.id_increase:
                oldSize++;
                updateSize();
                break;
            case R.id.id_decrease:
                oldSize = Float.max(oldSize - 1, 5);
                updateSize();
                break;
            case 5:
                String cityName = item.getTitle().toString();
                runForecast(cityName);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateSize() {
        cityTextField.setTextSize(oldSize);
        tempTextView.setTextSize(oldSize);
        tempMaxTextView.setTextSize(oldSize);
        tempMinTextView.setTextSize(oldSize);
        humidityTextView.setTextSize(oldSize);
        descriptionTextView.setTextSize(oldSize);
    }
}