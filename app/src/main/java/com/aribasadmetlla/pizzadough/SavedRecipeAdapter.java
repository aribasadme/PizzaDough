package com.aribasadmetlla.pizzadough;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.SavedRecipeViewHolder> {
    private static final String LOG_TAG = SavedRecipeAdapter.class.getSimpleName();
    private static OnItemClickListener mClickListener;
    private static OnItemLongClickListener mLongClickListener;
    private final ArrayList<SavedRecipeItem> mSavedRecipeList;

    public SavedRecipeAdapter(ArrayList<SavedRecipeItem> savedRecipeList) {
        mSavedRecipeList = savedRecipeList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    @NonNull
    @Override
    public SavedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_recipe_item, parent, false);
        return new SavedRecipeViewHolder(v);
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
        return mSavedRecipeList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public static class SavedRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mSavedRecipeName;
        public TextView mSavedRecipeDate;

        public SavedRecipeViewHolder(View itemView) {
            super(itemView);

            mSavedRecipeName = itemView.findViewById(R.id.text_view_title);
            mSavedRecipeDate = itemView.findViewById(R.id.text_view_date);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Log.d(LOG_TAG, "onLongClick");
                    mLongClickListener.onItemLongClick(position);
                    return true;
                }
            }
            return false;
        }
    }
}
