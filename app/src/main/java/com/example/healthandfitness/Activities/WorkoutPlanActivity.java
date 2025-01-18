package com.example.healthandfitness.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthandfitness.Database.CalorieBurnhelper;
import com.example.healthandfitness.Database.DatabaseHelper;
import com.example.healthandfitness.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WorkoutPlanActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private LinearLayout exerciseListLayout;
    private TextView snackRewardText;
    private Button btnMarkDayComplete;
    private TextView exerciseCaloriesText;
    private int totalExerciseCalories = 0;

    private final HashMap<String, String[][]> gymWorkoutData = new HashMap<String, String[][]>() {{
        put("Saturday", new String[][]{
                {"Bench Press 3 x 12", "https://www.youtube.com/watch?v=yN6Q1UI_xkE", "120"},
                {"Shoulder Press 3 x 12", "https://www.youtube.com/watch?v=yN6Q1UI_xkE", "90"},
                {"Tricep Dips 3 x 12", "https://www.youtube.com/watch?v=yN6Q1UI_xkE", "60"}
        });
        put("Sunday", new String[][]{
                {"Deadlifts", "https://www.youtube.com/watch?v=PHcq8Bp89LIo", "150"},
                {"Pull-Ups", "https://www.youtube.com/watch?v=PHcq8Bp89LIo", "100"},
                {"Bicep Curls", "https://www.youtube.com/watch?v=PHcq8Bp89LIo", "70"}
        });
        put("Monday", new String[][]{
                {"Squats", "https://www.youtube.com/watch?v=EZQMBYoFoRs", "130"},
                {"Lunges", "https://www.youtube.com/watch?v=EZQMBYoFoRs", "80"},
                {"Leg Press", "https://www.youtube.com/watch?v=EZQMBYoFoRs", "100"}
        });
    }};

    private final HashMap<String, String[][]> homeWorkoutData = new HashMap<String, String[][]>() {{
        put("Saturday", new String[][]{
                {"Push-Ups", "https://www.youtube.com/watch?v=IODxDxX7oi4", "80"},
                {"Chair Dips", "https://www.youtube.com/watch?v=jox1rb5krQI", "60"},
                {"Plank", "https://www.youtube.com/watch?v=IODxDxX7oi4", "50"}
        });
        put("Sunday", new String[][]{
                {"Bodyweight Squats", "https://www.youtube.com/watch?v=PHcq8Bp89LIo", "100"},
                {"Lunges", "https://www.youtube.com/watch?v=PHcq8Bp89LIo", "70"},
                {"Mountain Climbers", "https://www.youtube.com/watch?v=PHcq8Bp89LIo", "90"}
        });
        put("Monday", new String[][]{
                {"Plank", "https://www.youtube.com/watch?v=EZQMBYoFoRs", "50"},
                {"Sit-Ups", "https://www.youtube.com/watch?v=EZQMBYoFoRs", "70"},
                {"Russian Twists", "https://www.youtube.com/watch?v=EZQMBYoFoRs", "60"}
        });
    }};

    private final HashMap<String, String[]> snackData = new HashMap<String, String[]>() {{
        put("Saturday", new String[]{"Push Day", "1200"});
        put("Sunday", new String[]{"Pull Day", "1500"});
        put("Monday", new String[]{"Leg Day", "1000"});
    }};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan);

        calendarView = findViewById(R.id.calendarView);
        exerciseListLayout = findViewById(R.id.exercise_list_layout);
        snackRewardText = findViewById(R.id.snack_reward_text);
        btnMarkDayComplete = findViewById(R.id.btn_mark_day_complete);
        exerciseCaloriesText = findViewById(R.id.exercise_calories_text);

        // Get email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        // Get gym access from DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean gymAccess = dbHelper.getGymAccessByEmail(email);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dayOfWeek = getDayOfWeek(year, month, dayOfMonth);
            updateWorkoutDetails(dayOfWeek, gymAccess);
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dayOfWeek = getDayOfWeek(year, month, dayOfMonth);
            updateWorkoutDetails(dayOfWeek, gymAccess);
        });

        btnMarkDayComplete.setOnClickListener(v -> markDayAsCompleted());

        String today = getDayOfWeek(Calendar.getInstance());
        updateWorkoutDetails(today, gymAccess);
    }

    private String getDayOfWeek(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getDayOfWeek(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return getDayOfWeek(calendar);
    }

    private void updateWorkoutDetails(String dayOfWeek, boolean gymAccess) {
        exerciseListLayout.removeAllViews();
        totalExerciseCalories = 0;
        exerciseCaloriesText.setText("Calories Burned: 0 kcal");

        HashMap<String, String[][]> workoutData = gymAccess ? gymWorkoutData : homeWorkoutData;

        if (workoutData.containsKey(dayOfWeek)) {
            String[][] exercises = workoutData.get(dayOfWeek);

            for (String[] exercise : exercises) {
                View exerciseItem = getLayoutInflater().inflate(R.layout.exercise_item, exerciseListLayout, false);

                TextView exerciseName = exerciseItem.findViewById(R.id.exercise_name);
                Button watchButton = exerciseItem.findViewById(R.id.watch_button);
                Button doneButton = exerciseItem.findViewById(R.id.done_button);

                int calories = Integer.parseInt(exercise[2]);

                exerciseName.setText(exercise[0] + " (" + calories + " kcal)");

                watchButton.setOnClickListener(v -> {
                    Intent intent = new Intent(this, VideoPlayerActivity.class);
                    intent.putExtra("video_url", exercise[1]);
                    startActivity(intent);
                });

                doneButton.setOnClickListener(v -> {
                    if (!doneButton.isEnabled()) return;

                    doneButton.setEnabled(false);
                    doneButton.setBackgroundColor(getResources().getColor(R.color.gray));

                    totalExerciseCalories += calories;
                    exerciseCaloriesText.setText("Calories Burned: " + totalExerciseCalories + " kcal");

                    Toast.makeText(this, exercise[0] + " marked as done! +" + calories + " kcal", Toast.LENGTH_SHORT).show();
                });

                exerciseListLayout.addView(exerciseItem);
            }

            String[] snack = snackData.get(dayOfWeek);
            snackRewardText.setText("Today's workout will make you burn: " + snack[0] + " (" + snack[1] + " kcal)");
        } else {
            snackRewardText.setText("No workout scheduled today.");
        }
    }

    private void markDayAsCompleted() {
        String dayOfWeek = getDayOfWeek(Calendar.getInstance());
        if (snackData.containsKey(dayOfWeek)) {
            new AlertDialog.Builder(this)
                    .setTitle("Day Completed!")
                    .setMessage("Total Day Calories: " + totalExerciseCalories + " kcal. Save data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        saveCaloriesToDatabase(totalExerciseCalories);

                        totalExerciseCalories = 0;
                        exerciseCaloriesText.setText("Calories Burned: 0 kcal");
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void saveCaloriesToDatabase(int caloriesBurned) {
        // Fetch today's calorie data from the database
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        CalorieBurnhelper dbHelper = new CalorieBurnhelper(this);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dbHelper.insertWorkoutData(email, date, caloriesBurned);

//        Toast.makeText(this, "Calories burned saved for user: " + userEmail, Toast.LENGTH_SHORT).show();
    }
}
