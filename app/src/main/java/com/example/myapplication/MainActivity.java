package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is a pasword checker screen which verifies the password entered by user.
 *
 * @author Islam Metwally
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    /** This holds the text at the center of the screen */
    private TextView textView;
    /** User can enter the password in this field */
    private EditText editTextPassword;
    /** A button to verify the password */
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView2);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString();

            if (checkPasswordComplexity(password)) {
                textView.setText("Your password meets the requirements");
            } else {
                textView.setText("You shall not pass!");
            }
        });

    }

    /**
     * This function checks if the given string has:
     * 1. A Upper Case letter
     * 2. A lower case letter
     * 3. A number, and
     * 4. A special symbol (#$%^&*!@?)
     * And will return true/false accordingly.
     *
     * @param password The String which will be verified
     * @return true if all of the above 4 conditions satisfies, otherwise false
     */
    private boolean checkPasswordComplexity(String password) {
        boolean foundUpperCase = false, foundLowerCase = false, foundNumber = false, foundSpecial = false;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                foundUpperCase = true;
            }
            if (Character.isLowerCase(ch)) {
                foundLowerCase = true;
            }
            if (Character.isDigit(ch)) {
                foundNumber = true;
            }
            if (isSpecialCharacter(ch)) {
                foundSpecial = true;
            }
        }

        if (!foundUpperCase) {
            Toast.makeText(this, "Your password does not have an upper case letter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!foundLowerCase) {
            Toast.makeText(this, "Your password does not have an lower case letter", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!foundNumber) {
            Toast.makeText(this, "Your password does not have a number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!foundSpecial) {
            Toast.makeText(this, "Your password does not have a special symbol", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    /**
     * This function checks if the given character is a symbol or not.
     * @param c Character to check
     * @return True if given character is a symbol, otherwise false
     */
    private boolean isSpecialCharacter(char c) {
        switch (c) {
            case '#':
            case '$':
            case '%':
            case '^':
            case '&':
            case '*':
            case '!':
            case '@':
            case '?':
                return true;
            default:
                return false;
        }
    }
}