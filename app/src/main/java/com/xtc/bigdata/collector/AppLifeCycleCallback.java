package com.xtc.bigdata.collector;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;

import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

/** Activity lifecycle callback feeding page-duration tracking and session refresh. */
public class AppLifeCycleCallback implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "BCLifecycleCallback";

    private final Map<String, Long> activityMap = new HashMap<>();

    public AppLifeCycleCallback() {
        activityMap.clear();
        LogUtil.i(TAG, "初始化参数");
    }

    @Override
    public synchronized void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity != null) {
            String name = activity.getClass().getName();
            LogUtil.i(TAG, "==================onActivityCreated=====================" + name);
            if (!activityMap.containsKey(name)) {
                activityMap.put(name, SystemClock.elapsedRealtime());
            }
        }
    }

    @Override
    public synchronized void onActivityResumed(Activity activity) {
        if (activity != null) {
            String name = activity.getClass().getName();
            LogUtil.i(TAG, "==================onActivityResumed=====================" + name);
            if (!activityMap.containsKey(name)) {
                activityMap.put(name, SystemClock.elapsedRealtime());
            } else if (SystemClock.elapsedRealtime() - activityMap.get(name) > ConfigAgent.getBehaviorConfig().sessionTimeout) {
                SessionAgent.refreshSessionId();
            }
            if (ConfigAgent.getBehaviorConfig().openActivityDurationTrack) {
                BehaviorCollector.getInstance().pageBegin(name);
            }
        }
    }

    @Override
    public synchronized void onActivityPaused(Activity activity) {
        if (activity != null) {
            String name = activity.getClass().getName();
            LogUtil.i(TAG, "==================onActivityPaused=====================" + name);
            if (activityMap.containsKey(name)) {
                activityMap.remove(name);
            }
            if (ConfigAgent.getBehaviorConfig().openActivityDurationTrack) {
                BehaviorCollector.getInstance().pageEnd(name);
            }
        }
    }

    @Override
    public synchronized void onActivityDestroyed(Activity activity) {
        if (activity != null) {
            String name = activity.getClass().getName();
            LogUtil.i(TAG, "==================onActivityDestroyed=====================" + name);
            if (activityMap.containsKey(name)) {
                activityMap.remove(name);
            }
            if (activityMap.size() <= 0) {
                if (Constants.deviceType.equals(Constants.PHONE)) {
                    ShareHelper.getInstance().appExitNotify();
                }
                SessionAgent.clean();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle savedInstanceState) {
        LogUtil.i(TAG, "==================onActivitySaveInstanceState=====================" + activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtil.i(TAG, "==================onActivityStarted=====================" + activity.getClass().getName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtil.i(TAG, "==================onActivityStopped=====================" + activity.getClass().getName());
    }
}