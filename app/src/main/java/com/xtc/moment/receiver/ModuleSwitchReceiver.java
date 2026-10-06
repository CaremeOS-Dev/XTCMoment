package com.xtc.moment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.tasks.start.InitSwitchTask;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.wearswitch.function.FunSwitchUtil;

import java.util.Objects;

/**
 * 模块开关/功能开关更新广播接收器。
 */
public class ModuleSwitchReceiver extends BroadcastReceiver {

    private static final String TAG = "ModuleSwitchReceiver";
    private static final String ACTION = "com.xtc.launcher.moduleswitch.INIT_ACTION";
    private static final String FUN_INIT_ACTION = "com.xtc.funmanager.FUN_DATA_INIT_COMPLETE";
    private static final int FUN_SWITCH = 1;
    private static final int MODULE_SWITCH = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        LogUtil.d(TAG, "action = " + action);
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (Objects.equals(ACTION, action)) {
            LogUtil.i(TAG, "好友圈收到全网开关更新广播，更新全网开关缓存");
            refreshSwitchCache(context, MODULE_SWITCH);
        } else if (Objects.equals(FUN_INIT_ACTION, action)) {
            LogUtil.i(TAG, "好友圈收到功能开关更新广播，更新功能开关缓存");
            refreshSwitchCache(context, FUN_SWITCH);
        }
    }

    public void refreshSwitchCache(final Context context, final int switchType) {
        if (context == null) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                InitSwitchTask initSwitchTask = new InitSwitchTask(context.getApplicationContext());
                if (switchType == FUN_SWITCH) {
                    FunSwitchUtil.refresh();
                    initSwitchTask.loadFunSwitch();
                } else if (switchType == MODULE_SWITCH) {
                    ModuleSwitchUtil.clearCache();
                    initSwitchTask.loadModuleSwitch();
                }
            }
        });
    }

    public static void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        filter.addAction(FUN_INIT_ACTION);
        context.registerReceiver(new ModuleSwitchReceiver(), filter);
        LogUtil.d(TAG, "register");
    }
}