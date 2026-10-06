package com.xtc.web.core.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtc.log.LogUtil;
import com.xtc.web.core.CoreConstants;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.BatteryInfo;
import com.xtc.web.core.data.resp.RespBatteryInfo;

/** 电量管理器：注册电池广播后把最新电量回调给 H5。 */
public class BatteryInfoManager {

    private static final String TAG = CoreConstants.TAG + BatteryInfoManager.class.getSimpleName();
    private static BatteryInfoManager instance;

    private BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                LogUtil.e(TAG, "don't care");
            } else {
                dealBatteryChanged(intent);
            }
        }
    };

    private Context context;
    private CompletionHandler<RespBatteryInfo> handler;

    public static BatteryInfoManager getInstance(Context context) {
        if (instance == null) {
            instance = new BatteryInfoManager(context);
        }
        return instance;
    }

    public BatteryInfoManager(Context context) {
        this.context = context;
    }

    /** 解析电池广播并回调结果，随后注销广播。 */
    private void dealBatteryChanged(Intent intent) {
        int level = (int) ((intent.getIntExtra("level", 0) * 100.0f) / intent.getIntExtra("scale", 100));
        int plugged = intent.getIntExtra("plugged", -1);
        int temperature = intent.getIntExtra("temperature", -1);
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.setLevel(level);
        batteryInfo.setPlugged(plugged);
        batteryInfo.setTemperature(temperature);
        RespBatteryInfo response = new RespBatteryInfo();
        response.setData(batteryInfo);
        response.setCode(RespBatteryInfo.Code.SUCCESS);
        this.handler.complete(response);
        unRegisterReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.setPriority(1000);
        this.context.registerReceiver(this.batteryBroadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        this.context.unregisterReceiver(this.batteryBroadcastReceiver);
    }

    public synchronized void getBatteryInfo(CompletionHandler<RespBatteryInfo> completionHandler) {
        this.handler = completionHandler;
        registerReceiver();
    }
}