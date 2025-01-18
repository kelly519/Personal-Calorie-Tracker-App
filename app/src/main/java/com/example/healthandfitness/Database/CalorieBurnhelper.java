package com.example.healthandfitness.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalorieBurnhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "healthandfitness.db";
    private static final int DATABASE_VERSION = 2;

    public CalorieBurnhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table to store calorie intake data
        db.execSQL("CREATE TABLE calorie_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "date TEXT, " +
                "meal_type TEXT, " +
                "calories INTEGER, " +
                "fat REAL, " +
                "protein REAL, " +
                "carbs REAL)");

        // Table to store workout data
        db.execSQL("CREATE TABLE workout_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "date TEXT, " +
                "calories_burned INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE workout_data ADD COLUMN email TEXT");
        }
    }

    /**
     * Insert workout data for a specific user.
     *
     * @param email           User email
     * @param date            Workout date
     * @param caloriesBurned  Calories burned during the workout
     */
    public void insertWorkoutData(String email, String date, int caloriesBurned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("date", date);
        values.put("calories_burned", caloriesBurned);
        db.insert("workout_data", null, values);
        db.close();
    }

    /**
     * Get total calories burned by a user for today.
     *
     * @param email User email
     * @return Total calories burned today
     */
    public int getTodayCaloriesBurned(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery(
                "SELECT SUM(calories_burned) FROM workout_data WHERE email = ? AND date = ?",
                new String[]{email, date}
        );

        int totalBurned = 0;
        if (cursor.moveToFirst()) {
            totalBurned = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return totalBurned;
    }

    /**
     * Insert calorie intake data for a meal.
     *
     * @param email    User email
     * @param date     Date of the meal
     * @param mealType Meal type (e.g., breakfast, lunch, dinner)
     * @param calories Calories in the meal
     * @param fat      Fat content in the meal
     * @param protein  Protein content in the meal
     * @param carbs    Carbohydrates in the meal
     */
    public void insertCalorieData(String email, String date, String mealType, int calories, float fat, float protein, float carbs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("date", date);
        values.put("meal_type", mealType);
        values.put("calories", calories);
        values.put("fat", fat);
        values.put("protein", protein);
        values.put("carbs", carbs);
        db.insert("calorie_data", null, values);
        db.close();
    }

    /**
     * Get total calorie intake for today for a specific user.
     *
     * @param email User email
     * @return Total calorie intake today
     */
    public int getTodayCalorieIntake(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery(
                "SELECT SUM(calories) FROM calorie_data WHERE email = ? AND date = ?",
                new String[]{email, date}
        );

        int totalCalories = 0;
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return totalCalories;
    }
}
