package com.example.jacob.inclass10;

/*
In-class Assignment 10
Group 21 - Jacob Stern and Prayas Rode
*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoginAsync.ILogin {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailField = findViewById(R.id.emailField);
                EditText passwordField = findViewById(R.id.passwordField);
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (email.length() == 0 || password.length() == 0) {
                    Toast.makeText(MainActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                } else {
                    LoginAsync loginAsync = new LoginAsync(MainActivity.this);
                    loginAsync.execute(email, password);
                }
            }
        });
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        String token = preferences.getString(getString(R.string.session_token), null);
        if (token != null) {
            Intent intent = new Intent(this, ThreadListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void handleLogin(SessionInfo info) {
        if (info != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.session_token), info.token);
            editor.putString(getString(R.string.user_id), info.user_id);
            editor.putString(getString(R.string.user_first_name), info.user_fname);
            editor.putString(getString(R.string.user_last_name), info.user_lname);
            editor.apply();
            Intent intent = new Intent(this, ThreadListActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
}
