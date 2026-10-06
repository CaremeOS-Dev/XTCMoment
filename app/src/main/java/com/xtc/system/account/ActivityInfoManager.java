package com.xtc.system.account;

import android.app.Activity;

import com.xtc.log.LogUtil;

import java.util.LinkedList;

/** Tracks the stack of created activities and exposes the current top activity. */
public class ActivityInfoManager {

    private static final String TAG = "ActivityInfoManager";

    private static ActivityInfoManager instance;

    private final LinkedList<ActivityInfo> activityInfoQueue = new LinkedList<>();

    /** Simple record of a created activity. */
    public class ActivityInfo {

        private final String name;

        public ActivityInfo(String name) {
            this.name = name;
        }
    }

    private ActivityInfoManager() {
    }

    /** @return the shared singleton. */
    public static ActivityInfoManager getInstance() {
        if (instance == null) {
            instance = new ActivityInfoManager();
        }
        return instance;
    }

    private ActivityInfo createActivityInfo(Activity activity) {
        return new ActivityInfo(activity.getClass().getSimpleName());
    }

    /** Pushes the activity onto the top of the stack. */
    public synchronized void create(Activity activity) {
        ActivityInfo activityInfo = createActivityInfo(activity);
        activityInfoQueue.push(activityInfo);
        LogUtil.i(TAG, "create [" + activityInfo.name + "] success.");
    }

    /** No-op hook kept for lifecycle symmetry. */
    public synchronized void resume(Activity activity) {
    }

    /** No-op hook kept for lifecycle symmetry. */
    public synchronized void pause(Activity activity) {
    }

    /** Pops the activity from the stack when it is the current top. */
    public synchronized void destroy(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        ActivityInfo top = activityInfoQueue.peek();
        if (top.name.equals(simpleName)) {
            LogUtil.i(TAG, "destroy [" + activityInfoQueue.pop().name + "] success.");
        } else {
            LogUtil.e(TAG, "destroy [" + simpleName + "] fail,activityName:" + simpleName + ",activityInfoQueue.peek():" + top.name);
        }
    }

    /** @return the name of the current top activity, or null. */
    public synchronized String getTop() {
        ActivityInfo top = activityInfoQueue.peek();
        if (top != null) {
            LogUtil.i(TAG, "top activity is [" + top.name + "]");
            return top.name;
        }
        LogUtil.e(TAG, "top activity is null.");
        return null;
    }

    /** @return true when {@code activity} is the current top activity. */
    public synchronized boolean isTop(Activity activity) {
        String simpleName = activity.getClass().getSimpleName();
        ActivityInfo top = activityInfoQueue.peek();
        if (top != null) {
            if (simpleName.equals(top.name)) {
                LogUtil.i(TAG, "top activity is [" + top.name + "]");
                return true;
            }
        } else {
            LogUtil.e(TAG, "top activity is null.");
        }
        return false;
    }
}