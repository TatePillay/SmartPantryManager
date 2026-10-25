package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    TextView tvDetailTitle, tvDetailInstructions;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailInstructions = findViewById(R.id.tvDetailInstructions);
        dbHelper = new DatabaseHelper(this);

        // Get the recipe name passed from the previous screen
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");

        if (recipeName != null) {
            tvDetailTitle.setText(recipeName);
            loadRecipeDetails(recipeName);
        }
    }

    private void loadRecipeDetails(String recipeName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Find the recipe in the database
        Cursor cursor = db.query(DatabaseHelper.TABLE_RECIPES,
                null,
                DatabaseHelper.COL_RECIPE_NAME + " = ?",
                new String[]{recipeName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int instructionsIndex = cursor.getColumnIndex(DatabaseHelper.COL_INSTRUCTIONS);
            String instructions = cursor.getString(instructionsIndex);

            tvDetailInstructions.setText(instructions);
            cursor.close();
        }
    }
}