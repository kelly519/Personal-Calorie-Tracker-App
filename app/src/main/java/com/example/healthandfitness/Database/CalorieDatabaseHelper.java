package com.example.healthandfitness.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalorieDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "calorie_tracker.db";
    private static final int DATABASE_VERSION = 3; // Versiyon numarasını artırdık

    public static final String TABLE_CALORIES = "calories";
    public static final String TABLE_CALORIE_TRACKER = "calorie_tracker";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MEAL = "meal";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_PROTEIN = "protein";
    public static final String COLUMN_CARBS = "carbs";
    public static final String COLUMN_FOOD_ITEM = "food_item"; // Yeni kolon

    private static final String CREATE_TABLE_CALORIES = "CREATE TABLE " + TABLE_CALORIES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " TEXT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_MEAL + " TEXT, "
            + COLUMN_CALORIES + " INTEGER, "
            + COLUMN_FAT + " REAL, "
            + COLUMN_PROTEIN + " REAL, "
            + COLUMN_CARBS + " REAL)";

    private static final String CREATE_TABLE_CALORIE_TRACKER = "CREATE TABLE " + TABLE_CALORIE_TRACKER + " ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " TEXT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_MEAL + " TEXT, "
            + COLUMN_FOOD_ITEM + " TEXT, "
            + COLUMN_CALORIES + " INTEGER, "
            + COLUMN_FAT + " REAL, "
            + COLUMN_PROTEIN + " REAL, "
            + COLUMN_CARBS + " REAL)";

    public CalorieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CALORIE_TRACKER);
        db.execSQL(CREATE_TABLE_CALORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eğer veritabanı şemasını değiştirirseniz, bu metodu güncelleyin
        if (oldVersion < 2) {
            // Yeni tabloyu ekleyin
            db.execSQL(CREATE_TABLE_CALORIE_TRACKER);
        }
    }

    // Yeni metod: Food item bilgisiyle birlikte kaydetme
    public void insertCalorieData(String userId, String date, String mealType,
                                  String foodItem, int calories,
                                  double fat, double protein, double carbs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_MEAL, mealType);
        values.put(COLUMN_FOOD_ITEM, foodItem); // Yeni alan
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_FAT, fat);
        values.put(COLUMN_PROTEIN, protein);
        values.put(COLUMN_CARBS, carbs);

        // Calorie Tracker tablosuna kaydet
        db.insert(TABLE_CALORIE_TRACKER, null, values);

        // Orijinal calories tablosuna da kaydet
        db.insert(TABLE_CALORIES, null, values);
    }

    public Cursor getMealsForToday(String userId) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CALORIES, null,
                COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{userId, date}, null, null, null);
    }

    public int getTodayCaloriesConsumed(String userId) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();

        // Her iki tablodan da toplam kalorileri al
        String query = "SELECT SUM(" + COLUMN_CALORIES + ") FROM " + TABLE_CALORIES +
                " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?" +
                " UNION ALL " +
                "SELECT SUM(" + COLUMN_CALORIES + ") FROM " + TABLE_CALORIE_TRACKER +
                " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{userId, date, userId, date});

        int totalCalories = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                totalCalories += cursor.getInt(0);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return totalCalories;
    }

    public Cursor getMealItemsForToday(String userId, String mealType) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (mealType == null) {
            // If no meal type is specified, retrieve all meals for today
            return db.query(TABLE_CALORIE_TRACKER,
                    new String[]{"_id", COLUMN_MEAL, COLUMN_FOOD_ITEM, COLUMN_CALORIES, COLUMN_FAT, COLUMN_PROTEIN, COLUMN_CARBS},
                    COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                    new String[]{userId, today},
                    null, null, null);
        } else {
            // If a specific meal type is specified
            return db.query(TABLE_CALORIE_TRACKER,
                    new String[]{"_id", COLUMN_FOOD_ITEM, COLUMN_CALORIES, COLUMN_FAT, COLUMN_PROTEIN, COLUMN_CARBS},
                    COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ? AND " + COLUMN_MEAL + " = ?",
                    new String[]{userId, today, mealType},
                    null, null, null);
        }
    }

    public void deleteMealItem(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CALORIE_TRACKER, "_id = ?", new String[]{String.valueOf(itemId)});
    }
}