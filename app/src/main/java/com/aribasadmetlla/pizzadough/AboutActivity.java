package com.aribasadmetlla.pizzadough;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for SDK >= 29 and <= 35
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_about);

        int versionMajor = BuildConfig.VERSION_MAJOR;
        int versionMinor = BuildConfig.VERSION_MINOR;
        int versionPatch = BuildConfig.VERSION_PATCH;
        @SuppressLint("DefaultLocale") String appVersionName = String.format("%d.%d.%d", versionMajor, versionMinor, versionPatch);
        String versionText = getString(R.string.version_s).concat(": ");

        TextView versionTextView = findViewById(R.id.aboutText);
        versionTextView.setText(versionText.concat(appVersionName));
    }
}
