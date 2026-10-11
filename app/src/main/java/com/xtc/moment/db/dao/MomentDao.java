package com.xtc.moment.db.dao;

import android.content.Context;
import android.database.Cursor;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.MomentDbManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.utils.encode.JSONUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 动态表 DAO：负责动态的增删改查，以及旧数据封面信息的回填。
 */
public class MomentDao extends OrmLiteDao<DbMoment> {

    private static final String SQL_QUERY_ADVERTISE_LIST = "SELECT * FROM moment WHERE length(watchId) < 40";
    private static final String STRING_SOURCE = "source";
    private static final String TAG = "MomentDao";

    public MomentDao(Context context) {
        super(context, DbMoment.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public List<DbMoment> queryPersonalMoments(long offset, long limit, String watchId) {
        return queryForPagesByOrder("watchId", watchId, "createTime", false, offset, limit);
    }

    public List<DbMoment> queryMoments(Long offset, Long limit) {
        return queryForPagesByOrder("createTime", false, offset, limit);
    }

    public List<DbMoment> queryTopMoments() {
        QueryBuilder<DbMoment, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().eq("top", 1).and().gt("topExpireTime", Long.valueOf(System.currentTimeMillis()));
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public List<DbMoment> queryByMomentId(String momentId) {
        return queryByColumnName("momentId", momentId);
    }

    public List<DbMoment> queryByMomentIds(Set<String> momentIds) {
        if (momentIds != null && momentIds.size() > 0) {
            Iterator<String> iterator = momentIds.iterator();
            if (iterator != null && iterator.hasNext()) {
                try {
                    Where<DbMoment, Integer> where = this.ormLiteDao.queryBuilder().where();
                    while (iterator.hasNext()) {
                        where.eq("momentId", iterator.next());
                    }
                    where.or(momentIds.size());
                    List<DbMoment> moments = where.query();
                    LogUtil.d(TAG, "dbMoments:" + moments);
                    return moments;
                } catch (SQLException e) {
                    LogUtil.e(TAG, "queryByMomentIds error: ", e);
                }
            }
        }
        return null;
    }

    public List<DbMoment> queryByLimitTime(String watchId, long time) {
        try {
            Where<DbMoment, Integer> where = this.ormLiteDao.queryBuilder().where();
            where.eq("watchId", watchId).and().between("createTime", 0, Long.valueOf(time));
            return where.query();
        } catch (SQLException e) {
            LogUtil.i(TAG, "queryByMomentCreateTime error", e);
            return null;
        }
    }

    public List<DbMoment> queryByWatchId(String watchId) {
        return queryByColumnName("watchId", watchId);
    }

    /**
     * 「搜索动态」的候选分页查询：按关键词对文本类字段做 LIKE 粗筛，创建时间倒序返回。
     *
     * <p>粗筛只负责缩小范围（LIKE 对 JSON 内容会连键名一起命中），命中与否由
     * {@code MomentSearchTextUtil} 在内存里精确校验，因此这里多召回是安全的。
     *
     * @param keyword 关键词，调用方需保证非空
     * @param offset  起始行号
     * @param limit   本次最多返回行数
     * @return 候选动态；查询失败返回 {@code null}
     */
    public List<DbMoment> searchMomentCandidates(String keyword, long offset, long limit) {
        if (com.xtc.log.util.TextUtils.isEmpty(keyword)) {
            return null;
        }
        try {
            // 注意：Where 的 like/or 都是链式返回自身，不能把同一个 Where 实例拆开传参。
            String pattern = "%" + keyword + "%";
            QueryBuilder<DbMoment, Integer> builder = this.ormLiteDao.queryBuilder();
            builder.where()
                    .like("content", pattern)
                    .or().like("publishContent", pattern)
                    .or().like("description", pattern)
                    .or().like("location", pattern)
                    .or().like("name", pattern);
            builder.orderBy("createTime", false).orderBy("id", false);
            builder.offset(Long.valueOf(offset)).limit(Long.valueOf(limit));
            return builder.query();
        } catch (SQLException e) {
            LogUtil.e(TAG, "searchMomentCandidates error: keyword=" + keyword + ", offset=" + offset, e);
            return null;
        }
    }

    public void addMoments(final List<DbMoment> moments) {
        try {
            new TransactionManager(this.ormLiteDao.getConnectionSource()).callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int insertCount = 0;
                    int updateCount = 0;
                    for (DbMoment moment : moments) {
                        String momentId = moment.getMomentId();
                        List<DbMoment> localMoments = MomentDao.this.queryByMomentId(momentId);
                        if (localMoments == null || localMoments.size() == 0) {
                            if (MomentDao.this.insert(moment)) {
                                insertCount++;
                                LogUtil.d(MomentDao.TAG, "addMoments insert momentId = " + momentId);
                            }
                        } else {
                            DbMoment local = localMoments.get(0);
                            if (MomentTypeUtil.isOfficialType(moment)) {
                                moment.setEnableLike(local.isEnableLike());
                                moment.setLikeTotal(local.getLikeTotal());
                            }
                            int type = moment.getType().intValue();
                            String localContent = local.getContent();
                            if (!com.xtc.log.util.TextUtils.isEmpty(localContent)) {
                                MomentDao.this.resetDbMoment(moment, localContent, type);
                            }
                            if (!com.xtc.log.util.TextUtils.isEmpty(local.getReminderContent())) {
                                moment.setReminderContent(local.getReminderContent());
                                LogUtil.d(ReminderHelper.M_TAG, "db resetReminder = " + momentId);
                            }
                            if (!com.xtc.log.util.TextUtils.isEmpty(local.getReminderUrl())) {
                                moment.setReminderUrl(local.getReminderUrl());
                            }
                            if (MomentDao.this.updateBy(moment, "momentId", momentId)) {
                                updateCount++;
                                LogUtil.d(MomentDao.TAG, "addMoments updateMoments momentId = " + momentId);
                            }
                        }
                    }
                    LogUtil.i(MomentDao.TAG, "addMoments successfully,size:" + insertCount);
                    LogUtil.i(MomentDao.TAG, "updateMoments successfully,size:" + updateCount);
                    return null;
                }
            });
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
        }
    }

