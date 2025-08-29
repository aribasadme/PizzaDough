package com.aribasadmetlla.pizzadough;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference savedRecipesPref = findPreference("saved_recipes");
        assert savedRecipesPref != null;
        savedRecipesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Log.d(LOG_TAG, "Clicked on saved recipes");
                Intent savedRecipesListIntent = new Intent(getActivity(), ListSavedRecipesActivity.class);
                startActivity(savedRecipesListIntent);
                return true;
            }
        });
    }
}