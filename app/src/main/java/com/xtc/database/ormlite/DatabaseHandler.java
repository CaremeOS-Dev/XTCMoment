package com.xtc.database.ormlite;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xtc.log.LogUtil;

import java.sql.SQLException;
import java.util.List;

/** Creates, upgrades and downgrades the table of a single entity. */
public class DatabaseHandler<T> {

    private static final String TAG = "DatabaseHandler";

    private final Class<T> entityClass;
    private final String tableName;

    public DatabaseHandler(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.tableName = DatabaseUtil.getTableName(entityClass);
    }

    public String getTableName() {
        return this.tableName;
    }

    /** Creates or migrates the table so it matches the entity definition. */
    protected void upgradeTable(SQLiteDatabase database, ConnectionSource connectionSource) throws SQLException {
        List<ColumnStruct> oldColumns = DatabaseUtil.getTableColumns(database, this.tableName);
        List<ColumnStruct> newColumns = DatabaseUtil.getEntityColumns(connectionSource, this.entityClass);
        if (oldColumns.isEmpty() && newColumns.isEmpty()) {
            LogUtil.d(TAG, "数据表结构都为空！不是合法的数据库bean！！！");
            return;
        }
        if (oldColumns.isEmpty()) {
            LogUtil.d(TAG, "新增表");
            createTable(connectionSource);
        } else if (newColumns.isEmpty()) {
            LogUtil.e(TAG, "删除表");
            dropTable(connectionSource);
        } else {
            upgradeColumns(database, connectionSource, oldColumns, newColumns);
        }
    }

    private void upgradeColumns(SQLiteDatabase database, ConnectionSource connectionSource,
            List<ColumnStruct> oldColumns, List<ColumnStruct> newColumns) throws SQLException {
        if (DatabaseUtil.hasColumnChanged(oldColumns, newColumns)) {
            LogUtil.d(TAG, "数据表已有字段的描述改变");
            recreateTable(connectionSource);
            return;
        }
        List<String> oldNames = DatabaseUtil.getColumnNames(oldColumns);
        List<String> newNames = DatabaseUtil.getColumnNames(newColumns);
        if (!oldNames.equals(newNames)) {
            LogUtil.d(TAG, "表发生了变化 tableName =" + this.tableName + ",oldColumns = " + oldNames
                    + ",newColumns =" + newNames);
            upgradeByCopy(database, connectionSource,
                    DatabaseUtil.buildSharedColumns(oldNames, DatabaseUtil.getAddedColumns(oldNames, newNames)));
            return;
        }
        LogUtil.i(TAG, "表没有发生变化,不需要更新数据表");
    }

    private void upgradeByCopy(SQLiteDatabase database, ConnectionSource connectionSource, String columns)
            throws SQLException {
        if (TextUtils.isEmpty(columns)) {
            LogUtil.d(TAG, "upgradeByCopy columns is null");
            return;
        }
        database.beginTransaction();
        String tempTableName = this.tableName + "_temp";
        try {
            database.execSQL("ALTER TABLE " + this.tableName + " RENAME TO " + tempTableName);
            try {
                database.execSQL(TableUtils.getCreateTableStatements(connectionSource, this.entityClass).get(0));
            } catch (Exception e) {
                LogUtil.e(TAG, e);
                TableUtils.createTable(connectionSource, this.entityClass);
            }
            database.execSQL("INSERT INTO " + this.tableName + " (" + columns + ")  SELECT " + columns
                    + " FROM " + tempTableName);
            database.execSQL("DROP TABLE IF EXISTS " + tempTableName);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            throw new SQLException("upgrade database table struct fail");
        } finally {
            database.endTransaction();
        }
    }

    /** Called on database upgrade; recreates the table when the migration fails. */
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
            throws SQLException {
        try {
            upgradeTable(database, connectionSource);
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            recreateTable(connectionSource);
        }
    }

    /** Called on database downgrade; always recreates the table. */
    public void downgradeTable(ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        recreateTable(connectionSource);
    }

    private void recreateTable(ConnectionSource connectionSource) throws SQLException {
        dropTable(connectionSource);
        createTable(connectionSource);
    }

    /** Deletes every row of the table. */
    public void clearTable(ConnectionSource connectionSource) throws SQLException {
        TableUtils.clearTable(connectionSource, this.entityClass);
    }

    /** Creates the table. */
    public void createTable(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTable(connectionSource, this.entityClass);
    }

    /** Drops the table when it exists. */
    public void dropTable(ConnectionSource connectionSource) throws SQLException {
        TableUtils.dropTable(connectionSource, this.entityClass, true);
    }
}