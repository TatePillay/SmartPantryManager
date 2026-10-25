package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    RecyclerView rvSuggestedRecipes;
    TextView tvEmptyState;
    DatabaseHelper dbHelper;
    RecipeAdapter adapter;

    // This list will hold the names of the recipes that match
    List<String> matchedRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        rvSuggestedRecipes = findViewById(R.id.rvSuggestedRecipes);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        dbHelper = new DatabaseHelper(this);

        rvSuggestedRecipes.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        runStrictMatching();
    }

    private void runStrictMatching() {
        matchedRecipes.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 1. Get all recipes
        Cursor recipeCursor = db.query(DatabaseHelper.TABLE_RECIPES, null, null, null, null, null, null);

        if (recipeCursor != null && recipeCursor.moveToFirst()) {
            do {
                int nameIndex = recipeCursor.getColumnIndex(DatabaseHelper.COL_RECIPE_NAME);
                int idIndex = recipeCursor.getColumnIndex(DatabaseHelper.COL_ID);

                String recipeName = recipeCursor.getString(nameIndex);
                int recipeId = recipeCursor.getInt(idIndex);

                // 2. For each recipe, check if we have all required ingredients in the pantry
                if (hasAllIngredients(db, recipeId, recipeName)) {
                    matchedRecipes.add(recipeName);
                }

            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();

        // 3. Display the results
        if (matchedRecipes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvSuggestedRecipes.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvSuggestedRecipes.setVisibility(View.VISIBLE);

            // Query the database again to get the full details of the matched recipes
            String selection = DatabaseHelper.COL_RECIPE_NAME + " IN (" + makePlaceholders(matchedRecipes.size()) + ")";
            Cursor finalCursor = db.query(DatabaseHelper.TABLE_RECIPES, null, selection, matchedRecipes.toArray(new String[0]), null, null, null);

            adapter = new RecipeAdapter(this, finalCursor);
            rvSuggestedRecipes.setAdapter(adapter);
        }
    }

    // --- THIS IS THE STRICT MATCHING LOGIC ---
    private boolean hasAllIngredients(SQLiteDatabase db, int recipeId, String recipeName) {
        List<String> requiredIngredients = new ArrayList<>();

        // Hardcoded requirements for our seeded recipes
        switch (recipeName) {
            case "Scrambled Eggs":
                requiredIngredients.add("eggs");
                requiredIngredients.add("butter");
                break;
            case "Tomato Pasta":
                requiredIngredients.add("pasta");
                requiredIngredients.add("tomatoes");
                break;
            case "Cheese Omelette":
                requiredIngredients.add("eggs");
                requiredIngredients.add("cheese");
                break;
            case "Egg Sandwich":
                requiredIngredients.add("eggs");
                requiredIngredients.add("bread");
                break;
            case "Tomato Soup":
                requiredIngredients.add("tomatoes");
                break;
        }

        // Check if the pantry has each of these ingredients
        for (String required : requiredIngredients) {
            if (!isInPantry(db, required)) {
                return false; // If even one is missing, fail the whole recipe
            }
        }
        return true;
    }

    private boolean isInPantry(SQLiteDatabase db, String ingredientName) {
        // Look for the ingredient in the pantry (ignoring case, and checking if the name contains the word)
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PANTRY +
                " WHERE LOWER(" + DatabaseHelper.COL_NAME + ") LIKE '%" + ingredientName.toLowerCase() + "%'";
        Cursor cursor = db.rawQuery(query, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}