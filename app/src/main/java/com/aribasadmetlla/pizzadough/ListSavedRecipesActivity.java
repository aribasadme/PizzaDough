package com.aribasadmetlla.pizzadough;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
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
    private int mSelectedRecipePosition = RecyclerView.NO_POSITION; // Variable to store the position

    ActionMode mActionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_saved_recipes);

        createItems();
        buildRecyclerView();
    }

    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Use the stored position
            if (mSelectedRecipePosition == RecyclerView.NO_POSITION) {
                // Should not happen if an item was selected, but good for safety
                Log.e(LOG_TAG, "No position selected for action mode item click");
                mode.finish();
                return false;
            }

            switch (item.getItemId()) {
                case R.id.action_send_recipe:
                    sendRecipeFile(mSelectedRecipePosition);
                    mode.finish();
                    return true;
                case R.id.action_delete_recipe:
                    deleteRecipeFile(mSelectedRecipePosition);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null; // Clear the action mode
            mSelectedRecipePosition = RecyclerView.NO_POSITION; // Reset the position
        }
    };

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
        mSavedRecipeList = new ArrayList<>();
        ArrayList<File> fileList = new ArrayList<>();

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

        mAdapter.setOnItemClickListener(new SavedRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(LOG_TAG, "Click on item " + position + " (" + mSavedRecipeList.get(position).getFileName() + ")");
                callViewRecipe(position);
            }
        });
        mAdapter.setOnItemLongClickListener(new SavedRecipeAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                Log.i(LOG_TAG, "Long click on item " + position + " (" + mSavedRecipeList.get(position).getFileName() + ")");

                if (mActionMode != null) {
                    return;
                }
                mSelectedRecipePosition = position; // Store the position
                mActionMode = startSupportActionMode(mActionModeCallback);
                mActionMode.setTitle(getString(R.string.contextual_one_item_selected));
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

    public void sendRecipeFile(int position) {
        Intent sendIntent = new Intent();
        try (FileInputStream fis = getApplicationContext().openFileInput(mSavedRecipeList.get(position).getFileName())) {
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
        }
    }

    public void deleteRecipeFile(int position) {
        File file = new File(getFilesDir(), mSavedRecipeList.get(position).getFileName());
        String fileName = file.getName();
        if (deleteFile(file.getName())) {
            Log.i(LOG_TAG, "File " + fileName + " deleted");
            mSavedRecipeList.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
    }
}
