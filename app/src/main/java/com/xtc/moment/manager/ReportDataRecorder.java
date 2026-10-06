package com.xtc.moment.manager;

import android.util.ArrayMap;

import com.xtc.log.util.TextUtils;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;

/**
 * 举报流程中暂存动态与评论数据。
 */
public class ReportDataRecorder {

    private static ArrayMap<String, DbMoment> momentRecord;
    private static ArrayMap<String, DbMomentComment> commentRecord;

    public static DbMoment getRecordDbMoment(String momentId) {
        ArrayMap<String, DbMoment> records = momentRecord;
        if (records == null || records.isEmpty()) {
            return null;
        }
        return momentRecord.get(momentId);
    }

    private static void removeMomentRecord(String momentId) {
        ArrayMap<String, DbMoment> records = momentRecord;
        if (records == null || records.isEmpty() || !momentRecord.containsKey(momentId)) {
            return;
        }
        momentRecord.remove(momentId);
    }

    public static void recordDbMoment(DbMoment moment) {
        if (moment == null) {
            return;
        }
        if (momentRecord == null) {
            momentRecord = new ArrayMap<>();
        }
        if (momentRecord.containsKey(moment.getMomentId())) {
            return;
        }
        momentRecord.put(moment.getMomentId(), moment);
    }

    public static DbMomentComment getRecordComment(String key) {
        ArrayMap<String, DbMomentComment> records = commentRecord;
        if (records == null || records.isEmpty()) {
            return null;
        }
        return commentRecord.get(key);
    }

    public static void recordMomentComment(DbMomentComment comment) {
        if (comment == null) {
            return;
        }
        if (commentRecord == null) {
            commentRecord = new ArrayMap<>();
        }
        String key = comment.getMomentId() + comment.getCommentId();
        if (commentRecord.containsKey(key)) {
            return;
        }
        commentRecord.put(key, comment);
    }

    private static void removeCommentRecord(String key) {
        ArrayMap<String, DbMomentComment> records = commentRecord;
        if (records == null || records.isEmpty() || !commentRecord.containsKey(key)) {
            return;
        }
        commentRecord.remove(key);
    }

    public static void removeRecord(String momentId, String commentId) {
        if (TextUtils.isEmpty(commentId)) {
            removeMomentRecord(momentId);
            return;
        }
        removeCommentRecord(momentId + commentId);
    }
}