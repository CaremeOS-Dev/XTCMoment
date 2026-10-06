package com.xtc.moment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtc.moment.util.BroadcastReceiverUtil;

/**
 * 头像变更广播接收器基类，支持注册与反注册。
 */
public abstract class IConChangeReceiver extends BroadcastReceiver {

    public static final String ICON_CHANGE_ACTION = "com.xtc.initservice.head.download";
    public static final String DOWNLOAD_STATUS = "download_status";

    boolean registed = false;

    @Override
    public abstract void onReceive(Context context, Intent intent);

    public void register(Context context) {
        if (this.registed) {
            return;
        }
        this.registed = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ICON_CHANGE_ACTION);
        BroadcastReceiverUtil.registerReceiver(context, this, filter);
    }

    public void unRegister(Context context) {
        if (this.registed) {
            BroadcastReceiverUtil.unregisterReceiver(context, this);
            this.registed = false;
        }
    }
}