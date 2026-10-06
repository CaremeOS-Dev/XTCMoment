package com.xtc.moment.util;

import android.content.Context;
import android.net.Uri;

import com.xtc.log.LogUtil;

/**
 * 未读数变化通知，通过 ContentResolver 通知对应的 Provider。
 */
public class NotifyUtils {

    private static final String TAG = "Moment_NotifyUtils";

    public static void notifyLikeUnReadNum(final Context context) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "notifyUnReadNum--");
                context.getApplicationContext().getContentResolver()
                        .notifyChange(Uri.parse("content://com.xtc.moment.likeMessageProvider/likeMessageunread"), null);
            }
        });
    }

    public static void notifyCommentUnReadNum(final Context context) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "notifyUnReadNum--");
                context.getApplicationContext().getContentResolver()
                        .notifyChange(Uri.parse("content://com.xtc.moment.commentProvider/commentunread"), null);
            }
        });
    }
}