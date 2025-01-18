package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthandfitness.Database.DatabaseHelper;
import com.example.healthandfitness.R;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    RadioGroup genderRadioGroup;
    RadioGroup activityLevelRadioGroup;
    RadioGroup goalRadioGroup;
    RadioGroup gymAccessRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseHelper = new DatabaseHelper(this);

        // Linking UI elements to Java variables
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView loginButton = findViewById(R.id.login);
        EditText emailInput = findViewById(R.id.edtEmail);
        EditText passwordInput = findViewById(R.id.edtPassword);
        EditText heightInput = findViewById(R.id.edtHeight);
        EditText weightInput = findViewById(R.id.edtWeight);
        EditText ageInput = findViewById(R.id.edtAge);
        genderRadioGroup = findViewById(R.id.rgGender);
        activityLevelRadioGroup = findViewById(R.id.rgActivityLevel);
        gymAccessRadioGroup = findViewById(R.id.rgGymAccess);
        goalRadioGroup = findViewById(R.id.rgGoal);
        Button signupButton = findViewById(R.id.btnSave);

        // Set listeners
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String ageString = ageInput.getText().toString();
            int age = ageString.isEmpty() ? 0 : Integer.parseInt(ageString);
            double height = Double.parseDouble(heightInput.getText().toString());
            double weight = Double.parseDouble(weightInput.getText().toString());
            String gender = getSelectedOption(genderRadioGroup);
            String activityLevel = getSelectedOption(activityLevelRadioGroup);
            boolean gymAccess = getSelectedOption(gymAccessRadioGroup).equalsIgnoreCase("Yes");
            String goal = getSelectedOption(goalRadioGroup);

            double calorieTarget = calculateCalorieTarget(weight, gender, height, age, activityLevel, goal);
            double calorieBurnTarget = calculateCalorieBurnTarget(weight, gender, height, age, activityLevel, goal);

            if (email.isEmpty() || password.isEmpty() || height == 0 || weight == 0 || gender.isEmpty() ||
                    activityLevel.isEmpty() || goal.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (databaseHelper.insertUser(email, password, height, weight, gender, activityLevel, gymAccess, goal, calorieTarget, calorieBurnTarget)) {
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedOption(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton != null ? selectedButton.getText().toString() : "";
    }

    private double calculateCalorieTarget(double weight, String gender, double height, int age, String activityLevel, String goal) {
        double bmr = 0;

        // BMR Hesaplama (Harris-Benedict Formülü)
        if (gender.equalsIgnoreCase("Male")) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else if (gender.equalsIgnoreCase("Female")) {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        // Aktivite Seviyesine Göre Çarpan
        double activityMultiplier = 1.2; // Sedanter başlangıç çarpanı
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                activityMultiplier = 1.2;
                break;
            case "moderate":
                activityMultiplier = 1.5;
                break;
            case "active":
                activityMultiplier = 1.8;
                break;
        }

        double totalCalories = bmr * activityMultiplier;

        // Hedef (Kilo Kaybı, Kas Kazanımı veya Bakım)
        int goalAdjustment = 0;
        if (goal.equalsIgnoreCase("Weight Loss")) {
            goalAdjustment = -500; // Kilo kaybı için kalori açığı
        } else if (goal.equalsIgnoreCase("Muscle Gain")) {
            goalAdjustment = 500; // Kas kazanımı için kalori fazlası
        } else {
            goalAdjustment = 0;
        }

        // Son kalori hedefini hesapla
        totalCalories += goalAdjustment;

        return totalCalories;
    }

    private double calculateCalorieBurnTarget(double weight, String gender, double height, int age, String activityLevel, String goal) {
        double calorieBurnTarget = 0;

        // Calculate based on the goal
        if (goal.equalsIgnoreCase("Weight Loss")) {
            calorieBurnTarget = weight * 10; // Simple example calculation for Weight Loss goal
        } else if (goal.equalsIgnoreCase("Muscle Gain")) {
            calorieBurnTarget = weight * 12; // Example for Muscle Gain goal
        } else {
            calorieBurnTarget = weight * 8; // Maintenance
        }

        return calorieBurnTarget;
    }
}
