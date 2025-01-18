package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthandfitness.Database.DatabaseHelper;
import com.example.healthandfitness.R;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView signupButton = findViewById(R.id.signup);

        // Redirect to SignUpActivity
        signupButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.checkUser(email, password)) {
                // Save data to SharedPreferences
                saveUserToPreferences(email);

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish(); // Close LoginActivity
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserToPreferences(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("email", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}
