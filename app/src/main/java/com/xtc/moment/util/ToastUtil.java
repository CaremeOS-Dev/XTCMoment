package com.xtc.moment.util;

import android.content.Context;
import android.widget.Toast;

import com.xtc.bigdata.collector.utils.MainHandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/** Toast helpers that always run on the main thread. */
public class ToastUtil {

    private static final String TAG = "ToastUtil";

    private static Toast toast;

    private ToastUtil() {
    }

    public static void showShort(Context context, final String message) {
        if (context == null) {
            LogUtil.d(TAG, "showShort: context == null");
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        LogUtil.d(TAG, "showShort: msg = [" + message + "]");
        if (ThreadCheck.isMainThread()) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
        } else {
            MainHandlerUtil.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showShort(Context context, int resId) {
        if (context == null) {
            LogUtil.d(TAG, "showShort: context == null");
            return;
        }
        showShort(context, context.getString(resId));
    }

    public static void showNoConnected(Context context) {
        showShort(context, R.string.net_work_exception);
    }

    public static void showLong(Context context, final String message) {
        if (context == null) {
            LogUtil.d(TAG, "showShort: context == null");
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        LogUtil.d(TAG, "showLong: msg = [" + message + "]");
        if (ThreadCheck.isMainThread()) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show();
        } else {
            MainHandlerUtil.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void showShortCover(Context context, int resId) {
        if (context == null) {
            LogUtil.d(TAG, "showShortCover: context == null");
            return;
        }
        showShortCover(context, context.getString(resId));
    }

    /** Reuses a single toast so consecutive messages replace each other. */
    public static void showShortCover(Context context, final String message) {
        if (context == null) {
            LogUtil.d(TAG, "showShortCover: context == null");
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        LogUtil.d(TAG, "showShortCover: msg = " + message + "; context = " + applicationContext + ";toast = " + toast
                + ";thread = " + Thread.currentThread());
        if (ThreadCheck.isMainThread()) {
            showShortCoverToast(applicationContext, message);
        } else {
            MainHandlerUtil.post(new Runnable() {
                @Override
                public void run() {
                    showShortCoverToast(applicationContext, message);
                }
            });
        }
    }

    private static void showShortCoverToast(Context context, String message) {
        try {
            if (toast != null) {
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                toast.setText(message);
            } else {
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "showShortCoverToast: " + e);
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}