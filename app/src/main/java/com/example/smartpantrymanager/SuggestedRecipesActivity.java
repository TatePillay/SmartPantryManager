package com.example.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestedRecipesActivity extends AppCompatActivity {

    RecyclerView rvSuggestedRecipes;
    TextView tvEmptyState;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        rvSuggestedRecipes = findViewById(R.id.rvSuggestedRecipes);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        dbHelper = new DatabaseHelper(this);

        rvSuggestedRecipes.setLayoutManager(new LinearLayoutManager(this));

        // We will add the matching logic here soon!
        loadRecipes();
    }

    private void loadRecipes() {
        // Temporary: Just load ALL recipes for now to make sure the screen works.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_RECIPES, null, null, null, null, null, null);

        // (We will replace this with the strict matching logic next)
    }
}