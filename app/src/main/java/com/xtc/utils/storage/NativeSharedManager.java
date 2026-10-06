package com.xtc.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;

/** Wraps {@link SharedPreferences} with per-file caching and commit semantics. */
public class NativeSharedManager {

    private static String currentName;
    private static SharedPreferences currentPreferences;
    private static volatile NativeSharedManager instance;
    private static final HashMap<String, SharedPreferences> CACHE = new HashMap<>(5);

    private NativeSharedManager() {
    }

    /** Returns the singleton bound to the named preferences file. */
    public static NativeSharedManager getInstance(Context context, String name) {
        NativeSharedManager manager = instance;
        if (manager == null) {
            synchronized (NativeSharedManager.class) {
                manager = instance;
                if (manager == null) {
                    manager = new NativeSharedManager();
                    instance = manager;
                }
            }
        }
        bind(context, name);
        return manager;
    }

    /** Binds the shared instance to the given preferences file. */
    private static void bind(Context context, String name) {
        if (TextUtils.isEmpty(currentName) || !currentName.equals(name)) {
            SharedPreferences cached = CACHE.get(name);
            if (cached != null) {
                currentPreferences = cached;
                currentName = name;
            } else {
                SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name, 0);
                CACHE.put(name, preferences);
                currentPreferences = preferences;
                currentName = name;
            }
        }
    }

    /** The currently bound preferences. */
    public SharedPreferences getPreferences() {
        return currentPreferences;
    }

    private SharedPreferences.Editor edit() {
        return currentPreferences.edit();
    }

    public boolean contains(String key) {
        return currentPreferences.contains(key);
    }

    public boolean putInt(String key, int value) {
        return edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defaultValue) {
        return currentPreferences.getInt(key, defaultValue);
    }

    public boolean putLong(String key, long value) {
        return edit().putLong(key, value).commit();
    }

    public long getLong(String key, long defaultValue) {
        return currentPreferences.getLong(key, defaultValue);
    }

    public boolean putString(String key, String value) {
        return edit().putString(key, value).commit();
    }

    public String getString(String key, String defaultValue) {
        return currentPreferences.getString(key, defaultValue);
    }

    public boolean putBoolean(String key, boolean value) {
        return edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return currentPreferences.getBoolean(key, defaultValue);
    }

    public boolean putFloat(String key, float value) {
        return edit().putFloat(key, value).commit();
    }

    public float getFloat(String key, float defaultValue) {
        return currentPreferences.getFloat(key, defaultValue);
    }

    public boolean remove(String key) {
        return currentPreferences.edit().remove(key).commit();
    }

    public boolean clear() {
        return currentPreferences.edit().clear().commit();
    }
}