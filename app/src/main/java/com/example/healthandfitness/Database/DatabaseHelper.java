package com.example.healthandfitness.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_GOAL = "goal";
    public static final String COLUMN_CALORIE_TARGET = "calorie_target";
    public static final String COLUMN_CALORIE_BURN_TARGET = "calorie_burn_target"; // New column
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
    private static final String DATABASE_NAME = "healthApp.db";
    private static final int DATABASE_VERSION = 3; // Incremented database version

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String COLUMN_GYM_ACCESS = "gym_access";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_ACTIVITY_LEVEL + " TEXT, " +
                COLUMN_GYM_ACCESS + " INTEGER, " +
                COLUMN_GOAL + " TEXT, " +
                COLUMN_CALORIE_TARGET + " REAL, " +
                COLUMN_CALORIE_BURN_TARGET + " REAL DEFAULT 0)"; // Added new column
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) { // If version is less than 3, add the new column
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_CALORIE_BURN_TARGET + " REAL DEFAULT 0");
        }
    }

    public boolean insertUser(String email, String password, double height, double weight, String gender,
                              String activityLevel, boolean gymAccess, String goal, double calorieTarget, double calorieBurnTarget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_ACTIVITY_LEVEL, activityLevel);
        values.put(COLUMN_GYM_ACCESS, gymAccess ? 1 : 0);
        values.put(COLUMN_GOAL, goal);
        values.put(COLUMN_CALORIE_TARGET, calorieTarget);
        values.put(COLUMN_CALORIE_BURN_TARGET, calorieBurnTarget); // Insert new column value

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean userExists = cursor.moveToFirst(); // Check if a record exists
        cursor.close();
        return userExists;
    }

    public Cursor getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
    }

    public int getCalorieTargetByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int calorieTarget = 1200; // Default value

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CALORIE_TARGET + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            calorieTarget = cursor.getInt(0); // Get the first column value
        }
        cursor.close();
        return calorieTarget;
    }

    public String getGoalByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String goal = "Unknown"; // Default value if no goal is found

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GOAL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            goal = cursor.getString(0); // Get the first column value
        }
        cursor.close();
        return goal;
    }
    public boolean getGymAccessByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean gymAccess = false; // Default value

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GYM_ACCESS + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            gymAccess = cursor.getInt(0) == 1; // Convert int to boolean
        }
        cursor.close();
        return gymAccess;
    }
    public double getCalorieBurnTargetByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        double calorieBurnTarget = 0.0; // Default value

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CALORIE_BURN_TARGET + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            calorieBurnTarget = cursor.getDouble(0); // Get the first column value
        }
        cursor.close();
        return calorieBurnTarget;
    }

    public boolean updateUser(String email, double height, double weight, String gender,
                              String activityLevel, String goal, double calorieTarget, double calorieBurnTarget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_ACTIVITY_LEVEL, activityLevel);
        values.put(COLUMN_GOAL, goal);
        values.put(COLUMN_CALORIE_TARGET, calorieTarget);
        values.put(COLUMN_CALORIE_BURN_TARGET, calorieBurnTarget); // Update new column value

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
        return rowsAffected > 0;
    }
}
