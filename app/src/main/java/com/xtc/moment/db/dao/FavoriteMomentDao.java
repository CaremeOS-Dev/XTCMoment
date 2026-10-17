package com.xtc.moment.db.dao;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbFavoriteMoment;
import com.xtc.moment.serve.ServerCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏动态 DAO：按 momentId 唯一保存一条收藏记录（含动态快照）。
 */
public class FavoriteMomentDao extends OrmLiteDao<DbFavoriteMoment> {

    private static final String TAG = "FavoriteMomentDao";

    public FavoriteMomentDao(Context context) {
        super(context, DbFavoriteMoment.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    /**
     * 写入一条收藏记录：已存在则更新快照，不存在则插入。
     */
    public boolean insertOrUpdate(DbFavoriteMoment favorite) {
        if (favorite == null || TextUtils.isEmpty(favorite.getMomentId())) {
            return false;
        }
        DbFavoriteMoment local = queryByMomentId(favorite.getMomentId());
        if (local == null) {
            return insert(favorite);
        }
        favorite.setId(local.getId());
        return updateBy(favorite, DbFavoriteMoment.MOMENT_ID_FIELD_NAME, favorite.getMomentId());
    }

    public DbFavoriteMoment queryByMomentId(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return null;
        }
        List<DbFavoriteMoment> favorites =
                queryByColumnName(DbFavoriteMoment.MOMENT_ID_FIELD_NAME, momentId);
        if (favorites == null || favorites.isEmpty()) {
            return null;
        }
        return favorites.get(0);
    }

    /** 按收藏时间倒序返回全部收藏。 */
    public List<DbFavoriteMoment> queryAllOrderByCreateTimeDesc() {
        List<DbFavoriteMoment> favorites =
                queryAllByOrder(DbFavoriteMoment.CREATE_TIME_FIELD_NAME, false);
        return favorites == null ? new ArrayList<DbFavoriteMoment>() : favorites;
    }

    public boolean deleteByMomentId(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return false;
        }
        return deleteByColumnName(DbFavoriteMoment.MOMENT_ID_FIELD_NAME, momentId);
    }

    public boolean clearAll() {
        try {
            return clearTableData();
        } catch (Exception e) {
            LogUtil.e(TAG, "clearAll failed", e);
            return false;
        }
    }
}
