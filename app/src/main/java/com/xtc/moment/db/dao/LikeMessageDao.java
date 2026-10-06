package com.xtc.moment.db.dao;

import android.content.Context;

import com.j256.ormlite.misc.TransactionManager;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.serve.ServerCache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 点赞消息 DAO。
 */
public class LikeMessageDao extends OrmLiteDao<DbLikeMessage> {

    private static final String TAG = "LikeMessageDao";

    public LikeMessageDao(Context context) {
        super(context, DbLikeMessage.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public List<DbLikeMessage> getByMomentId(String momentId) {
        return queryByColumnName("momentId", momentId);
    }

    public List<DbLikeMessage> getByMomentIdAndWatchId(String momentId, String watchId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        conditions.put("watchId", watchId);
        return queryByColumnName(conditions);
    }

    public void create(final List<DbLikeMessage> messages) {
        try {
            new TransactionManager(this.ormLiteDao.getConnectionSource()).callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (DbLikeMessage message : messages) {
                        List<DbLikeMessage> existing =
                                getByMomentIdAndWatchId(message.getMomentId(), message.getWatchId());
                        if (existing == null || existing.size() == 0) {
                            insert(message);
                        }
                    }
                    LogUtil.i(TAG, "create like message successfully,size:" + messages.size());
                    return null;
                }
            });
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
        }
    }

    public void update(List<DbLikeMessage> messages) {
        if (updateForBatch(messages)) {
            LogUtil.i(TAG, "update dbLikeMessageList successfully,size:" + messages.size());
            return;
        }
        LogUtil.i(TAG, "update dbLikeMessageList failed,size:" + messages.size());
    }

    public void updateName(String watchId, String watchName) {
        List<DbLikeMessage> messages = queryByColumnName("watchId", watchId);
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (DbLikeMessage message : messages) {
            if (message != null) {
                message.setWatchName(watchName);
            }
        }
        update(messages);
    }

    public List<DbLikeMessage> loadAllUncheckedLikeMessage() {
        return queryByColumnName("checked", false);
    }

    public List<DbLikeMessage> loadAllUncheckedLikeMessageByWatchId(String watchId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("checked", false);
        conditions.put("momentWatchId", watchId);
        return queryByColumnName(conditions);
    }

    public long getUncheckedLikeMessageCountByWatchId(String watchId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("checked", false);
        conditions.put("momentWatchId", watchId);
        return getCount(conditions);
    }

    public boolean deleteByMomentId(String momentId) {
        return deleteByColumnName("momentId", momentId);
    }

    public boolean deleteChecked() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("checked", true);
        return deleteByColumnName(conditions);
    }
}