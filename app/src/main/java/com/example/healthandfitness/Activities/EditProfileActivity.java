package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthandfitness.Database.DatabaseHelper;
import com.example.healthandfitness.R;

import android.content.Context;
import android.content.SharedPreferences;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText edtHeight, edtWeight, edtGoal, edtCalorieTarget;
    private RadioGroup rgGender, rgActivityLevel;
    private Button btnSave,btnlogout;
    private String email; // Email of the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        databaseHelper = new DatabaseHelper(this);

        // Retrieve email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        // Check if email is null
        if (email == null) {
            Toast.makeText(this, "No email found in preferences. Please log in again.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if email is missing
            return;
        }

        // Initialize views
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        edtGoal = findViewById(R.id.edtGoal);
        edtCalorieTarget = findViewById(R.id.edtCalorieTarget);
        rgGender = findViewById(R.id.rgGender);
        rgActivityLevel = findViewById(R.id.rgActivityLevel);
        btnSave = findViewById(R.id.btnSave);
        btnlogout = findViewById(R.id.btnlogout);

        // Load current user data
        loadUserData();

        // Save button click listener
        btnSave.setOnClickListener(v -> saveProfile());
        btnlogout.setOnClickListener(v -> logout());
    }
    private void logout() {
        // Clear email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email"); // Remove the stored email
        editor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish the current activity
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("Range")
    private void loadUserData() {
        Cursor cursor = databaseHelper.getUserData(email);
        if (cursor != null && cursor.moveToFirst()) {
            edtHeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_HEIGHT))));
            edtWeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT))));
            edtGoal.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GOAL)));

            double calorieTarget = cursor.isNull(cursor.getColumnIndex(DatabaseHelper.COLUMN_CALORIE_TARGET))
                    ? 0.0 // Default value if null
                    : cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_CALORIE_TARGET));
            edtCalorieTarget.setText(String.valueOf(calorieTarget));

            String gender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER));
            if ("Male".equals(gender)) {
                ((RadioButton) rgGender.findViewById(R.id.radioMale)).setChecked(true);
            } else if ("Female".equals(gender)) {
                ((RadioButton) rgGender.findViewById(R.id.radioFemale)).setChecked(true);
            }

            String activityLevel = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACTIVITY_LEVEL));
            switch (activityLevel) {
                case "Sedentary":
                    ((RadioButton) rgActivityLevel.findViewById(R.id.radioSedentary)).setChecked(true);
                    break;
                case "Moderate":
                    ((RadioButton) rgActivityLevel.findViewById(R.id.radioModerate)).setChecked(true);
                    break;
                case "Active":
                    ((RadioButton) rgActivityLevel.findViewById(R.id.radioActive)).setChecked(true);
                    break;
            }
            cursor.close();
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        double height = Double.parseDouble(edtHeight.getText().toString());
        double weight = Double.parseDouble(edtWeight.getText().toString());
        String goal = edtGoal.getText().toString();
        double calorieTarget = Double.parseDouble(edtCalorieTarget.getText().toString());

        int genderRadioButtonId = rgGender.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(genderRadioButtonId);
        String gender = selectedGender != null ? selectedGender.getText().toString() : "";

        int activityLevelRadioButtonId = rgActivityLevel.getCheckedRadioButtonId();
        RadioButton selectedActivityLevel = findViewById(activityLevelRadioButtonId);
        String activityLevel = selectedActivityLevel != null ? selectedActivityLevel.getText().toString() : "";

        double calorieBurnTarget = 0;
        boolean success = databaseHelper.updateUser(
                email, // Use the email retrieved from SharedPreferences
                height,
                weight,
                gender,
                activityLevel,
                goal,
                calorieTarget,
                calorieBurnTarget
        );

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
