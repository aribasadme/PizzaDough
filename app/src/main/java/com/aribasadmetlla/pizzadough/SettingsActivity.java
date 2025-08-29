package com.aribasadmetlla.pizzadough;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for SDK >= 29 and <= 35
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_settings);

        // Handles overlap between the navigation bar and the status bar
        View rootView = findViewById(R.id.settings_container);
        if (rootView != null) { // Add null check for safety
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                mlp.leftMargin = insets.left;
                mlp.rightMargin = insets.right;
                mlp.topMargin = insets.top;
                mlp.bottomMargin = insets.bottom;
                v.setLayoutParams(mlp);

                // Return CONSUMED if you don't want the window insets to keep passing
                // down to descendant views.
                return WindowInsetsCompat.CONSUMED;
            });
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
        Log.d(LOG_TAG, "Opened SettingsActivity");
    }
}