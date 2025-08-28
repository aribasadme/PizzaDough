package com.aribasadmetlla.pizzadough;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.SavedRecipeViewHolder> {
    private static final String LOG_TAG = SavedRecipeAdapter.class.getSimpleName();
    private static String[] mMenuTitles;
    private static OnItemClickListener mClickListener;
    private final ArrayList<SavedRecipeItem> mSavedRecipeList;

    public SavedRecipeAdapter(ArrayList<SavedRecipeItem> savedRecipeList) {
        mSavedRecipeList = savedRecipeList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setMenuTitles(String[] titles) {
        mMenuTitles = titles;
    }

    @NonNull
    @Override
    public SavedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_recipe_item, parent, false);
        return new SavedRecipeViewHolder(v, mMenuTitles);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRecipeViewHolder holder, int position) {
        SavedRecipeItem currentItem = mSavedRecipeList.get(position);
        String fileName = currentItem.getFileName();
        holder.mSavedRecipeName.setText(fileName.substring(0, fileName.lastIndexOf(".txt")));
        holder.mSavedRecipeDate.setText(currentItem.getDateSaved());
    }

    @Override
    public int getItemCount() {
        int listSize = mSavedRecipeList.size();
        Log.d(LOG_TAG, "Got " + listSize + " items");
        return listSize;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onSendClick(int position);

        void onDeleteClick(int position);
    }

    public static class SavedRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView mSavedRecipeName;
        public TextView mSavedRecipeDate;

        public SavedRecipeViewHolder(View itemView,
                                     final String[] titles) {
            super(itemView);

            mSavedRecipeName = itemView.findViewById(R.id.text_view_title);
            mSavedRecipeDate = itemView.findViewById(R.id.text_view_date);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Log.d(LOG_TAG, "onClick");
                    mClickListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem send = contextMenu.add(Menu.NONE, 1, 1, mMenuTitles[0]);
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, mMenuTitles[1]);

            send.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Log.d(LOG_TAG, "onMenuItemClick");
                    switch (menuItem.getItemId()) {
                        case 1:
                            // Send
                            mClickListener.onSendClick(position);
                            Log.d(LOG_TAG, "onSendClick");
                            return true;
                        case 2:
                            // Delete
                            mClickListener.onDeleteClick(position);
                            Log.d(LOG_TAG, "onDeleteClick");
                            return true;
                    }
                }
            }
            return false;
        }
    }

}
