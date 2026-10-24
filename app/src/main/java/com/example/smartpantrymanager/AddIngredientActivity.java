package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AddIngredientActivity extends AppCompatActivity {

    EditText etName, etQuantity, etUnit, etExpiry;
    Button btnSave;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PANTRY_DEBUG", "AddIngredientActivity started");

        setContentView(R.layout.activity_add_ingredient);
        Log.d("PANTRY_DEBUG", "Layout loaded");

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        Log.d("PANTRY_DEBUG", "Views linked");

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(qtyStr)) {
            Toast.makeText(this, "Name and Quantity are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NAME, name);
        values.put(DatabaseHelper.COL_QTY, quantity);
        values.put(DatabaseHelper.COL_UNIT, unit);
        values.put(DatabaseHelper.COL_EXPIRY, expiry);

        long newRowId = db.insert(DatabaseHelper.TABLE_PANTRY, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Ingredient Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving ingredient", Toast.LENGTH_SHORT).show();
        }
    }
}