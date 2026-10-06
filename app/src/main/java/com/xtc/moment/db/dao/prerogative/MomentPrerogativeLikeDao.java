package com.xtc.moment.db.dao.prerogative;

import android.content.Context;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.prerogative.AbsPrerogativeBean;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.serve.ServerCache;

import java.util.List;

/**
 * 点赞特权资源 DAO。
 */
public class MomentPrerogativeLikeDao extends OrmLiteDao<DbMomentPrerogativeLike> implements IPrerogativeDao<DbMomentPrerogativeLike> {

    private static final String TAG = "MomentPrerogativeLikeDao";

    public MomentPrerogativeLikeDao(Context context) {
        super(context, DbMomentPrerogativeLike.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    @Override
    public boolean insertList(List<DbMomentPrerogativeLike> list) {
        if (CollectionUtil.isEmpty(list)) {
            LogUtil.d(TAG, "insertList : likes invalid");
            return false;
        }
        LogUtil.d(TAG, "insertList : likes sizi = " + list.size());
        return insertForBatch(list);
    }

    @Override
    public int deleteDataForAll() {
        LogUtil.d(TAG, "deleteDataForAll");
        return deleteAll();
    }

    @Override
    public List<DbMomentPrerogativeLike> queryDataForAll() {
        LogUtil.d(TAG, "queryDataForAll");
        return queryForAll();
    }

    @Override
    public DbMomentPrerogativeLike queryDbForPrerogativeId(int prerogativeId) {
        LogUtil.d(TAG, "queryDbForPrerogativeId: prerogativeId = [" + prerogativeId + "]");
        return queryForFirst(AbsPrerogativeBean.DB_PREROGATIVE_ID, Integer.valueOf(prerogativeId));
    }

    @Override
    public boolean updateLocalPrerogativeById(DbMomentPrerogativeLike item) {
        LogUtil.d(TAG, "updateLocalPrerogativeById: dbMomentPrerogativeLike = [" + item + "]");
        return updateBy(item, AbsPrerogativeBean.DB_PREROGATIVE_ID, Integer.valueOf(item.getPrerogativeId()));
    }
}