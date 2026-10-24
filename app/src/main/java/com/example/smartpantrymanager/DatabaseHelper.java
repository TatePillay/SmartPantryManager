package com.example.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2; // Changed to 2 to force an update

    // Pantry Table
    public static final String TABLE_PANTRY = "pantry_table";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "ingredient_name";
    public static final String COL_QTY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiration_date";

    // Recipe Table
    public static final String TABLE_RECIPES = "recipe_table";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_INSTRUCTIONS = "instructions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QTY + " REAL, " +
                COL_UNIT + " TEXT, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(createPantryTable);

        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT, " +
                COL_INSTRUCTIONS + " TEXT)";
        db.execSQL(createRecipeTable);

        // --- SEED THE RECIPES ---
        insertRecipe(db, "Scrambled Eggs", "Beat eggs, cook in pan with butter.");
        insertRecipe(db, "Tomato Pasta", "Boil pasta, mix with tomatoes.");
        insertRecipe(db, "Cheese Omelette", "Whisk eggs with cheese, cook in pan.");
        insertRecipe(db, "Egg Sandwich", "Fry egg, put on bread.");
        insertRecipe(db, "Tomato Soup", "Blend tomatoes, boil, add salt.");
    }

    private void insertRecipe(SQLiteDatabase db, String name, String instructions) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_INSTRUCTIONS, instructions);
        db.insert(TABLE_RECIPES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }
}