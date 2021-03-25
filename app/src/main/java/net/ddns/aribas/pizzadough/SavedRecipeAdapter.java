package net.ddns.aribas.pizzadough;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.SavedRecipeViewHolder> {
    private static final String LOG_TAG = SavedRecipeAdapter.class.getSimpleName();

    private final ArrayList<SavedRecipeItem> mSavedRecipeList;
    private OnItemClickListener mClickListener;
    private String mDeleteMenuTitle;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }
    public void setDeleteMenuTitle(String title){
        mDeleteMenuTitle = title;
    }

    public static class SavedRecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView mSavedRecipeName;
        public TextView mSavedRecipeDate;

        public SavedRecipeViewHolder(View itemView,
                                     final OnItemClickListener clickListener,
                                     final String title) {
            super(itemView);

            mSavedRecipeName = itemView.findViewById(R.id.text_view_title);
            mSavedRecipeDate = itemView.findViewById(R.id.text_view_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Log.i(LOG_TAG, "onClick");
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuItem delete = menu.add(Menu.NONE, 1, 1, title);

                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (clickListener != null){
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    Log.i(LOG_TAG, "onMenuItemClick");
                                    if(item.getItemId() == 1) {
                                        clickListener.onDeleteClick(position);
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    });
                }
            });
        }
    }

    public SavedRecipeAdapter(ArrayList<SavedRecipeItem> savedRecipeList) {
        mSavedRecipeList = savedRecipeList;
    }

    @NonNull
    @Override
    public SavedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_recipe_item, parent, false);
        return new SavedRecipeViewHolder(v, mClickListener, mDeleteMenuTitle);
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

}
