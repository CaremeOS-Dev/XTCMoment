package com.xtc.moment.util;

import android.content.Context;
import android.content.Intent;

import com.xtc.system.account.constant.AppUnreadConstants;

/**
 * 应用红点与未读数广播工具。
 */
public class ReddotUtil {

    private static void toSwitch(Context context, boolean show) {
        Intent intent = new Intent();
        intent.setAction(AppUnreadConstants.ACTION_UNREAD_POINT_CHANGED);
        intent.putExtra(AppUnreadConstants.EXTRA_UNREAD_POINT, show);
        intent.putExtra(AppUnreadConstants.EXTRA_UNREAD_PACKAGE_NAME, context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static void on(Context context) {
        toSwitch(context, true);
    }

    public static void hide(Context context) {
        toSwitch(context, false);
    }

    public static void broadcastUnreadMessageNumber(Context context, int unreadNumber, String activity) {
        Intent intent = new Intent();
        intent.setAction(AppUnreadConstants.ACTION_UNREAD_NUMBER_CHANGED);
        intent.putExtra(AppUnreadConstants.EXTRA_UNREAD_NUMBER, unreadNumber);
        intent.putExtra(AppUnreadConstants.EXTRA_UNREAD_PACKAGE_NAME, context.getPackageName());
        if (activity != null) {
            intent.putExtra(AppUnreadConstants.EXTRA_UNREAD_ACTIVITY, activity);
        }
        context.sendBroadcast(intent);
    }
}