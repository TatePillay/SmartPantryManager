package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnAddIngredient;
    DatabaseHelper dbHelper;
    PantryAdapter adapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Link the UI components
        recyclerView = findViewById(R.id.recyclerView);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        dbHelper = new DatabaseHelper(this);

        // 2. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. THIS IS THE MISSING PIECE: Open Add Screen on button click
        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load data every time the screen comes into view
        loadPantryData();
    }

    private void loadPantryData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (cursor != null) {
            cursor.close();
        }
        cursor = db.query(DatabaseHelper.TABLE_PANTRY, null, null, null, null, null, null);

        if (adapter == null) {
            adapter = new PantryAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        dbHelper.close();
    }
}