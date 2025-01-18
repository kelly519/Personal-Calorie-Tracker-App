package com.example.healthandfitness.Activities;

import static com.example.healthandfitness.Database.CalorieDatabaseHelper.COLUMN_CALORIES;
import static com.example.healthandfitness.Database.CalorieDatabaseHelper.COLUMN_CARBS;
import static com.example.healthandfitness.Database.CalorieDatabaseHelper.COLUMN_FAT;
import static com.example.healthandfitness.Database.CalorieDatabaseHelper.COLUMN_MEAL;
import static com.example.healthandfitness.Database.CalorieDatabaseHelper.COLUMN_PROTEIN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthandfitness.Database.CalorieDatabaseHelper;
import com.example.healthandfitness.Database.DatabaseHelper;
import com.example.healthandfitness.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalorieTrackerActivity extends AppCompatActivity {

    private static final String API_KEY = "XBYq5Wr2xuRD8lEMTID1Hw==1QXz5trngShxEWsB";
    private static final String BASE_URL = "https://api.calorieninjas.com/v1/nutrition?query=";

    private CircularProgressBar caloriesConsumedProgressBar;
    private TextView breakfastCaloriesLabel, lunchCaloriesLabel, dinnerCaloriesLabel;
    private EditText foodInput, amountInput;
    private Button breakfastButton, lunchButton, dinnerButton;


    private int totalCaloriesConsumed = 0;
    private int breakfastCalories = 0, lunchCalories = 0, dinnerCalories = 0;
    private double totalFat = 0.0, totalProtein = 0.0, totalCarbs = 0.0;

    private String selectedMealType = "Breakfast";
    private OkHttpClient httpClient;
    private CalorieDatabaseHelper dbHelper;
    private DatabaseHelper dbHelper12;
    private String USER_ID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_tracker);

        dbHelper12 = new DatabaseHelper(this);

        // Retrieve USER_ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("email", null);

        // Set daily calorie goal
        TextView goal = findViewById(R.id.goal);
        int dailyCalorieGoal = dbHelper12.getCalorieTargetByEmail(USER_ID);
        goal.setText(String.valueOf(dailyCalorieGoal) + " kcal");

        // Initialize views
        caloriesConsumedProgressBar = findViewById(R.id.circularProgressBar);
        caloriesConsumedProgressBar.setProgressBarWidth(15f);
        caloriesConsumedProgressBar.setBackgroundProgressBarWidth(10f);

        foodInput = findViewById(R.id.food_input);
        amountInput = findViewById(R.id.amount_input);

        breakfastCaloriesLabel = findViewById(R.id.breakfast_calories_label);
        lunchCaloriesLabel = findViewById(R.id.lunch_calories_label);
        dinnerCaloriesLabel = findViewById(R.id.dinner_calories_label);

        // Initialize buttons
        breakfastButton = findViewById(R.id.breakfast_button);
        lunchButton = findViewById(R.id.lunch_button);
        dinnerButton = findViewById(R.id.dinner_button);

        // Set meal type selection
        breakfastButton.setOnClickListener(v -> {
            resetButtonColors();
            breakfastButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
            selectedMealType = "Breakfast";
            Toast.makeText(this, "Selected: Breakfast", Toast.LENGTH_SHORT).show();
        });

        lunchButton.setOnClickListener(v -> {
            resetButtonColors();
            lunchButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
            selectedMealType = "Lunch";
            Toast.makeText(this, "Selected: Lunch", Toast.LENGTH_SHORT).show();
        });

        dinnerButton.setOnClickListener(v -> {
            resetButtonColors();
            dinnerButton.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
            selectedMealType = "Dinner";
            Toast.makeText(this, "Selected: Dinner", Toast.LENGTH_SHORT).show();
        });

        // Add meal button
        Button addMealButton = findViewById(R.id.add_meal_button);
        addMealButton.setOnClickListener(v -> {
            String foodItem = foodInput.getText().toString();
            String amount = amountInput.getText().toString();

            if (foodItem.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "Please enter food item and amount", Toast.LENGTH_SHORT).show();
                return;
            }

            fetchCalorieData(foodItem, amount);
        });

        // Initialize OkHttp client and database
        httpClient = new OkHttpClient();
        dbHelper = new CalorieDatabaseHelper(this);

        // Add click listeners for meal labels to show meal items
        breakfastCaloriesLabel.setOnClickListener(v -> showMealItemsDialog("Breakfast"));
        lunchCaloriesLabel.setOnClickListener(v -> showMealItemsDialog("Lunch"));
        dinnerCaloriesLabel.setOnClickListener(v -> showMealItemsDialog("Dinner"));

        // Show saved data
        showLastSavedDataOfTheDay();
    }

    private void resetButtonColors() {
        breakfastButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        lunchButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        dinnerButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void fetchCalorieData(String foodQuery, String amount) {
        String query = foodQuery + " " + amount + "g";
        String url = BASE_URL + query;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(CalorieTrackerActivity.this, "API request failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        parseNutritionResponse(responseBody, foodInput.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void parseNutritionResponse(String responseBody, String foodItem) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray items = jsonResponse.getJSONArray("items");
            int mealCalories = 0;
            double mealFat = 0.0, mealProtein = 0.0, mealCarbs = 0.0;

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                mealCalories += item.getInt("calories");
                mealFat += item.optDouble("fat_total_g", 0.0);
                mealProtein += item.optDouble("protein_g", 0.0);
                mealCarbs += item.optDouble("carbohydrates_total_g", 0.0);
            }

            totalCaloriesConsumed += mealCalories;
            totalFat += mealFat;
            totalProtein += mealProtein;
            totalCarbs += mealCarbs;

            // Save to database with food item
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHelper.insertCalorieData(USER_ID, date, selectedMealType,
                    foodItem, mealCalories, mealFat, mealProtein, mealCarbs);

            switch (selectedMealType) {
                case "Breakfast":
                    breakfastCalories += mealCalories;
                    break;
                case "Lunch":
                    lunchCalories += mealCalories;
                    break;
                case "Dinner":
                    dinnerCalories += mealCalories;
                    break;
            }

            runOnUiThread(() -> {
                updateUI();
                // Clear input fields after adding meal
                foodInput.setText("");
                amountInput.setText("");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLastSavedDataOfTheDay() {
        // Reset all values
        totalCaloriesConsumed = 0;
        breakfastCalories = 0;
        lunchCalories = 0;
        dinnerCalories = 0;
        totalFat = 0.0;
        totalProtein = 0.0;
        totalCarbs = 0.0;

        // Use getMealItemsForToday instead of getMealsForToday
        Cursor cursor = dbHelper.getMealItemsForToday(USER_ID, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String mealType = cursor.getString(cursor.getColumnIndex(COLUMN_MEAL));
                @SuppressLint("Range") int calories = cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES));
                @SuppressLint("Range") double fat = cursor.getDouble(cursor.getColumnIndex(COLUMN_FAT));
                @SuppressLint("Range") double protein = cursor.getDouble(cursor.getColumnIndex(COLUMN_PROTEIN));
                @SuppressLint("Range") double carbs = cursor.getDouble(cursor.getColumnIndex(COLUMN_CARBS));

                totalCaloriesConsumed += calories;
                totalFat += fat;
                totalProtein += protein;
                totalCarbs += carbs;

                switch (mealType) {
                    case "Breakfast":
                        breakfastCalories += calories;
                        break;
                    case "Lunch":
                        lunchCalories += calories;
                        break;
                    case "Dinner":
                        dinnerCalories += calories;
                        break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        updateUI();
    }

    private void updateUI() {
        // Günlük hedefi al
        int dailyCalorieGoal = dbHelper12.getCalorieTargetByEmail(USER_ID);

        // Kalori progress bar'ını güncelle
        int calorieProgress = (totalCaloriesConsumed * 100) / dailyCalorieGoal;
        caloriesConsumedProgressBar.setProgress(calorieProgress);

        // Besin öğeleri progress bar'larını güncelle
        updateProgressBar(R.id.protein_progress_bar, R.id.protein_progress_label,
                totalProtein, 121.0, "Protein");
        updateProgressBar(R.id.fat_progress_bar, R.id.fat_progress_label,
                totalFat, 28.7, "Fat");
        updateProgressBar(R.id.carbs_progress_bar, R.id.carbs_progress_label,
                totalCarbs, 161.6, "Carbs");

        // Öğün kalori etiketlerini güncelle
        breakfastCaloriesLabel.setText("Breakfast: " + breakfastCalories + " kcal");
        lunchCaloriesLabel.setText("Lunch: " + lunchCalories + " kcal");
        dinnerCaloriesLabel.setText("Dinner: " + dinnerCalories + " kcal");
    }

    private void updateProgressBar(int progressBarId, int labelId,
                                   double currentValue, double maxValue,
                                   String nutrientName) {
        ProgressBar progressBar = findViewById(progressBarId);
        TextView label = findViewById(labelId);

        int progress = (int) ((currentValue / maxValue) * 100);
        progressBar.setProgress(progress);

        label.setText(String.format(Locale.getDefault(), "%.1f of %.1f",
                currentValue, maxValue));
    }

    private void showMealItemsDialog(String mealType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mealType + " Meals");

        // Bugünün yiyeceklerini veritabanından çek
        Cursor cursor = dbHelper.getMealItemsForToday(USER_ID, mealType);

        // Cursor'ı listeye dönüştür
        final List<MealItem> mealItems = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String foodItem = cursor.getString(cursor.getColumnIndex("food_item"));
                @SuppressLint("Range") int calories = cursor.getInt(cursor.getColumnIndex("calories"));

                mealItems.add(new MealItem(id, foodItem, calories));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Özel bir adaptör oluştur
        ArrayAdapter<MealItem> adapter = new ArrayAdapter<MealItem>(this,
                android.R.layout.simple_list_item_1, mealItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    MealItem item = mealItems.get(position);
                    textView.setText(item.foodItem + " (" + item.calories + " kcal) ");
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
                }

                return view;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            // Silme işlemi için dialog
            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
            deleteBuilder.setTitle("Delete Meal Item");
            deleteBuilder.setMessage("Do you want to delete this item?");

            deleteBuilder.setPositiveButton("Delete", (deleteDialog, deleteWhich) -> {
                // Seçilen öğeyi sil
                long itemId = mealItems.get(which).id;
                dbHelper.deleteMealItem(itemId);

                // UI'ı güncelle
                updateAfterDeletion(mealType);
            });

            deleteBuilder.setNegativeButton("Cancel", null);
            deleteBuilder.show();
        });

        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private void updateAfterDeletion(String mealType) {
        showLastSavedDataOfTheDay();
    }

    // Yardımcı sınıf
    private static class MealItem {
        long id;
        String foodItem;
        int calories;

        MealItem(long id, String foodItem, int calories) {
            this.id = id;
            this.foodItem = foodItem;
            this.calories = calories;
        }
    }
}