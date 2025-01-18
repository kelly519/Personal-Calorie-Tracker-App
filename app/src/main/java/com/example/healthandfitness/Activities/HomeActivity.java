package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthandfitness.Database.CalorieBurnhelper;
import com.example.healthandfitness.Database.CalorieDatabaseHelper;
import com.example.healthandfitness.Database.DatabaseHelper;
import com.example.healthandfitness.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class HomeActivity extends AppCompatActivity {

    private CircularProgressBar caloriesConsumedProgress;
    private CircularProgressBar caloriesBurnedProgress;
    private ProgressBar calorieDeficitProgress;
    private TextView calorieDeficitLabel;
    private TextView caloriesConsumedLabel;
    private TextView caloriesBurnedLabel;

    private LinearLayout programCard;
    private LinearLayout addMealCard;
    private LinearLayout myPlanCard;
    private LinearLayout challengeCard;

    private CalorieDatabaseHelper dbHelper;
    private CalorieBurnhelper dbHelper1;
    private DatabaseHelper dbHelper12;
    private DatabaseHelperC dbHelperC;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        caloriesConsumedProgress = findViewById(R.id.circularProgressBar);
        caloriesBurnedProgress = findViewById(R.id.circularProgressBarBurned);
        calorieDeficitProgress = findViewById(R.id.calorie_deficit_progress);
        calorieDeficitLabel = findViewById(R.id.calorie_deficit_label);
        caloriesConsumedLabel = findViewById(R.id.calories_consumed_label);
        caloriesBurnedLabel = findViewById(R.id.calories_burned_label);
        programCard = findViewById(R.id.programCard);
        addMealCard = findViewById(R.id.addMealCard);
        myPlanCard = findViewById(R.id.myPlanCard);
        challengeCard = findViewById(R.id.challengeCard);
// Set Width
        caloriesConsumedProgress.setProgressBarWidth(15f); // in DP
        caloriesConsumedProgress.setBackgroundProgressBarWidth(10f);
        caloriesBurnedProgress.setProgressBarWidth(15); // in DP
        caloriesBurnedProgress.setBackgroundProgressBarWidth(10);

        // Initialize database helper
        dbHelper = new CalorieDatabaseHelper(this);
        dbHelper1 = new CalorieBurnhelper(this);
        dbHelper12 = new DatabaseHelper(this);
        dbHelperC = new DatabaseHelperC(this);

        // Load calorie data and update the UI
        showCalorieData();

        // Set click listeners for cards
        setCardListeners();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh calorie data and update the progress bars
        showCalorieData();
    }

    private void showCalorieData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            caloriesConsumedLabel.setText("Error: Email not found in preferences.");
            return;
        }

        // Yakılan kalorileri al
        int caloriesBurned = dbHelper1.getTodayCaloriesBurned(email);

        // Alınan toplam kalorileri hesapla
        int caloriesConsumed1 = dbHelper.getTodayCaloriesConsumed(email);
        int caloriesConsumed2 = dbHelperC.getTotalCalories(email);
        int totalCaloriesConsumed = caloriesConsumed1 + caloriesConsumed2;

        // Hedefleri al
        int dailyCalorieGoal = dbHelper12.getCalorieTargetByEmail(email);
        double dailyCalorieBurnGoal = dbHelper12.getCalorieBurnTargetByEmail(email);
        String goalType = dbHelper12.getGoalByEmail(email);

        // Hedefe göre kalori açığı hedefini belirle
        int targetDeficit = 0;
        if (goalType.equalsIgnoreCase("Weight Loss")) {
            targetDeficit = dailyCalorieGoal-500; // Kilo vermek için günlük 500 kalori açığı
        } else if (goalType.equalsIgnoreCase("Muscle Gain")) {
            targetDeficit = dailyCalorieGoal+500;  // Kas kazanmak için günlük 500 kalori fazlası
        }else {
            targetDeficit = dailyCalorieGoal;
        }

        // Kalori açığını doğru şekilde hesapla
        // Negatif değer kalori açığını, pozitif değer kalori fazlasını gösterir
        int calorieDeficit = totalCaloriesConsumed - (dailyCalorieGoal + caloriesBurned);

        // Progress bar'ları güncelle
        float consumedPercentage = (totalCaloriesConsumed / (float) dailyCalorieGoal) * 100;
        float burnedPercentage = (caloriesBurned / (float) dailyCalorieBurnGoal) * 100;

        // UI güncellemeleri
        caloriesConsumedProgress.setProgressWithAnimation(consumedPercentage, 1000L);
        caloriesBurnedProgress.setProgressWithAnimation(burnedPercentage, 1000L);

        caloriesConsumedLabel.setText(totalCaloriesConsumed + " / " + dailyCalorieGoal + " kcal");
        caloriesBurnedLabel.setText(caloriesBurned + " / " + dailyCalorieBurnGoal + " kcal");

        // Kalori açığı etiketini güncelle
        String deficitText = Math.abs(calorieDeficit) + " / " +targetDeficit + " kcal ";
        deficitText += (calorieDeficit < 0) ? "deficit" : "surplus";
        calorieDeficitLabel.setText(deficitText);

        // Progress bar'ı hedefle karşılaştırarak güncelle
        float deficitProgress = (Math.abs(calorieDeficit) / (float) targetDeficit) * 100;
        calorieDeficitProgress.setProgress((int) Math.min(deficitProgress, 100));
    }

    private void setCardListeners() {
        programCard.setOnClickListener(v ->
        {
            Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
            startActivity(intent);

        } );

        addMealCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalorieTrackerActivity.class);
            startActivity(intent);
        });

        myPlanCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, WorkoutPlanActivity.class);
            startActivity(intent);
        });

        challengeCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChallengeActivity.class);
            startActivity(intent);

        } );
    }
}
