package com.xtc.database.ormlite;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.logger.LocalLog;
import com.xtc.log.LogUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/** Process-wide registry of the opened databases. */
public class DatabaseHelper extends OrmLiteDatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    private static final Map<String, DatabaseInfo> DATABASE_INFOS = new HashMap<>();

    private DatabaseHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "FATAL");
    }

    public static DatabaseHelper getInstance(Context context, String databaseName) {
        return getInstance(context, databaseName, -1);
    }

    public static synchronized DatabaseHelper getInstance(Context context, String databaseName, int databaseVersion) {
        if (TextUtils.isEmpty(databaseName)) {
            LogUtil.d(TAG, "database name is null");
            throw new IllegalStateException("database name is null");
        }
        if (DATABASE_INFOS.get(databaseName) != null && DATABASE_INFOS.get(databaseName).getHelper() != null) {
            return DATABASE_INFOS.get(databaseName).getHelper();
        }
        LogUtil.d(TAG, "current instance is  null now,init here");
        return createDatabase(context, databaseName, databaseVersion);
    }

    private static DatabaseHelper createDatabase(Context context, String databaseName, int databaseVersion) {
        LogUtil.i(TAG, "initDatabase dbName = " + databaseName + ",version " + databaseVersion
                + ",ThreadId = " + Thread.currentThread().getName());
        synchronized (DatabaseHelper.class) {
            DatabaseInfo info = DATABASE_INFOS.get(databaseName);
            if (info == null) {
                info = new DatabaseInfo();
            }
            info.setDatabaseName(databaseName);
            int version = -1;
            boolean debug = false;
            try {
                Class<?> buildConfig = Class.forName(context.getPackageName() + ".BuildConfig");
                Field versionField = buildConfig.getDeclaredField("VERSION_CODE");
                versionField.setAccessible(true);
                version = versionField.getInt(null);
                try {
                    Field debugField = buildConfig.getDeclaredField("DEBUG");
                    debugField.setAccessible(true);
                    debug = debugField.getBoolean(null);
                } catch (Exception e) {
                    LogUtil.e(TAG, e);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e);
                version = -1;
            }
            if (!debug || databaseVersion == -1) {
                databaseVersion = version;
            }
            if (databaseVersion == -1) {
                LogUtil.d(TAG, "something went wrong,need set version = 1");
                databaseVersion = 1;
            }
            LogUtil.i(TAG, "initDatabase dbVersion " + databaseVersion);
            info.setDatabaseVersion(databaseVersion);
            DatabaseHelper helper = new DatabaseHelper(context, databaseName, databaseVersion);
            info.setHelper(helper);
            DATABASE_INFOS.put(databaseName, info);
            return helper;
        }
    }

    /** Registers the entity [clazz] on this database. */
    public <T> void registerTable(Class<T> clazz) {
        super.registerTable(clazz);
    }

    /** Closes the database with the given name. */
    public synchronized void closeDatabase(String databaseName) {
        if (DATABASE_INFOS.get(databaseName) != null && DATABASE_INFOS.get(databaseName).getHelper() != null) {
            DATABASE_INFOS.get(databaseName).getHelper().close();
            return;
        }
        LogUtil.i(TAG, "current database helper is null");
    }
}