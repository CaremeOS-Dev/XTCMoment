package com.xtc.moment.serve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtc.log.LogUtil;
import com.xtc.moment.util.BroadcastReceiverUtil;

/**
 * 电池充电状态监听服务（单例）。
 */
public class BatteryServe {

    public static final String TAG = BatteryServe.class.getSimpleName();
    private static volatile BatteryServe serve = new BatteryServe();

    private BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!"android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                return;
            }
            BatteryServe.this.dealBatteryChanged(intent);
        }
    };
    private BatteryChangeCallback callback;
    private volatile boolean isRegisterReceiver;

    public interface BatteryChangeCallback {
        void onBatteryChange(boolean charging);
    }

    private BatteryServe() {
    }

    public static BatteryServe getInstance() {
        return serve;
    }

    public void registerBatteryChange(Context context, BatteryChangeCallback batteryChangeCallback) {
        if (batteryChangeCallback != null) {
            this.callback = batteryChangeCallback;
        }
        if (this.isRegisterReceiver) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.setPriority(1000);
        BroadcastReceiverUtil.registerReceiver(context, this.batteryBroadcastReceiver, intentFilter);
        this.isRegisterReceiver = true;
    }

    public void unRegisterBatteryChange(Context context) {
        this.callback = null;
        if (this.isRegisterReceiver) {
            BroadcastReceiverUtil.unregisterReceiver(context, this.batteryBroadcastReceiver);
            this.isRegisterReceiver = false;
            LogUtil.w(TAG, "unRegisterBatteryChange is complete");
        }
    }

    private void dealBatteryChanged(Intent intent) {
        int plugged = intent.getIntExtra("plugged", -1);
        boolean charging = plugged == 2 || plugged == 1;
        LogUtil.i(TAG, "dealBatteryChanged  plugged:" + plugged);
        BatteryChangeCallback batteryChangeCallback = this.callback;
        if (batteryChangeCallback != null) {
            batteryChangeCallback.onBatteryChange(charging);
        }
    }
}