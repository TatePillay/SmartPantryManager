package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    EditText etName, etQuantity, etUnit, etExpiry;
    Button btnSave;
    DatabaseHelper dbHelper;
    long editId = -1; // -1 means we are adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DatabaseHelper(this);

        // Check if we are editing an existing ingredient
        if (getIntent().hasExtra("EDIT_ID")) {
            editId = getIntent().getLongExtra("EDIT_ID", -1);
            etName.setText(getIntent().getStringExtra("EDIT_NAME"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("EDIT_QTY", 0)));
            etUnit.setText(getIntent().getStringExtra("EDIT_UNIT"));
            etExpiry.setText(getIntent().getStringExtra("EDIT_EXPIRY"));
            btnSave.setText("Update Ingredient"); // Change button text
        }

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
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NAME, name);
        values.put(DatabaseHelper.COL_QTY, quantity);
        values.put(DatabaseHelper.COL_UNIT, unit);
        values.put(DatabaseHelper.COL_EXPIRY, expiry);

        if (editId == -1) {
            // CREATE: Insert a new row
            long newRowId = db.insert(DatabaseHelper.TABLE_PANTRY, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Ingredient Added!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // UPDATE: Modify the existing row
            int rowsAffected = db.update(DatabaseHelper.TABLE_PANTRY, values, DatabaseHelper.COL_ID + " = ?", new String[]{String.valueOf(editId)});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Ingredient Updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}