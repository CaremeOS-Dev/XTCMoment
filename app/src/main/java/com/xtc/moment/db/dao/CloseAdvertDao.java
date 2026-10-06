package com.xtc.moment.db.dao;

import android.content.Context;

import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbAdvertCloseRecord;
import com.xtc.moment.serve.ServerCache;

/**
 * 广告动态关闭记录 DAO。
 */
public class CloseAdvertDao extends OrmLiteDao<DbAdvertCloseRecord> {

    public CloseAdvertDao(Context context) {
        super(context, DbAdvertCloseRecord.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public boolean addCloseAdvertRecord(DbAdvertCloseRecord record) {
        if (record == null) {
            return false;
        }
        return insert(record);
    }

    public DbAdvertCloseRecord getByAdvertId(String advertId) {
        return queryForFirst("advertId", advertId);
    }

    @Override
    public int deleteAll() {
        return super.deleteAll();
    }
}