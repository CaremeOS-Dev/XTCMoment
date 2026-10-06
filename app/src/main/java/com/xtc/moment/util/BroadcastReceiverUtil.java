package com.xtc.moment.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import com.xtc.log.LogUtil;

/**
 * 广播注册/反注册工具，低版本走主线程、高版本走后台线程避免 ANR。
 */
public class BroadcastReceiverUtil {

    private static final String TAG = "BroadcastReceiverUtil";

    public static void registerReceiver(final Context context, final BroadcastReceiver receiver,
            final IntentFilter filter) {
        if (isNeedAsync()) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    registerReceiverSync(context, receiver, filter);
                }
            }, true);
        } else {
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    registerReceiverSync(context, receiver, filter);
                }
            });
        }
    }

    public static void registerReceiverSync(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver == null) {
            LogUtil.d(TAG, "registerReceiverSync context is null");
            return;
        }
        if (filter == null) {
            LogUtil.d(TAG, "registerReceiverSync intentFilter is null");
            return;
        }
        if (checkContext(context)) {
            context.registerReceiver(receiver, filter);
            LogUtil.d(TAG, "registerReceiverSync : receiverAction = ["
                    + (filter.countActions() > 0 ? filter.getAction(0) : "") + "]");
        }
    }

    public static void unregisterReceiver(final Context context, final BroadcastReceiver receiver) {
        if (isNeedAsync()) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    unregisterReceiverSync(context, receiver);
                }
            }, true);
        } else {
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    unregisterReceiverSync(context, receiver);
                }
            });
        }
    }

    public static void unregisterReceiverSync(Context context, BroadcastReceiver receiver) {
        if (receiver == null) {
            LogUtil.d(TAG, "unregisterReceiverSync receiver is null");
            return;
        }
        if (context == null) {
            LogUtil.d(TAG, "unregisterReceiverSync context is null");
            return;
        }
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            LogUtil.e(TAG, "unregisterReceiver", e);
        }
    }

    public static boolean checkContext(Context context) {
        if (context == null) {
            LogUtil.d(TAG, "context is null");
            return false;
        }
        if (!(context instanceof Activity)) {
            return true;
        }
        Activity activity = (Activity) context;
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            return true;
        }
        LogUtil.d(TAG, "activity is isDestroy");
        return false;
    }

    private static boolean isNeedAsync() {
        return Build.VERSION.SDK_INT >= 23;
    }
}