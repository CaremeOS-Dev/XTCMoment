package com.xtc.moment.module.illegal.state;

import android.content.Context;

import com.xtc.log.LogUtil;

/**
 * 违规状态基类。
 */
public abstract class IllegalBaseState {

    private static final String TAG = "IllegalBaseState";

    private final Context mContext;
    private long endTime;

    public abstract int getCurrentState();

    protected abstract String getStateName();

    public IllegalBaseState(Context context) {
        this.mContext = context;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void into() {
        LogUtil.d(TAG, "Illegal state into:" + getStateName());
    }

    public void leave() {
        LogUtil.d(TAG, "Illegal state leave:" + getStateName());
    }
}