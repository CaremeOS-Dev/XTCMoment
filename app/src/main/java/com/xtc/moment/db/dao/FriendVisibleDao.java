package com.xtc.moment.db.dao;

import android.content.Context;

import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbVisible;
import com.xtc.moment.serve.ServerCache;

/**
 * 动态可见范围记录表 DAO。
 */
public class FriendVisibleDao extends OrmLiteDao<DbVisible> {

    private static final String TAG = "FriendVisibleDao";

    public FriendVisibleDao(Context context) {
        super(context, DbVisible.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }
}