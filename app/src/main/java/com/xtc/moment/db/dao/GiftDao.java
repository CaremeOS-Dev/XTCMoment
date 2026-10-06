package com.xtc.moment.db.dao;

import android.content.Context;

import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbGiftRecord;
import com.xtc.moment.serve.ServerCache;

import java.util.List;

/**
 * 礼物记录 DAO。
 */
public class GiftDao extends OrmLiteDao<DbGiftRecord> {

    private static final String TAG = "GiftDao";

    public GiftDao(Context context) {
        super(context, DbGiftRecord.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public boolean addGiftRecordMessage(DbGiftRecord record) {
        return insert(record);
    }

    public List<DbGiftRecord> getByMomentId(String momentId) {
        return queryByColumnName("momentId", momentId);
    }

    public boolean updateGiftMessage(DbGiftRecord record) {
        return updateBy(record, "momentId", record.getMomentId());
    }
}