package com.xtc.database.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.xtc.log.LogUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Ormlite helper that keeps a handler per registered entity and caches the daos. */
public class OrmLiteDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "OrmLiteDatabaseHelper";

    List<DatabaseHandler> databaseHandlers;
    Map<String, Dao> daoCache;

    public OrmLiteDatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory cursorFactory,
            int databaseVersion) {
        super(context, databaseName, cursorFactory, databaseVersion);
        this.daoCache = new HashMap<>();
    }

    /** Registers the entity [clazz] so its table is created/upgraded. */
    public <T> void registerTable(Class<T> clazz) {
        if (this.databaseHandlers == null) {
            this.databaseHandlers = new ArrayList<>();
        }
        DatabaseHandler handler = new DatabaseHandler(clazz);
        if (isNewTable(handler)) {
            this.databaseHandlers.add(handler);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Iterator<DatabaseHandler> iterator = this.databaseHandlers.iterator();
            while (iterator.hasNext()) {
                iterator.next().createTable(connectionSource);
            }
        } catch (SQLException e) {
            LogUtil.e("database create fail", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        LogUtil.i(TAG, "数据库升级了 oldVersion = " + oldVersion + " newVersion = " + newVersion);
        try {
            Iterator<DatabaseHandler> iterator = this.databaseHandlers.iterator();
            while (iterator.hasNext()) {
                iterator.next().upgradeTable(database, connectionSource);
            }
        } catch (SQLException e) {
            LogUtil.e("database upgrade fail", e);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ConnectionSource connectionSource = getConnectionSource();
        DatabaseConnection specialConnection = connectionSource.getSpecialConnection(null);
        boolean saved = true;
        if (specialConnection == null) {
            specialConnection = new AndroidDatabaseConnection(database, true, this.cancelQueriesEnabled);
            try {
                connectionSource.saveSpecialConnection(specialConnection);
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        } else {
            saved = false;
        }
        try {
            onDowngradeInternal(connectionSource, oldVersion, newVersion);
        } finally {
            if (saved) {
                connectionSource.clearSpecialConnection(specialConnection);
            }
        }
    }

    /** Recreates the tables after a downgrade. */
    public void onDowngradeInternal(ConnectionSource connectionSource, int oldVersion, int newVersion) {
        LogUtil.i(TAG, "数据库降级了 oldVersion = " + oldVersion + " newVersion = " + newVersion);
        try {
            Iterator<DatabaseHandler> iterator = this.databaseHandlers.iterator();
            while (iterator.hasNext()) {
                iterator.next().downgradeTable(connectionSource, oldVersion, newVersion);
            }
        } catch (SQLException e) {
            LogUtil.e("database downgrade fail", e);
        }
    }

    /** Clears every registered table. */
    public void clearAllTables() {
        try {
            Iterator<DatabaseHandler> iterator = this.databaseHandlers.iterator();
            while (iterator.hasNext()) {
                iterator.next().clearTable(this.connectionSource);
            }
        } catch (SQLException e) {
            LogUtil.e("clear all table fail", e);
        }
    }

    @Override
    public synchronized Dao getDao(Class clazz) {
        Dao dao;
        String simpleName = clazz.getSimpleName();
        if (this.daoCache.containsKey(simpleName)) {
            dao = this.daoCache.get(simpleName);
        } else {
            try {
                dao = super.getDao(clazz);
                this.daoCache.put(simpleName, dao);
            } catch (SQLException e) {
                LogUtil.e("database operate fail", e);
                return null;
            }
        }
        return dao;
    }

    @Override
    public void close() {
        super.close();
        synchronized (this) {
            this.daoCache.clear();
        }
    }

    private boolean isNewTable(DatabaseHandler handler) {
        if (this.databaseHandlers == null || handler == null) {
            return false;
        }
        String tableName = handler.getTableName();
        Iterator<DatabaseHandler> iterator = this.databaseHandlers.iterator();
        while (iterator.hasNext()) {
            if (tableName.equals(iterator.next().getTableName())) {
                return false;
            }
        }
        return true;
    }
}