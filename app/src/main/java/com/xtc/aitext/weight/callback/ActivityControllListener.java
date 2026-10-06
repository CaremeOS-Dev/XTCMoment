package com.xtc.aitext.weight.callback;

import com.xtc.database.ormlite.CollectionUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Activity 结束事件分发器。
 */
public class ActivityControllListener {

    private static volatile ActivityControllListener instance;

    private List<ActivityFinishCallback> activityFinishCallbacks;

    private ActivityControllListener() {
    }

    public static ActivityControllListener getInstance() {
        if (instance == null) {
            synchronized (ActivityControllListener.class) {
                if (instance == null) {
                    instance = new ActivityControllListener();
                }
            }
        }
        return instance;
    }

    public void addListener(ActivityFinishCallback callback) {
        if (CollectionUtil.isEmpty(this.activityFinishCallbacks)) {
            this.activityFinishCallbacks = new ArrayList<>();
        }
        this.activityFinishCallbacks.add(callback);
    }

    public void removeListener(ActivityFinishCallback callback) {
        if (CollectionUtil.isEmpty(this.activityFinishCallbacks)) {
            return;
        }
        this.activityFinishCallbacks.remove(callback);
    }

    /** 通知所有监听者结束 Activity。 */
    public void finishActivity() {
        if (CollectionUtil.isEmpty(this.activityFinishCallbacks)) {
            return;
        }
        Iterator<ActivityFinishCallback> iterator = this.activityFinishCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().finishActivity();
        }
    }
}