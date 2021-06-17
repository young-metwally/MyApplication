package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        Button loginButton = findViewById(R.id.nextPageButton);
        EditText emailEditText = findViewById(R.id.inputEditText);

        String email = prefs.getString("EMAIL", "");
        emailEditText.setText(email);

        loginButton.setOnClickListener(v -> {
            String emailStr = emailEditText.getText().toString();

            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("EMAIL", emailStr);
            edit.apply();

            Intent nextPage = new Intent( MainActivity.this, SecondActivity.class);
            nextPage.putExtra( "EmailAddress", emailStr );
            startActivity(nextPage);
        });

        Log.w( TAG, "In onCreate() - Loading Widgets" );



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "In onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "In onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "In onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume()");
    }
}
