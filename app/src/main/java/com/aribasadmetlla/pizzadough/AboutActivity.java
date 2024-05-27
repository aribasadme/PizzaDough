package com.aribasadmetlla.pizzadough;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {
    private TextView mAbout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String appVersionName = BuildConfig.VERSION_NAME;
        String versionText = getString(R.string.version_s).concat(": ");

        TextView versionTextView = findViewById(R.id.aboutText);
        versionTextView.setText(versionText.concat(appVersionName));
    }
}
