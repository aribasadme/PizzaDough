package com.aribasadmetlla.pizzadough;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;


public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for SDK >= 29 and <= 35
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_about);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar); // Use the ID from the include tag
        setSupportActionBar(toolbar);

        // Handles overlap between the navigation bar and the status bar
        View rootView = findViewById(R.id.about_activity_root);
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
