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

public class SignupActivity extends AppCompatActivity implements SignupAsync.ISignup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText firstNameField = findViewById(R.id.firstNameField);
                String firstName = firstNameField.getText().toString();
                EditText lastNameField = findViewById(R.id.lastNameField);
                String lastName = lastNameField.getText().toString();
                EditText emailField = findViewById(R.id.emailField);
                String email = emailField.getText().toString();
                EditText passwordField = findViewById(R.id.passwordField);
                String password = passwordField.getText().toString();
                EditText confirmPasswordField = findViewById(R.id.confirmPasswordField);
                String confirmPassword = confirmPasswordField.getText().toString();
                if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 ||
                        password.length() == 0 || confirmPassword.length() == 0) {
                    Toast.makeText(SignupActivity.this, "Please fill in every field", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "The password fields must match", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Your password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    SignupAsync signupAsync = new SignupAsync(SignupActivity.this);
                    signupAsync.execute(firstName, lastName, email, password);
                }
            }
        });
    }

    @Override
    public void handleSignup(SessionInfo info) {
        if (info != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.session_token), info.token);
            editor.apply();
            Intent intent = new Intent(this, ThreadListActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
}
