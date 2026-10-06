package com.xtc.database.ormlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.misc.JavaxPersistenceConfigurer;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.xtc.log.LogUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Introspects and compares the ormlite table schemas. */
public class DatabaseUtil {

    private static final String TAG = "DatabaseUtil";

    private DatabaseUtil() {
    }

    /** @return the table name declared by the {@link DatabaseTable} annotation. */
    public static <T> String getTableName(Class<T> clazz) {
        DatabaseTable table = clazz.getAnnotation(DatabaseTable.class);
        if (table != null && table.tableName() != null && table.tableName().length() > 0) {
            return table.tableName();
        }
        String entityName = new com.j256.ormlite.misc.JavaxPersistenceImpl().getEntityName(clazz);
        return entityName == null ? clazz.getSimpleName().toLowerCase() : entityName;
    }

    /** @return the column names of [columns]. */
    public static List<String> getColumnNames(List<ColumnStruct> columns) {
        ArrayList<String> names = new ArrayList<>();
        if (columns == null) {
            return names;
        }
        Iterator<ColumnStruct> iterator = columns.iterator();
        while (iterator.hasNext()) {
            names.add(iterator.next().getColumnName());
        }
        return names;
    }

    /** @return the columns declared by the ormlite entity [clazz]. */
    public static <T> List<ColumnStruct> getEntityColumns(ConnectionSource connectionSource, Class<T> clazz) {
        ArrayList<ColumnStruct> columns = new ArrayList<>();
        try {
            return parseCreateTableSql(TableUtils.getCreateTableStatements(connectionSource, clazz).get(0));
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return columns;
        }
    }

    /** @return the columns of the existing table [tableName]. */
    public static List<ColumnStruct> getTableColumns(SQLiteDatabase database, String tableName) {
        List<ColumnStruct> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("select * from sqlite_master where type = ? AND name = ?",
                    new String[]{"table", tableName});
            if (cursor != null) {
                cursor.moveToFirst();
                int sqlIndex = cursor.getColumnIndex("sql");
                if (-1 != sqlIndex && cursor.getCount() > 0) {
                    columns = parseCreateTableSql(cursor.getString(sqlIndex));
                } else {
                    LogUtil.i(TAG, "不存在数据表：" + tableName);
                }
            } else {
                LogUtil.i(TAG, "数据库操作失败");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }

    /** @return the comma separated list of the columns present in both lists. */
    public static String buildSharedColumns(List<String> oldColumns, List<String> newColumns) {
        StringBuilder builder = new StringBuilder("");
        if (oldColumns == null || newColumns == null) {
            return builder.toString();
        }
        int index = 0;
        for (String column : oldColumns) {
            if (!CollectionUtil.contains(column, newColumns)) {
                if (index > 0) {
                    builder.append(", ");
                }
                builder.append("`");
                builder.append(column);
                builder.append("`");
                index++;
            }
        }
        return builder.toString();
    }

    /** Parses the columns out of a {@code CREATE TABLE} statement. */
    public static List<ColumnStruct> parseCreateTableSql(String createTableSql) {
        ArrayList<ColumnStruct> columns = new ArrayList<>();
        String body = createTableSql.substring(createTableSql.indexOf("(") + 1, createTableSql.length() - 1);
        for (String rawColumn : body.split(", ")) {
            if (rawColumn.contains("(") || rawColumn.contains(")")) {
                rawColumn = rawColumn.replace("(", "").replace(")", "");
            }
            String column = rawColumn.trim();
            if (column.startsWith("`")) {
                String[] parts = column.split("` ");
                columns.add(new ColumnStruct(parts[0].replace("`", ""), parts[1]));
            } else if (column.contains(",")) {
                for (String name : column.split(" `")[1].replace("`", "").split(",")) {
                    mergeColumn(columns, name, "UniqueCombo");
                }
            } else {
                String[] parts = column.split(" `");
                mergeColumn(columns, parts[1].replace("`", ""), parts[0]);
            }
        }
        return columns;
    }

    private static void mergeColumn(List<ColumnStruct> columns, String columnName, String columnLimit) {
        if (columns == null || columns.isEmpty()) {
            LogUtil.e(TAG, "list is null.");
            return;
        }
        if (TextUtils.isEmpty(columnName) || TextUtils.isEmpty(columnLimit)) {
            LogUtil.e(TAG, "columnName is null or limit is null.");
            return;
        }
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            ColumnStruct column = columns.get(i);
            if (columnName.equals(column.getColumnName())) {
                column.setColumnLimit(column.getColumnLimit() + " " + columnLimit);
                return;
            }
        }
        columns.add(new ColumnStruct(columnName, columnLimit));
    }

    /** @return true when any column of [oldColumns] changed its definition. */
    public static boolean hasColumnChanged(List<ColumnStruct> oldColumns, List<ColumnStruct> newColumns) {
        if (oldColumns != null && newColumns != null) {
            Iterator<ColumnStruct> iterator = oldColumns.iterator();
            while (iterator.hasNext()) {
                if (containsChanged(newColumns, iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsChanged(List<ColumnStruct> columns, ColumnStruct column) {
        if (column == null || TextUtils.isEmpty(column.getColumnName())) {
            return false;
        }
        Iterator<ColumnStruct> iterator = columns.iterator();
        while (iterator.hasNext()) {
            if (isChanged(column, iterator.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChanged(ColumnStruct oldColumn, ColumnStruct newColumn) {
        if (oldColumn == null || newColumn == null) {
            return false;
        }
        String oldName = oldColumn.getColumnName();
        String oldLimit = oldColumn.getColumnLimit();
        if (oldName == null || !oldName.equals(newColumn.getColumnName())) {
            return false;
        }
        String newLimit = newColumn.getColumnLimit();
        if (oldLimit == null && newLimit == null) {
            return false;
        }
        return oldLimit == null || newLimit == null || !oldLimit.equals(newLimit);
    }

    /** @return the columns of [newColumns] that are not present in [oldColumns]. */
    public static List<String> getAddedColumns(List<String> oldColumns, List<String> newColumns) {
        return subtract(newColumns, oldColumns);
    }

    private static List<String> subtract(List<String> first, List<String> second) {
        ArrayList<String> result = new ArrayList<>();
        if (first != null && second != null) {
            for (String value : first) {
                boolean found = false;
                for (String other : second) {
                    if (other != null && other.equals(value)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    /** @return the column named [columnName], or null. */
    public static ColumnStruct findColumn(String columnName, List<ColumnStruct> columns) {
        if (columns != null && columnName != null) {
            for (ColumnStruct column : columns) {
                if (column != null && columnName.equals(column.getColumnName())) {
                    return column;
                }
            }
        }
        return null;
    }
}