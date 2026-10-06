package com.xtc.moment.util;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.watch.WatchConfigManager;

import java.util.Collection;

/** General helpers of the moment app. */
public class Utils {

    private static final String TAG = "Utils";

    private Utils() {
    }

    /** @return true when [context] is still usable for UI work. */
    public static boolean isContextEffective(Context context) {
        if (context == null) {
            return false;
        }
        if (!(context instanceof Activity)) {
            return true;
        }
        Activity activity = (Activity) context;
        return !(activity.isFinishing() || activity.isDestroyed());
    }

    /** Hides the soft keyboard attached to [view]. */
    public static void hideSoftInputFromWindow(InputMethodManager inputMethodManager, View view) {
        if (inputMethodManager == null || view == null) {
            return;
        }
        try {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            LogUtil.d(TAG, "hideSoftInputFromWindow", e);
        }
    }

    /** Logs the size of [collection]. */
    public static void logSize(String tag, String name, Collection<?> collection) {
        LogUtil.d(tag, name + " = [" + (collection == null ? 0 : collection.size()) + "]");
    }

    /** Logs [moment] on the background thread. */
    public static void logMoment(final String tag, final String prefix, final DbMoment moment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(tag, "logMoment: " + prefix + ", dbMoment = [" + moment + "]");
            }
        });
    }

    /** @return true when [key] points to a local image file. */
    public static boolean isIconKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return key.contains(FileManager.WEBP_FORMAT) || key.contains(FileManager.JPG_FORMAT)
                || key.contains(FileManager.PNG_FORMAT);
    }

    /** Initializes the watch config manager and logs how long it took. */
    public static void initWatchConfigManager(Context context) {
        long startTime = SystemClock.elapsedRealtime();
        WatchConfigManager.init(context);
        LogUtil.d(TAG, "watch config manager init waste: " + (SystemClock.elapsedRealtime() - startTime));
    }
}