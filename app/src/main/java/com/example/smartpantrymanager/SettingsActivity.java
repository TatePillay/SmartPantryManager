package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Switch switchExpiringAlerts;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiringAlerts = findViewById(R.id.switchExpiringAlerts);
        sharedPreferences = getSharedPreferences("PantryPrefs", MODE_PRIVATE);

        // Load saved preference
        boolean alertsEnabled = sharedPreferences.getBoolean("expiring_alerts", true);
        switchExpiringAlerts.setChecked(alertsEnabled);

        // Save preference when toggled
        switchExpiringAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("expiring_alerts", isChecked);
            editor.apply();
        });
    }
}