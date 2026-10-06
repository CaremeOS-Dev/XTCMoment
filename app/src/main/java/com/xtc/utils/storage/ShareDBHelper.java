package com.xtc.utils.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xtc.log.LogUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Read/write access to a SQLite database owned by another app. */
public class ShareDBHelper {

    private static final String TAG = "ShareDBHelper";
    /** Date format used when binding {@link Date} columns. */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SQLiteDatabase database;

    /** Callback receiving the raw query cursor. */
    public interface ICursorCallBack {
        void callBack(Cursor cursor);
    }

    private ShareDBHelper(String path) {
        this.database = null;
        this.database = open(path);
    }

    private ShareDBHelper(String packageName, String dbName) {
        this.database = null;
        this.database = open(String.format("/data/data/%s/databases/%s", packageName, dbName));
    }

    private SQLiteDatabase open(String path) {
        if (!new File(path).exists()) {
            return null;
        }
        try {
            return SQLiteDatabase.openDatabase(path, null, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Opens the database at the given path. */
    public static synchronized ShareDBHelper getInstance(String path) {
        return new ShareDBHelper(path);
    }

    /** Opens a database of another package by name. */
    public static synchronized ShareDBHelper getInstance(String packageName, String dbName) {
        return new ShareDBHelper(packageName, dbName);
    }

    /** Executes the statement. */
    public boolean execSql(String sql) {
        return execute(sql);
    }

    /** Executes the statement. */
    public boolean execSqlCompat(String sql) {
        return execute(sql);
    }

    /** Executes the statement. */
    public boolean insert(String sql) {
        return execute(sql);
    }

    /** Executes the statement. */
    public boolean update(String sql) {
        return execute(sql);
    }

    /** Executes the statement and hands the cursor to the callback. */
    public void query(String sql, ICursorCallBack callBack) {
        SQLiteDatabase db = this.database;
        if (db != null) {
            Cursor cursor = db.rawQuery(sql, null);
            LogUtil.d(TAG, "getCursorData start");
            callBack.callBack(cursor);
            close();
            return;
        }
        LogUtil.e(TAG, "mDB is null!!!");
    }

    /** Runs a raw query, returning the cursor or null. */
    public Cursor rawQuery(String sql) {
        SQLiteDatabase db = this.database;
        if (db != null) {
            return db.rawQuery(sql, null);
        }
        return null;
    }

    /** Closes the database. */
    public void close() {
        SQLiteDatabase db = this.database;
        if (db != null) {
            db.close();
            this.database = null;
        }
    }

    /** Binds a cursor row onto {@code target} using its declared fields. */
    public <T> T bindRow(Cursor cursor, T target) {
        try {
            Class<?> clazz = target.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                int columnIndex = cursor.getColumnIndex(field.getName());
                if (columnIndex >= 0) {
                    Method setter = findSetter(clazz, field);
                    if (setter == null) {
                        continue;
                    }
                    String value = cursor.getString(columnIndex) + "";
                    if (cursor.isNull(columnIndex)) {
                        value = null;
                    }
                    Class<?> type = field.getType();
                    if (type == String.class) {
                        setter.invoke(target, value);
                    } else if (type == Integer.TYPE || type == Integer.class) {
                        setter.invoke(target, Integer.valueOf(value == null ? 0 : Integer.parseInt(value)));
                    } else if (type == Float.TYPE || type == Float.class) {
                        setter.invoke(target, Float.valueOf(value == null ? 0f : Float.parseFloat(value)));
                    } else if (type == Long.TYPE || type == Long.class) {
                        setter.invoke(target, Long.valueOf(value == null ? 0L : Long.parseLong(value)));
                    } else if (type == Date.class) {
                        setter.invoke(target, value == null ? null : parseDate(value));
                    } else {
                        setter.invoke(target, value);
                    }
                }
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** Formats a date using {@link #DATE_FORMAT}. */
    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        if (date != null) {
            return format.format(date);
        }
        return null;
    }

    /** Parses a date using {@link #DATE_FORMAT}, or null on failure. */
    private Date parseDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        if (value == null) {
            return null;
        }
        try {
            return format.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Finds a {@code getXxx} accessor matching the field name. */
    private Method findGetter(Class<?> clazz, Field field) {
        Locale locale = Locale.getDefault();
        try {
            Method[] methods = clazz.getDeclaredMethods();
            String fieldName = field.getName().toLowerCase(locale);
            for (Method method : methods) {
                String methodName = method.getName().toLowerCase(locale);
                if (!methodName.startsWith("set") && methodName.endsWith(fieldName) && methodName.startsWith("get")) {
                    return method;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Finds a {@code setXxx} mutator matching the field name. */
    private Method findSetter(Class<?> clazz, Field field) {
        Locale locale = Locale.getDefault();
        try {
            Method[] methods = clazz.getDeclaredMethods();
            String fieldName = field.getName().toLowerCase(locale);
            if ("serialversionuid".equals(fieldName)) {
                return null;
            }
            String setterName = "set" + fieldName;
            for (Method method : methods) {
                String methodName = method.getName().toLowerCase(locale);
                if (!methodName.startsWith("get") && methodName.equals(setterName)) {
                    return method;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Executes the statement inside a transaction. */
    private boolean execute(String sql) {
        boolean success = false;
        try {
            if (this.database != null) {
                try {
                    if ("".equals(sql) && sql == null) {
                        return false;
                    }
                    this.database.beginTransaction();
                    this.database.execSQL(sql);
                    this.database.setTransactionSuccessful();
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return success;
        } finally {
            this.database.endTransaction();
            this.database.close();
        }
    }
}