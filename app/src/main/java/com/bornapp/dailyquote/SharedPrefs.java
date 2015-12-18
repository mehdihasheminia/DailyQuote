package com.bornapp.dailyquote;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPrefs {
    // Writes preference item to the SharedPreferences file for this widget
    //Like : SavePrefItem(context, mAppWidgetId, "UserName", Value1);
    public static void SavePrefItem(Context context, String key, String value) {
        String prefName = context.getString(R.string.txt_pref_name);
        String prefprefix = context.getString(R.string.txt_pref_prefix);
        SharedPreferences.Editor prefs = context.getSharedPreferences(prefName, 0).edit();
        prefs.putString(prefprefix + key, value);
        prefs.commit();
    }

    public static void SavePrefItem(Context context, String key, int value) {
        String prefName = context.getString(R.string.txt_pref_name);
        String prefprefix = context.getString(R.string.txt_pref_prefix);
        SharedPreferences.Editor prefs = context.getSharedPreferences(prefName, 0).edit();
        prefs.putInt(prefprefix + key, value);
        prefs.commit();
    }

    // Read preference item from the SharedPreferences file for this widget.
    // If there is no preference saved, returns a default from a resource
    public static String LoadPref_String(Context context, String key) {
        String prefName = context.getString(R.string.txt_pref_name);
        String prefprefix = context.getString(R.string.txt_pref_prefix);
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        return prefs.getString(prefprefix + key, "");
    }

    public static int LoadPref_Int(Context context, String key) {
        String prefName = context.getString(R.string.txt_pref_name);
        String prefprefix = context.getString(R.string.txt_pref_prefix);
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        return prefs.getInt(prefprefix + key, -1);
    }
}