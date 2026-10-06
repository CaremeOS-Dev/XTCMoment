package com.xtc.virtualselfapi.helper;

import android.util.Log;

import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.db.DbDanger;
import com.xtc.virtualselfapi.bean.db.DbDecorate;
import com.xtc.virtualselfapi.bean.db.DbPosition;

/** Registers the virtual-self tables on the host database. */
public class VirtualSelfDbHelper {

    private static final String TAG = "Virtual_Self_Api_VirtualSelfDbHelper";

    private VirtualSelfDbHelper() {
    }

    public static void registerTable(DatabaseHelper databaseHelper) {
        if (databaseHelper == null) {
            Log.d(TAG, "databaseHelper is null");
            return;
        }
        Log.d(TAG, "registerTable: start");
        databaseHelper.registerTable(DbCostume.class);
        databaseHelper.registerTable(DbDanger.class);
        databaseHelper.registerTable(DbDecorate.class);
        databaseHelper.registerTable(DbPosition.class);
        Log.d(TAG, "registerTable: finish");
    }
}