package com.aribasadmetlla.pizzadough;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;

    public Preferences(Context context) {
        sharedPref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.apply();
    }

    public int getInt(String key, int def) {
        return sharedPref.getInt(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPref.getBoolean(key, def);
    }

    public String getString(String key, String def) {
        return sharedPref.getString(key, def);
    }

    public int getRemoveAd() {
        //int defaultValue = getResources().getInteger("RemoveAd");
        return sharedPref.getInt("RemoveAd", 0);
    }

    public void setRemoveAd(int value) {
        editor.putInt("RemoveAd", value);
        editor.apply();
    }
}
