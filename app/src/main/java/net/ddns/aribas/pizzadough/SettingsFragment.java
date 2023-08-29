package net.ddns.aribas.pizzadough;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference savedRecipesPref = findPreference("saved_recipes");
        assert savedRecipesPref != null;
        savedRecipesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent savedRecipesListIntent = new Intent(getActivity(),ListSavedRecipes.class);
                startActivity(savedRecipesListIntent);
                return true;
            }
        });
    }
}