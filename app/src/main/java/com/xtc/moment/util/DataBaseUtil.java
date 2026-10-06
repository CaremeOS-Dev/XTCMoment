package com.xtc.moment.util;

import android.database.sqlite.SQLiteDatabase;

import com.xtc.log.LogUtil;

/**
 * 数据库完整性检查工具。
 */
public class DataBaseUtil {

    private static final String TAG = "DataBaseUtil";

    public static boolean isDatabaseIntegrityOk(SQLiteDatabase database) {
        try {
            return database.isDatabaseIntegrityOk();
        } catch (Exception e) {
            LogUtil.e(TAG, "isDatabaseIntegrityOk: ", e);
            return false;
        }
    }
}