package com.xtc.moment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.util.BroadcastReceiverUtil;

/**
 * 首次绑定手表广播接收器。
 */
public class BindWatchReceiver extends BroadcastReceiver {

    private static final String TAG = "BindWatchReceiver";
    private static final String BIND_WATCH = "com.xtc.initservice.action.FIRST_BIND_WATCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && BIND_WATCH.equals(intent.getAction())) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    MomentApp.refreshAccount();
                }
            });
        }
    }

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BIND_WATCH);
        BroadcastReceiverUtil.registerReceiver(context, new BindWatchReceiver(), filter);
        LogUtil.d(TAG, "register");
    }
}