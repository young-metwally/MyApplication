package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mytext = findViewById(R.id.textview);
        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        Toast.makeText(context, text, duration).show();

        Button mybutton = findViewById(R.id.mybutton);

        EditText myEdit = findViewById(R.id.myEdit);
        mybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mytext.setText(myEdit.getText());
                // Code here executes on main thread after user presses button
            }
        });

        mytext.setText("This is new text");


        CheckBox mycb = findViewById(R.id.thecheckbox);
        mycb.setText("IM a checkbox");

        Switch mySwitch = findViewById(R.id.myswitch);



        RadioButton myRB = findViewById(R.id.myRButton);

        ImageView myimg = findViewById(R.id.imageView);

        ImageButton myImgBtn = findViewById(R.id.myImageButton);
        myImgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Code here executes on main thread after user presses button
            }
        });


    }
}