    /**
     * 把本地已有的封面信息回填到服务端返回的新数据上，避免封面 URL 过期后图片丢失。
     */
    private void resetDbMoment(DbMoment moment, String localContent, int type) {
        LogUtil.d(TAG, "this dbMoment " + moment + " this type：" + type);
        if (MomentTypeUtil.isSharePhoto(type)) {
            if (localContent.contains(STRING_SOURCE)) {
                dealMsgCover(moment, localContent, type);
            }
            return;
        }
        if (MomentTypeUtil.isCommonPhoto(type)) {
            dealMsgCover(moment, localContent, type);
            return;
        }
        if (MomentTypeUtil.isPhotoList(type)) {
            MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(localContent, MultiPhotoContent.class);
            if (multiPhotoContent == null) {
                return;
            }
            CloudFileResource resource = multiPhotoContent.getResource();
            if (resource == null || isOverTime(resource.getUrlDeadline())) {
                return;
            }
            moment.setContent(localContent);
            return;
        }
        if (MomentTypeUtil.isVideoContent(type)) {
            MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(localContent, MultiPhotoContent.class);
            if (multiPhotoContent == null) {
                return;
            }
            String videoMsgContent = multiPhotoContent.getVideoMsgContent();
            if (com.xtc.log.util.TextUtils.isEmpty(videoMsgContent)) {
                return;
            }
            dealVideoMsgCover(moment, videoMsgContent, type);
            return;
        }
        if (MomentTypeUtil.isShareVideo(type) || MomentTypeUtil.isVideo(type)) {
            dealVideoMsgCover(moment, localContent, type);
        }
    }

    private static void dealVideoMsgCover(DbMoment moment, String content, int type) {
        VideoMsg videoMsg = JSONUtil.fromJSON(content, VideoMsg.class);
        if (videoMsg == null) {
            return;
        }
        CloudFileResource source = videoMsg.getSource();
        CloudFileResource icon = videoMsg.getIcon();
        boolean sourceNull = source == null;
        boolean iconNull = icon == null;
        if (sourceNull || iconNull) {
            LogUtil.d(TAG, "source = null ? :" + sourceNull);
            LogUtil.d(TAG, "icon = null ? :" + iconNull);
            return;
        }
        boolean bothValid = !(sourceNull || iconNull);
        boolean notExpired = !(isOverTime(source.getUrlDeadline()) || isOverTime(icon.getUrlDeadline()));
        if (bothValid && notExpired) {
            if (MomentTypeUtil.isVideoContent(type)) {
                moment.setPublishContent(content);
                setVideoContent(moment, videoMsg);
            } else {
                moment.setContent(content);
            }
        }
    }

