package net.ddns.aribas.pizzadough;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class ViewSavedRecipe extends AppCompatActivity {
    private TextView mTexRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_recipe);

        mTexRecipe = findViewById(R.id.loadedRecipe);

        Bundle bundle = getIntent().getExtras();
        String fileName = bundle.getString("FILE_NAME");

        loadFile(fileName);
    }

    private void loadFile(String fileName) {
        FileInputStream fis = null;
        try {
            fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            mTexRecipe.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}