package com.popular.android.mibanco.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.popular.android.mibanco.App;

public final class PreferencesUtils {

    private PreferencesUtils() {
    }

    public static String getPrefsString(final String key, final String defaultValue) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        return preferences.getString(key, defaultValue);
    }

    public static boolean putPrefsString(final String key, final String value) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static void putPrefsStringAsync(final String key, final String value) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void removePrefsStringAsync(final String key) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static boolean removePrefsString(final String key) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static boolean getPrefsBoolean(final String key, final boolean defaultValue) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        return preferences.getBoolean(key, defaultValue);
    }

    public static boolean putPrefsBoolean(final String key, final boolean value) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static long getPrefsLong(final String key, final long defaultValue) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        return preferences.getLong(key, defaultValue);
    }

    public static boolean putPrefsLong(final String key, final long value) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static int getPrefsInt(final String key, final int defaultValue) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        return preferences.getInt(key, defaultValue);
    }

    public static boolean putPrefsInt(final String key, final int value) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance());
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static void clearPrefs() {
        PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance()).edit().clear().commit();
    }

    public static boolean hasPrefsKey(final String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getApplicationInstance()).contains(key);
    }
}
