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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    String stringURL;
    Bitmap image;
    String current;
    String min;
    String max;
    String description;
    String iconName;
    String humidity;
    private EditText cityTextField;
    private Button forecastButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextField = findViewById(R.id.cityTextField);
        forecastButton = findViewById(R.id.forecastButton);

        forecastButton.setOnClickListener(v -> {
            String city = cityTextField.getText().toString();

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Getting Forecast")
                    .setMessage("We're calling people in " + city + " to look outside their window and tell us what's the weather like over there.")
                    .setView(new ProgressBar(MainActivity.this))
                    .show();

            try {
                stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(city, "UTF-8")
                        + "&appid=7e943c97096a9784391a981c4d878b22&Units=Metric&mode=xml";
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

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(in, "UTF-8");


                    while (xpp.next() != XmlPullParser.END_DOCUMENT) {
                        switch (xpp.getEventType()) {
                            case XmlPullParser.START_TAG:
                                if (xpp.getName().equals("temperature")) {
                                    current = xpp.getAttributeValue(null, "value");  //this gets the current temperature
                                    min = xpp.getAttributeValue(null, "min"); //this gets the min temperature
                                    max = xpp.getAttributeValue(null, "max"); //this gets the max temperature
                                } else if (xpp.getName().equals("weather")) {
                                    description = xpp.getAttributeValue(null, "value");  //this gets the weather description
                                    iconName = xpp.getAttributeValue(null, "icon"); //this gets the icon name
                                } else if (xpp.getName().equals("humidity")) {
                                    humidity = xpp.getAttributeValue(null, "value"); //this gets the icon name
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                break;
                            case XmlPullParser.TEXT:
                                break;
                        }
                    }


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

                    runOnUiThread(() -> {
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

                        dialog.hide();
                    });


                } catch (IOException | XmlPullParserException e) {
                    Log.e("Connection error:", e.getMessage());
                }
            });

        });

    }

}