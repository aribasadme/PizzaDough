package com.aribasadmetlla.pizzadough;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class ViewSavedRecipeActivity extends AppCompatActivity {
    private static final String LOG_TAG = ViewSavedRecipeActivity.class.getSimpleName();
    private TextView mTexRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display for SDK >= 29 and <= 35
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_view_saved_recipe);

        // Set up the toolbar and add the back button.
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTexRecipe = findViewById(R.id.loadedRecipe);

        Bundle bundle = getIntent().getExtras();
        String fileName = bundle.getString("FILE_NAME");

        loadFile(fileName);
    }

    private void loadFile(String fileName) {
        try (FileInputStream fis = getApplicationContext().openFileInput(fileName)) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            mTexRecipe.setText(sb.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading file: " + e.getMessage());
        }
    }
}