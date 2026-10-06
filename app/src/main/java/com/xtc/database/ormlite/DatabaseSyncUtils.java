package com.xtc.database.ormlite;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xtc.log.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Copies the rows of a set of tables from one database file into the current one. */
public class DatabaseSyncUtils {

    private static final String TAG = "DatabaseSyncUtils";

    private DatabaseSyncUtils() {
    }

    /** @return the schema of every table of [tableNames] present in [database]. */
    public static Map<String, List<ColumnStruct>> getTablesStruct(SQLiteDatabase database, String... tableNames) {
        List<String> requestedTables = Arrays.asList(tableNames);
        Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
        HashMap<String, List<ColumnStruct>> result = new HashMap<>();
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(0);
            if (requestedTables.contains(tableName)) {
                List<ColumnStruct> columns = DatabaseUtil.getTableColumns(database, tableName);
                LogUtil.i(TAG, "getTablesStruct: " + tableName + ";tableStruct = " + columns);
                result.put(tableName, columns);
            } else {
                LogUtil.d(TAG, "getTablesStruct: " + tableName + " is not in config table!");
            }
        }
        cursor.close();
        return result;
    }

    /**
     * Attaches the database file [databasePath] as [alias] and copies the rows of
     * [tableNames] from it into the current database.
     */
    public static boolean copyTables(SQLiteDatabase database, Map<String, List<ColumnStruct>> oldStructs,
            String databasePath, String alias, String... tableNames) {
        if (database == null || TextUtils.isEmpty(databasePath) || TextUtils.isEmpty(alias) || tableNames == null) {
            return false;
        }
        if (tableNames.length < 1) {
            return false;
        }
        List<String> requestedTables = Arrays.asList(tableNames);
        ArrayList<String> newTables = new ArrayList<>();
        Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(0);
            LogUtil.i(TAG, "新数据库表：" + tableName);
            newTables.add(tableName);
        }
        cursor.close();
        if (!newTables.containsAll(requestedTables)) {
            LogUtil.w(TAG, "存在新旧表不一致！");
            return false;
        }
        try {
            database.execSQL(String.format("ATTACH DATABASE '%s' AS %s", databasePath, alias));
            database.beginTransaction();
            for (String tableName : tableNames) {
                try {
                    List<ColumnStruct> oldColumns = oldStructs.get(tableName);
                    List<ColumnStruct> newColumns = DatabaseUtil.getTableColumns(database, tableName);
                    LogUtil.d(TAG, "dbTableCopy: oldStruct = " + oldColumns);
                    LogUtil.d(TAG, "dbTableCopy: newStruct = " + newColumns);
                    if (DatabaseUtil.hasColumnChanged(oldColumns, newColumns)) {
                        LogUtil.d(TAG, "数据表已有字段的描述改变");
                    } else {
                        List<String> oldNames = DatabaseUtil.getColumnNames(oldColumns);
                        List<String> newNames = DatabaseUtil.getColumnNames(newColumns);
                        if (!oldNames.isEmpty() && !oldNames.equals(newNames)) {
                            LogUtil.d(TAG, "表发生了变化 tableName =" + tableName + ",oldColumns = " + oldNames
                                    + ",newColumns =" + newNames);
                            String sharedColumns = DatabaseUtil.buildSharedColumns(oldNames,
                                    DatabaseUtil.getAddedColumns(oldNames, newNames));
                            LogUtil.i(TAG, "dbTableCopy: table " + tableName + " insert count:"
                                    + database.compileStatement(String.format(
                                            "INSERT INTO %s (%s) SELECT %s FROM  %s", tableName, sharedColumns,
                                            sharedColumns, alias + "." + tableName)).executeInsert()
                                    + ";copyColumns = " + sharedColumns);
                        } else {
                            LogUtil.i(TAG, "表没有发生变化");
                            LogUtil.i(TAG, "dbTableCopy: table " + tableName + " insert count:"
                                    + database.compileStatement(String.format("INSERT INTO %s SELECT * FROM  %s",
                                            tableName, alias + "." + tableName)).executeInsert());
                        }
                    }
                } catch (SQLException e) {
                    LogUtil.e(TAG, "dbTableCopy: ", e);
                }
            }
            database.setTransactionSuccessful();
            database.endTransaction();
            database.execSQL(String.format("DETACH DATABASE %s", alias));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}