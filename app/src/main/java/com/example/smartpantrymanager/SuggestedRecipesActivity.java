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

        // Get all recipes
        Cursor recipeCursor = db.query(DatabaseHelper.TABLE_RECIPES, null, null, null, null, null, null);

        if (recipeCursor != null && recipeCursor.moveToFirst()) {
            do {
                int nameIndex = recipeCursor.getColumnIndex(DatabaseHelper.COL_RECIPE_NAME);
                int idIndex = recipeCursor.getColumnIndex(DatabaseHelper.COL_ID);
                String recipeName = recipeCursor.getString(nameIndex);
                int recipeId = recipeCursor.getInt(idIndex);

                if (hasAllIngredients(db, recipeId)) {
                    matchedRecipes.add(recipeName);
                }
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();

        // Display
        if (matchedRecipes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvSuggestedRecipes.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvSuggestedRecipes.setVisibility(View.VISIBLE);
            String selection = DatabaseHelper.COL_RECIPE_NAME + " IN (" + makePlaceholders(matchedRecipes.size()) + ")";
            Cursor finalCursor = db.query(DatabaseHelper.TABLE_RECIPES, null, selection, matchedRecipes.toArray(new String[0]), null, null, null);
            adapter = new RecipeAdapter(this, finalCursor);
            rvSuggestedRecipes.setAdapter(adapter);
        }
    }

    // NEW LOGIC: Uses the linking table
    private boolean hasAllIngredients(SQLiteDatabase db, int recipeId) {
        // Get all required ingredients for this recipe
        Cursor reqCursor = db.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                new String[]{DatabaseHelper.COL_REQ_INGREDIENT},
                DatabaseHelper.COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        if (reqCursor != null && reqCursor.moveToFirst()) {
            do {
                int reqIndex = reqCursor.getColumnIndex(DatabaseHelper.COL_REQ_INGREDIENT);
                String requiredIngredient = reqCursor.getString(reqIndex);

                if (!isInPantry(db, requiredIngredient)) {
                    reqCursor.close();
                    return false; // Missing an ingredient!
                }
            } while (reqCursor.moveToNext());
        }
        reqCursor.close();
        return true;
    }

    private boolean isInPantry(SQLiteDatabase db, String ingredientName) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PANTRY +
                " WHERE LOWER(" + DatabaseHelper.COL_NAME + ") LIKE '%" + ingredientName.toLowerCase() + "%'";
        Cursor cursor = db.rawQuery(query, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private String makePlaceholders(int len) {
        if (len < 1) return null;
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) sb.append(",?");
        return sb.toString();
    }
}