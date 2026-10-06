package com.xtc.moment.db.dao;

import android.content.Context;

import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbSexyPhotoDistinguish;
import com.xtc.moment.serve.ServerCache;

import java.util.List;

/**
 * 失效动态（性感图片识别记录）DAO。
 */
public class NetInvalidMomentDao extends OrmLiteDao<DbSexyPhotoDistinguish> {

    public NetInvalidMomentDao(Context context) {
        super(context, DbSexyPhotoDistinguish.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public boolean addMomentBatch(List<DbSexyPhotoDistinguish> records) {
        return insertForBatch(records);
    }
}