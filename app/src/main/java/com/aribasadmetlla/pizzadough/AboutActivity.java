package com.aribasadmetlla.pizzadough;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        int versionMajor = BuildConfig.VERSION_MAJOR;
        int versionMinor = BuildConfig.VERSION_MINOR;
        int versionPatch = BuildConfig.VERSION_PATCH;
        int versionBuild = BuildConfig.VERSION_BUILD;
        String appVersionName = String.format("%d.%d.%d.%d", versionMajor, versionMinor, versionPatch, versionBuild);
        String versionText = getString(R.string.version_s).concat(": ");

        TextView versionTextView = findViewById(R.id.aboutText);
        versionTextView.setText(versionText.concat(appVersionName));
    }
}
