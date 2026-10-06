package com.xtc.moment.module.illegal.dao;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.database.ormlite.RxDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbIllegalRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * 违规记录本地表的读写服务。
 */
public class DbIllegalRecordServe {

    private static final String TAG = "DbIllegalRecordServe";
    private static final String COLUMN_WATCH_ID = "watchId";

    private static DbIllegalRecordServe mInstance;

    private Context mContext;
    private RxDao<DbIllegalRecord> mDbIllegalRecordRxDao;

    public static DbIllegalRecordServe getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DbIllegalRecordServe.class) {
                if (mInstance == null) {
                    mInstance = new DbIllegalRecordServe(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public DbIllegalRecordServe(Context context) {
        this.mContext = context;
        this.mDbIllegalRecordRxDao = new RxDao<>(context, DbIllegalRecord.class, Constants.DATABASE_NAME);
    }

    public DbIllegalRecord queryIllegalRecordByWatchId(String watchId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put(COLUMN_WATCH_ID, watchId);
        return this.mDbIllegalRecordRxDao.queryForFirst(conditions);
    }

    public boolean addIllegalRecord(DbIllegalRecord record) {
        if (record == null) {
            return false;
        }
        return this.mDbIllegalRecordRxDao.insert(record);
    }

    public boolean updateIllegalRecord(DbIllegalRecord record) {
        if (record == null) {
            return false;
        }
        return this.mDbIllegalRecordRxDao.update(record);
    }

    public boolean deleteIllegalRecord(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return false;
        }
        Map<String, Object> conditions = new HashMap<>();
        conditions.put(COLUMN_WATCH_ID, watchId);
        return this.mDbIllegalRecordRxDao.deleteByColumnName(conditions);
    }

    /** 已存在则更新，不存在则新增。 */
    public boolean addOrUpdateIllegalRecord(DbIllegalRecord record) {
        String watchId = record.getWatchId();
        if (TextUtils.isEmpty(watchId)) {
            return false;
        }
        DbIllegalRecord existingRecord = queryIllegalRecordByWatchId(watchId);
        if (existingRecord == null) {
            LogUtil.i(TAG, "addIllegalRecord DbIllegalRecord:" + record);
            return addIllegalRecord(record);
        }
        record.setId(existingRecord.getId());
        LogUtil.i(TAG, "updateIllegalRecord DbIllegalRecord:" + record);
        return updateIllegalRecord(record);
    }
}