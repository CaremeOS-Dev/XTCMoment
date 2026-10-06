package com.xtc.bigdata.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xtc.log.LogUtil;

/**
 * 异步广播接收器基类，使用 goAsync 把处理逻辑放到后台线程。
 */
public abstract class AbsAsyncBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "AsyncBroadcastReceiver";

    protected abstract void onReceiveAsync(Context context, Intent intent);

    @Override
    public final void onReceive(final Context context, final Intent intent) {
        final PendingResult pendingResult = goAsync();
        if (pendingResult == null) {
            LogUtil.w(TAG, "goAsync failure");
            onReceiveAsync(context, intent);
        } else {
            ExecutorProvider.background().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onReceiveAsync(context, intent);
                    } finally {
                        pendingResult.finish();
                    }
                }
            });
        }
    }
}