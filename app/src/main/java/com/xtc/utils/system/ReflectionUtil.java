package com.xtc.utils.system;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.InputStream;

/** Cross-package resource and database access helpers. */
public class ReflectionUtil {

    /** Callback receiving the cursor of a raw query. */
    public interface CursorData {
        void onCursor(Cursor cursor);
    }

    private ReflectionUtil() {
    }

    /** Creates a context for another installed package. */
    public static Context createPackageContext(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
    }

    /** Opens the database file in read/write mode, or null when missing. */
    private static SQLiteDatabase openDatabase(String path) {
        if (new File(path).exists()) {
            return SQLiteDatabase.openDatabase(path, null, 0);
        }
        return null;
    }

    /** Executes a raw SQL statement against another app's database. */
    public static void execSql(String path, String sql) {
        SQLiteDatabase database = openDatabase(path);
        if (database != null) {
            database.execSQL(sql);
            database.close();
        }
    }

    /** Runs a raw query against another app's database. */
    public static void rawQuery(String path, String sql, CursorData cursorData) {
        SQLiteDatabase database = openDatabase(path);
        if (database != null) {
            cursorData.onCursor(database.rawQuery(sql, null));
            database.close();
        }
    }

    /** Resources of another installed package. */
    public static Resources getResources(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return createPackageContext(context, packageName).getResources();
    }

    /** Resolves a resource identifier in another package. */
    public static int getIdentifier(Context context, String packageName, String name, String type) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).getIdentifier(name, type, packageName);
    }

    /** Reads a colour resource from another package. */
    public static int getColor(Context context, String packageName, String name) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).getColor(getIdentifier(context, packageName, name, "color"));
    }

    /** Reads a dimension resource from another package. */
    public static float getDimension(Context context, String packageName, String name) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).getDimension(getIdentifier(context, packageName, name, "dimen"));
    }

    /** Reads a drawable resource from another package. */
    public static Drawable getDrawable(Context context, String packageName, String name) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).getDrawable(getIdentifier(context, packageName, name, "drawable"));
    }

    /** Reads a string resource from another package. */
    public static String getString(Context context, String packageName, String name) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).getString(getIdentifier(context, packageName, name, "string"));
    }

    /** Reads a string-array resource from another package. */
    public static String[] getStringArray(Context context, String packageName, String name) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).getStringArray(getIdentifier(context, packageName, name, "array"));
    }

    /** Opens a raw resource of another package. */
    public static InputStream openRawResource(Context context, String packageName, String name) throws PackageManager.NameNotFoundException {
        return getResources(context, packageName).openRawResource(getIdentifier(context, packageName, name, "raw"));
    }
}