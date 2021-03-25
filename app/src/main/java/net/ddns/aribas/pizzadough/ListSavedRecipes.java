package net.ddns.aribas.pizzadough;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ListSavedRecipes extends AppCompatActivity {
    private static final String LOG_TAG = ListSavedRecipes.class.getSimpleName();

    private ArrayList<SavedRecipeItem> mSavedRecipeList;
    private RecyclerView mRecyclerView;
    private SavedRecipeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_saved_recipes);

        createItems();
        buildRecyclerView();
    }

    public void listFiles(Context context, ArrayList<File> fl) {
        for (String fn : context.fileList()) {
            if (fn.contains(".txt")) {
                Log.i(LOG_TAG, fn);
                fl.add(new File(this.getFilesDir(), fn));
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

    public void callViewRecipe (int position)  {
        Bundle bundle = new Bundle();
        bundle.putString("FILE_NAME", mSavedRecipeList.get(position).getFileName());
        Intent viewRecipeIntent = new Intent(this, ViewSavedRecipe.class);
        viewRecipeIntent.putExtras(bundle);
        startActivity(viewRecipeIntent);
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SavedRecipeAdapter(mSavedRecipeList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setDeleteMenuTitle(getString(R.string.delete));
        mAdapter.setOnItemClickListener(new SavedRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                callViewRecipe(position);
            }

            @Override
            public void onDeleteClick(int position) {
               deleteFile(position);
            }
        });
    }

    public void deleteFile(int position) {
        File file = new File(getFilesDir(),  mSavedRecipeList.get(position).getFileName());
        if (deleteFile(file.getName())) {
            mSavedRecipeList.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
    }
}