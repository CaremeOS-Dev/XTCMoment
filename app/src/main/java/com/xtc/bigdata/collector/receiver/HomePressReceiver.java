package com.xtc.bigdata.collector.receiver;

import android.content.Context;
import android.content.Intent;

import com.xtc.bigdata.common.utils.AbsAsyncBroadcastReceiver;

/**
 * Home 键广播接收器，把 home 键事件回调给监听者。
 */
public class HomePressReceiver extends AbsAsyncBroadcastReceiver {

    private static final String SYSTEM_HOME_KEY = "homekey";
    private static final String SYSTEM_REASON = "reason";

    private HomePressListener mListener;

    public interface HomePressListener {
        void onHomePressed();
    }

    public HomePressReceiver(HomePressListener listener) {
        this.mListener = null;
        this.mListener = listener;
    }

    @Override
    public void onReceiveAsync(Context context, Intent intent) {
        if (!"android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
            return;
        }
        String reason = intent.getStringExtra(SYSTEM_REASON);
        if (reason == null || !reason.equals(SYSTEM_HOME_KEY)) {
            return;
        }
        HomePressListener listener = this.mListener;
        if (listener == null) {
            return;
        }
        listener.onHomePressed();
    }
}