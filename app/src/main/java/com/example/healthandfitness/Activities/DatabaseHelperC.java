package com.example.healthandfitness.Activities;



import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperC extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "health_fitness.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    private static final String TABLE_TOTAL_CALORIES = "total_calories";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_TOTAL_CALORIES = "total";

    public DatabaseHelperC(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TOTAL_CALORIES + " ("
                + COLUMN_USER_ID + " TEXT PRIMARY KEY, "
                + COLUMN_TOTAL_CALORIES + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOTAL_CALORIES);
        onCreate(db);
    }

    // Initialize total calories for a user if not already present
    public void initializeTotalCalories(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TOTAL_CALORIES + " WHERE " +
                COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_TOTAL_CALORIES, 0);
            db.insert(TABLE_TOTAL_CALORIES, null, values);
        }
        if (cursor != null) cursor.close();
    }

    // Add snack calories for a specific user
    @SuppressLint("Range")
    public void addSnackCalories(String userId, int caloriesToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Fetch current total calories for the user
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TOTAL_CALORIES + " FROM " +
                        TABLE_TOTAL_CALORIES + " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});

        int currentCalories = 0;
        if (cursor != null && cursor.moveToFirst()) {
            currentCalories = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_CALORIES));
        }
        if (cursor != null) cursor.close();

        // Update total calories
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOTAL_CALORIES, currentCalories + caloriesToAdd);

        db.update(TABLE_TOTAL_CALORIES, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    // Fetch total calories for a specific user
    @SuppressLint("Range")
    public int getTotalCalories(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TOTAL_CALORIES + " FROM " +
                        TABLE_TOTAL_CALORIES + " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});

        int totalCalories = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalCalories = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_CALORIES));
        }
        if (cursor != null) cursor.close();

        return totalCalories;
    }
}
