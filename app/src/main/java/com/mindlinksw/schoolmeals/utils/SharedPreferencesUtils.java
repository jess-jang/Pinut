package com.mindlinksw.schoolmeals.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtils {

    private static final String TAG = SharedPreferencesUtils.class.getName();

    public static void save(Context context, String key, Object value) {

        SharedPreferences pref = context.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (value.getClass().isAssignableFrom(String.class)) {
            editor.putString(key, (String) value);
        } else if (value.getClass().isAssignableFrom(Boolean.class)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value.getClass().isAssignableFrom(Integer.class)) {
            editor.putInt(key, (Integer) value);
        } else if (value.getClass().isAssignableFrom(Long.class)) {
            editor.putLong(key, (Long) value);
        } else if (value.getClass().isAssignableFrom(Float.class)) {
            editor.putFloat(key, (Float) value);
        }

        editor.apply();

    }

    public static String read(Context context, String key, String defValue) {
        SharedPreferences pref = context.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(key, defValue);
    }

    public static int read(Context context, String key, int defValue) {
        SharedPreferences pref = context.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getInt(key, defValue);
    }

    public static boolean read(Context context, String key, boolean defValue) {
        SharedPreferences pref = context.getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getBoolean(key, defValue);
    }

    public static void remove(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeAll(Context context) {
        SharedPreferences pref = context.getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
