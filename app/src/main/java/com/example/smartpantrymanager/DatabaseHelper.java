package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 3; // Incremented to 3 to force update

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

    // Linking Table (New requirement!)
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients_table";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_REQ_INGREDIENT = "ingredient_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Create Pantry Table
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QTY + " REAL, " +
                COL_UNIT + " TEXT, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(createPantryTable);

        // 2. Create Recipe Table
        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT, " +
                COL_INSTRUCTIONS + " TEXT)";
        db.execSQL(createRecipeTable);

        // 3. Create Linking Table
        String createLinkTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_ID + " INTEGER, " +
                COL_REQ_INGREDIENT + " TEXT)";
        db.execSQL(createLinkTable);

        // --- SEED 20 RECIPES AND THEIR INGREDIENTS ---
        addRecipeWithIngredients(db, "Scrambled Eggs", "Beat eggs, cook in pan with butter.", new String[]{"eggs", "butter"});
        addRecipeWithIngredients(db, "Tomato Pasta", "Boil pasta, mix with tomatoes.", new String[]{"pasta", "tomatoes"});
        addRecipeWithIngredients(db, "Cheese Omelette", "Whisk eggs with cheese, cook in pan.", new String[]{"eggs", "cheese"});
        addRecipeWithIngredients(db, "Egg Sandwich", "Fry egg, put on bread.", new String[]{"eggs", "bread"});
        addRecipeWithIngredients(db, "Tomato Soup", "Blend tomatoes, boil, add salt.", new String[]{"tomatoes", "salt"});
        addRecipeWithIngredients(db, "Garlic Bread", "Toast bread with butter and garlic.", new String[]{"bread", "butter", "garlic"});
        addRecipeWithIngredients(db, "Mac and Cheese", "Boil pasta, add cheese and milk.", new String[]{"pasta", "cheese", "milk"});
        addRecipeWithIngredients(db, "Pancakes", "Mix flour, milk, eggs. Fry in pan.", new String[]{"flour", "milk", "eggs"});
        addRecipeWithIngredients(db, "Tomato Salad", "Chop tomatoes, add salt and oil.", new String[]{"tomatoes", "salt", "oil"});
        addRecipeWithIngredients(db, "Cheese Toast", "Melt cheese on bread.", new String[]{"bread", "cheese"});
        addRecipeWithIngredients(db, "Pasta Salad", "Cool pasta, add tomatoes and oil.", new String[]{"pasta", "tomatoes", "oil"});
        addRecipeWithIngredients(db, "French Toast", "Dip bread in egg and milk, fry.", new String[]{"bread", "eggs", "milk"});
        addRecipeWithIngredients(db, "Butter Pasta", "Boil pasta, mix with butter.", new String[]{"pasta", "butter"});
        addRecipeWithIngredients(db, "Fried Eggs", "Fry eggs in oil.", new String[]{"eggs", "oil"});
        addRecipeWithIngredients(db, "Cheese Sauce", "Melt cheese with milk.", new String[]{"cheese", "milk"});
        addRecipeWithIngredients(db, "Garlic Pasta", "Boil pasta, mix with garlic and oil.", new String[]{"pasta", "garlic", "oil"});
        addRecipeWithIngredients(db, "Bread and Butter", "Spread butter on bread.", new String[]{"bread", "butter"});
        addRecipeWithIngredients(db, "Milk Toast", "Soak bread in milk.", new String[]{"bread", "milk"});
        addRecipeWithIngredients(db, "Tomato Sandwich", "Slice tomatoes on bread.", new String[]{"tomatoes", "bread"});
        addRecipeWithIngredients(db, "Cheesy Eggs", "Scramble eggs with cheese.", new String[]{"eggs", "cheese"});
    }

    private void addRecipeWithIngredients(SQLiteDatabase db, String name, String instructions, String[] ingredients) {
        // Insert Recipe
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        // Insert Ingredients for this recipe
        for (String ingredient : ingredients) {
            ContentValues linkValues = new ContentValues();
            linkValues.put(COL_RECIPE_ID, recipeId);
            linkValues.put(COL_REQ_INGREDIENT, ingredient);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, linkValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }
}