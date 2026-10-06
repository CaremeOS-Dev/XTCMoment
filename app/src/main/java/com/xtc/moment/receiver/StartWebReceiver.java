package com.xtc.moment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.util.SharedTool;

/**
 * Web 进程启动完成广播接收器。
 */
public class StartWebReceiver extends BroadcastReceiver {

    public static final String ACTION_FLAG_START_WEB_PROCESS = "com.xtc.moment.startProcess";

    private static final String TAG = "StartWebReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        LogUtil.d(TAG, "action = " + action);
        if (!TextUtils.isEmpty(action) && ACTION_FLAG_START_WEB_PROCESS.equals(action)) {
            SharedTool.saveIsRunning(context, false);
        }
    }
}