package com.xtc.moment.util;

import android.content.Context;
import android.net.Uri;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.Constants;
import com.xtc.utils.encode.JSONUtil;

/**
 * 通过 ContentResolver 通知 Provider 数据变更。
 */
public class ProviderNotifyUtils {

    public static void notifyPersonalMomentDeleted(DbMoment moment, Context context) {
        Uri uri = buildMomentUri(moment.getType().intValue());
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.DELETE_MY_MOMENT);
        builder.appendQueryParameter("data", JSONUtil.toJSON(moment));
        context.getContentResolver().notifyChange(builder.build(), null);
    }

    public static void notifyCommentDelete(DbMomentComment comment, Context context) {
        Uri.Builder builder = Uri.parse("content://com.xtc.moment.commentProvider/comment").buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.DELETE_COMMENT_OR_CANCEL_LIKE);
        builder.appendQueryParameter("data", JSONUtil.toJSON(comment));
        context.getContentResolver().notifyChange(builder.build(), null);
    }

    public static void notifyPersonalMomentDeletedShare(DbMoment moment, Context context) {
        Uri uri = buildMomentUri(moment.getType().intValue());
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.DELETE_MY_MOMENT);
        builder.appendQueryParameter("data", JSONUtil.toJSON(moment));
        context.getContentResolver().notifyChange(builder.build(), null);
    }

    private static Uri buildMomentUri(int type) {
        if (type == 0) {
            return Uri.parse("content://com.xtc.moment.momentProvider/mood");
        }
        if (type == 1) {
            return Uri.parse("content://com.xtc.moment.momentProvider/state");
        }
        if (type == 2) {
            return Uri.parse("content://com.xtc.moment.momentProvider/location");
        }
        if (type == 3) {
            return Uri.parse("content://com.xtc.moment.momentProvider/word");
        }
        if (type == 5) {
            return Uri.parse("content://com.xtc.moment.momentProvider/photo");
        }
        if (type == 6) {
            return Uri.parse("content://com.xtc.moment.momentProvider/video");
        }
        return Uri.parse("content://com.xtc.moment.momentProvider/item");
    }
}