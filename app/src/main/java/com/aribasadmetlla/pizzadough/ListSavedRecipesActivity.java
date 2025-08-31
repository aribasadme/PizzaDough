package com.aribasadmetlla.pizzadough;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class ListSavedRecipesActivity extends AppCompatActivity {
    private static final String LOG_TAG = ListSavedRecipesActivity.class.getSimpleName();

    private ArrayList<SavedRecipeItem> mSavedRecipeList;
    private SavedRecipeAdapter mAdapter;
    private String[] mMenuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for SDK >= 29 and <= 35
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_list_saved_recipes);

        createItems();
        buildRecyclerView();
    }

    public void listFiles(Context context, ArrayList<File> fileList) {
        if (context.fileList().length == 0) {
            Log.e(LOG_TAG, "No files found");
        } else {
            Log.i(LOG_TAG, "Found files:");
            for (String fileName : context.fileList()) {
                if (fileName.contains(".txt")) {
                    Log.i(LOG_TAG, "- " + fileName);
                    fileList.add(new File(this.getFilesDir(), fileName));
                }
            }
        }
    }

    public void createItems() {
        mSavedRecipeList = new ArrayList<SavedRecipeItem>();
        ArrayList<File> fileList = new ArrayList<File>();

        listFiles(this, fileList);
        for (File f : fileList) {
            Calendar dateLastModified = Calendar.getInstance();
            dateLastModified.setTimeInMillis(f.lastModified());
            mSavedRecipeList.add(new SavedRecipeItem(f.getName(), dateLastModified));
        }
    }

    public void buildRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SavedRecipeAdapter(mSavedRecipeList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        createMenuTitles();
        mAdapter.setMenuTitles(mMenuTitles);
        mAdapter.setOnItemClickListener(new SavedRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                callViewRecipe(position);
            }

            @Override
            public void onSendClick(int position) {
                sendFile(position);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteFile(position);
            }
        });
    }

    public void callViewRecipe(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("FILE_NAME", mSavedRecipeList.get(position).getFileName());
        Intent viewRecipeIntent = new Intent(this, ViewSavedRecipeActivity.class);
        viewRecipeIntent.putExtras(bundle);
        startActivity(viewRecipeIntent);
    }

    public void sendFile(int position) {
        Intent sendIntent = new Intent();
        FileInputStream fis = null;
        try {
            fis = getApplicationContext().openFileInput(mSavedRecipeList.get(position).getFileName());
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, null));
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

    public void deleteFile(int position) {
        File file = new File(getFilesDir(), mSavedRecipeList.get(position).getFileName());
        if (deleteFile(file.getName())) {
            mSavedRecipeList.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
    }

    public void createMenuTitles() {
        mMenuTitles = new String[2];
        mMenuTitles[0] = getString(R.string.send);
        mMenuTitles[1] = getString(R.string.delete);
    }
}