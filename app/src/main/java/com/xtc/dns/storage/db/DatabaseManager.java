package com.xtc.dns.storage.db;

import android.content.Context;

import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.dns.LogTag;
import com.xtc.log.LogUtil;

/**
 * DNS 数据库管理器，负责初始化 HttpDns.db。
 */
public class DatabaseManager {

    static final String DB_NAME = "HttpDns.db";

    private static final String TAG = LogTag.tag("DatabaseManager");

    private static volatile DatabaseManager instance;

    private final DatabaseHelper databaseHelper;
    private volatile boolean initialized = false;

    private DatabaseManager(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context, DB_NAME);
    }

    public static DatabaseManager getInstance(Context context) {
        DatabaseManager manager = instance;
        if (manager == null) {
            synchronized (DatabaseManager.class) {
                manager = instance;
                if (manager == null) {
                    manager = new DatabaseManager(context);
                    instance = manager;
                }
            }
        }
        return manager;
    }

    /** 初始化数据库表。 */
    public synchronized void init() {
        if (this.initialized) {
            LogUtil.d(TAG, "database has inited , return !");
            return;
        }
        this.initialized = true;
        this.databaseHelper.registerTable(DomainModel.class);
        LogUtil.d(TAG, "database init completed");
    }
}