    public static void setVideoContent(DbMoment moment, VideoMsg videoMsg) {
        String content = moment.getContent();
        if (android.text.TextUtils.isEmpty(content)) {
            LogUtil.i(TAG, "setVideoContent, content is empty");
            return;
        }
        MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(content, MultiPhotoContent.class);
        multiPhotoContent.setVideoMsgContent(JSONUtil.toJSON(videoMsg));
        moment.setContent(JSONUtil.toJSON(multiPhotoContent));
    }

    private static void dealMsgCover(DbMoment moment, String content, int type) {
        PhotoMsg photoMsg = JSONUtil.fromJSON(content, PhotoMsg.class);
        if (photoMsg == null) {
            return;
        }
        CloudFileResource source = photoMsg.getSource();
        if (source == null || isOverTime(source.getUrlDeadline())) {
            return;
        }
        moment.setContent(content);
        if (MomentTypeUtil.isCommonPhoto(type)) {
            moment.setPublishContent(content);
        }
    }

    private static boolean isOverTime(long deadline) {
        return System.currentTimeMillis() > deadline;
    }

    public void updateMomentsByMomentId(final List<DbMoment> moments) {
        try {
            new TransactionManager(this.ormLiteDao.getConnectionSource()).callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (DbMoment moment : moments) {
                        List<DbMoment> localMoments = MomentDao.this.queryByMomentId(moment.getMomentId());
                        if (localMoments == null || localMoments.size() == 0) {
                            MomentDao.this.insert(moment);
                        } else {
                            MomentDao.this.updateBy(moment, "momentId", moment.getMomentId());
                        }
                    }
                    LogUtil.i(MomentDao.TAG, "updateMoments successfully,size:" + moments.size());
                    return null;
                }
            });
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
        }
    }

    public boolean updateMomentByMomentId(DbMoment moment) {
        return updateBy(moment, "momentId", moment.getMomentId());
    }

    public boolean deleteMomentByWatchId(String watchId) {
        return deleteByColumnName("watchId", watchId);
    }

    public boolean deleteMomentByMomentId(String watchId, String momentId) {
        LogUtil.d(TAG, watchId + ": deleteMomentByMomentId = " + momentId);
        return deleteByColumnName("momentId", momentId);
    }

    public List<DbMoment> getOthersUncheckedMoment(String watchIds) {
        try {
            QueryBuilder<DbMoment, Integer> builder = this.ormLiteDao.queryBuilder();
            builder.where().eq("checked", false).and().notIn("watchId", watchIds);
            return builder.query();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public long getOthersUncheckedMomentCount(String watchIds) {
        QueryBuilder<DbMoment, Integer> builder = this.ormLiteDao.queryBuilder();
        try {
            builder.where().eq("checked", false).and().notIn("watchId", watchIds);
            return builder.countOf();
        } catch (SQLException e) {
            LogUtil.i(TAG, "getOthersUncheckedMoment: ", e);
            return 0L;
        }
    }

    /**
     * 查询本地缓存的广告动态（watchId 长度小于 40 的记录）。
     */
    public List<DbMoment> getLocalAdvertiseList(Context context) {
        long startTime = System.currentTimeMillis();
        ArrayList<DbMoment> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = MomentDbManager.getInstance(context.getApplicationContext()).getDatabaseHelper().getReadableDatabase().rawQuery(SQL_QUERY_ADVERTISE_LIST, null);
            if (cursor == null) {
                LogUtil.w(TAG, "getLocalAdvertiseList: no advertise in local database: " + (System.currentTimeMillis() - startTime));
                return null;
            }
            while (cursor.moveToNext()) {
                DbMoment moment = new DbMoment();
                moment.setCreateTime(Long.valueOf(cursor.getLong(cursor.getColumnIndex("createTime"))));
                moment.setMomentId(cursor.getString(cursor.getColumnIndex("momentId")));
                moment.setWatchId(cursor.getString(cursor.getColumnIndex("watchId")));
                moment.setType(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("type"))));
                result.add(moment);
            }
            LogUtil.i(TAG, "getLocalAdvertiseList: " + result.size() + ", 耗时：" + (System.currentTimeMillis() - startTime));
            return result;
        } catch (Exception e) {
            LogUtil.e(TAG, "getLocalAdvertiseList#error", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}