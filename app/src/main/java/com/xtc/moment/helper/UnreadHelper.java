package com.xtc.moment.helper;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.module.main.MomentActivity;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ReddotUtil;

/**
 * 未读红点与未读数处理。
 */
public class UnreadHelper {

    private static final String TAG = "XTC_MOMENT_UnreadHelper";

    private static volatile UnreadHelper instance;

    private final Context mContext;
    private boolean hasNewMoment;

    public static UnreadHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (UnreadHelper.class) {
                if (instance == null) {
                    instance = new UnreadHelper(context);
                }
            }
        }
        return instance;
    }

    public UnreadHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void dealUnreadPoint() {
        long uncheckedCount = MomentServeImpl.getInstance(this.mContext)
                .getOthersUncheckedMomentCount(MomentApp.getWatchId());
        LogUtil.i(TAG, "dealUnreadPoint: uncheckedCount = " + uncheckedCount);
        if (uncheckedCount > 0) {
            showUnreadPoint();
        } else {
            hideUnreadPoint();
        }
    }

    public void showUnreadPoint() {
        ReddotUtil.on(this.mContext);
        this.hasNewMoment = true;
    }

    public void hideUnreadPoint() {
        ReddotUtil.hide(this.mContext);
        this.hasNewMoment = false;
    }

    public void refreshRedPoint() {
        LogUtil.d(TAG, "当前是否有未刷新的动态: " + this.hasNewMoment);
        if (this.hasNewMoment) {
            ReddotUtil.on(this.mContext);
        }
    }

    public void showUnreadNumber() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                showNumber();
            }
        });
    }

    private void showNumber() {
        String watchId = MomentApp.getWatchId();
        int unreadCount = (int) (MomentServeImpl.getInstance(this.mContext)
                .getUncheckedLikeMessageCountByWatchId(watchId)
                + MomentServeImpl.getInstance(this.mContext).getUncheckedCommentCountByWatchId(watchId));
        LogUtil.i(TAG, "sendUnreadNumber: unread sum = " + unreadCount);
        if (unreadCount < 0) {
            unreadCount = 0;
        }
        ReddotUtil.broadcastUnreadMessageNumber(this.mContext, unreadCount, MomentActivity.class.getName());
    }
}