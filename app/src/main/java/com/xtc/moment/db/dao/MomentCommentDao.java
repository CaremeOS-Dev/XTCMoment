package com.xtc.moment.db.dao;

import android.content.Context;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.Where;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.serve.ServerCache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 评论表 DAO：评论的批量写入/更新、按动态或用户查询、以及未读评论统计。
 */
public class MomentCommentDao extends OrmLiteDao<DbMomentComment> {

    private static final String TAG = "MomentCommentDao";

    public MomentCommentDao(Context context) {
        super(context, DbMomentComment.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public void addComment(DbMomentComment comment) {
        if (comment == null) {
            return;
        }
        insert(comment);
    }

    public void update(final List<DbMomentComment> comments) {
        try {
            new TransactionManager(this.ormLiteDao.getConnectionSource()).callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int updateCount = 0;
                    for (DbMomentComment comment : comments) {
                        if (MomentCommentDao.this.updateBy(comment, "commentId", comment.getCommentId())) {
                            updateCount++;
                        }
                    }
                    LogUtil.i(MomentCommentDao.TAG, "updateComment success, size: " + updateCount);
                    return null;
                }
            });
        } catch (SQLException e) {
            LogUtil.e(TAG, "updateComment error", e);
        }
    }

    public void addComments(final List<DbMomentComment> comments) {
        if (comments == null || comments.size() <= 0) {
            return;
        }
        try {
            new TransactionManager(this.ormLiteDao.getConnectionSource()).callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int insertCount = 0;
                    for (DbMomentComment comment : comments) {
                        List<DbMomentComment> localComments = MomentCommentDao.this.queryByMomentIdAndCommentId(comment.getMomentId(), comment.getCommentId());
                        if (localComments == null || localComments.size() == 0) {
                            if (MomentCommentDao.this.insert(comment)) {
                                insertCount++;
                            }
                        } else {
                            DbMomentComment local = localComments.get(0);
                            if (local.isChecked() != comment.isChecked()) {
                                comment.setChecked(local.isChecked());
                                LogUtil.i(MomentCommentDao.TAG, "addComments successfully,update:" + MomentCommentDao.this.update(comment));
                            }
                        }
                    }
                    LogUtil.i(MomentCommentDao.TAG, "addComments successfully,size:" + insertCount);
                    return null;
                }
            });
        } catch (SQLException e) {
            LogUtil.e(e);
        }
    }

    private List<DbMomentComment> queryByMomentIdAndCommentId(String momentId, String commentId) {
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        conditions.put("commentId", commentId);
        return queryByColumnName(conditions);
    }

    public List<DbMomentComment> queryCommentByMomentId(String momentId) {
        if (TextUtils.isEmpty(momentId)) {
            return new ArrayList<>();
        }
        return queryByColumnName("momentId", momentId);
    }

    public List<DbMomentComment> queryCommentPageByMomentId(long offset, long limit, String momentId) {
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        conditions.put(DbMomentComment.DELETED_FIELD_NAME, false);
        return queryForPagesByOrder(conditions, "createTime", true, Long.valueOf(offset), Long.valueOf(limit));
    }

    public List<DbMomentComment> queryCommentByWatchId(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return null;
        }
        return queryByColumnName("watchId", watchId);
    }

    public List<DbMomentComment> queryCommentByCommentId(String commentId) {
        if (TextUtils.isEmpty(commentId)) {
            return new ArrayList<>(0);
        }
        return queryByColumnName("commentId", commentId);
    }

    public List<DbMomentComment> queryCommentByMomentIdAndCommentId(String momentId, String commentId) {
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(commentId)) {
            return null;
        }
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        conditions.put("commentId", commentId);
        return queryByColumnName(conditions);
    }

    public void deleteDBCommentByMomentIdAndCommentId(String momentId, String commentId) {
        if (TextUtils.isEmpty(momentId) || TextUtils.isEmpty(commentId)) {
            return;
        }
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("momentId", momentId);
        conditions.put("commentId", commentId);
        deleteByColumnName(conditions);
    }

    public boolean deleteCommentByMomentWatchId(String momentWatchId) {
        return deleteByColumnName("momentWatchId", momentWatchId);
    }

    public boolean deleteCommentByWatchId(String watchId) {
        return deleteByColumnName("watchId", watchId);
    }

    public boolean deleteCommentByReplyId(String replyId) {
        return deleteByColumnName(DbMomentComment.REPLYID_FIELD_NAME, replyId);
    }

    public boolean deleteCommentByMomentId(String momentId) {
        return deleteByColumnName("momentId", momentId);
    }

    public void deleteDbCommentForBatch(List<DbMomentComment> comments) {
        if (comments == null || comments.size() <= 0) {
            return;
        }
        for (int i = 0; i < comments.size(); i++) {
            deleteDBCommentByMomentIdAndCommentId(comments.get(i).getMomentId(), comments.get(i).getCommentId());
        }
    }

    public List<DbMomentComment> loadAllUncheckedComment() {
        return queryByColumnName("checked", false);
    }

    public List<DbMomentComment> loadAllUncheckedCommentByWatchId(String watchId) {
        try {
            return buildWhereForUncheckedCommentByWatchId(watchId).query();
        } catch (SQLException e) {
            LogUtil.e(TAG, "loadAllUncheckedCommentByWatchId error: ", e);
            return new ArrayList<>();
        } catch (Throwable throwable) {
            return new ArrayList<>();
        }
    }

    public long getUncheckedCommentCountByWatchId(String watchId) {
        try {
            return buildWhereForUncheckedCommentByWatchId(watchId).countOf();
        } catch (SQLException e) {
            LogUtil.e(TAG, "getUncheckedCommentCountByWatchId: ", e);
            return 0L;
        }
    }

    /**
     * 未读评论条件：动态作者是我，或者评论回复的是我。
     */
    private Where<DbMomentComment, Integer> buildWhereForUncheckedCommentByWatchId(String watchId) throws SQLException {
        Where<DbMomentComment, Integer> where = this.ormLiteDao.queryBuilder().where();
        return where.or(
                where.and(where.eq("checked", false), where.eq("momentWatchId", watchId), new Where[0]),
                where.and(where.eq("checked", false), where.eq(DbMomentComment.REPLYID_FIELD_NAME, watchId), new Where[0]),
                new Where[0]);
    }
}