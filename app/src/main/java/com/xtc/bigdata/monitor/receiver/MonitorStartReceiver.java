package com.xtc.bigdata.monitor.receiver;

import android.content.Context;
import android.content.Intent;

import com.xtc.bigdata.collector.encapsulation.entity.BaseAttr;
import com.xtc.bigdata.common.utils.AbsAsyncBroadcastReceiver;
import com.xtc.bigdata.common.utils.SystemInfoUtils;
import com.xtc.bigdata.monitor.anr.WatchDogs;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.SystemProperty;

/** 接收外部广播以启动/停止 WatchDogs，并响应区域变更广播刷新基础属性。 */
public class MonitorStartReceiver extends AbsAsyncBroadcastReceiver {

    private static final String CHANGE_REGION_ACTION = "com.xtc.launcher.ACTION_CHANGE_REGION";
    private static final String TAG = "WatchDogs";
    public static final String WATCHDOGS = "com.xtc.watchdogs";

    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_FORCE = "force";
    private static final String EXTRA_TIMEOUT = "timeout";

    @Override
    protected void onReceiveAsync(Context context, Intent intent) {
        try {
            dealBroadcast(context, intent);
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }

    private void dealBroadcast(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(TAG, "action = " + action);
        if (action == null) {
            return;
        }
        if (WATCHDOGS.equals(action)) {
            dealWatchdogs(intent);
        } else if (CHANGE_REGION_ACTION.equals(action)) {
            new BaseAttr().genBaseAttr();
        } else {
            LogUtil.i(TAG, "do not care action = " + action);
        }
    }

    private void dealWatchdogs(Intent intent) {
        if (intent.getIntExtra(EXTRA_TYPE, 0) == 0) {
            WatchDogs.exit();
            return;
        }
        int forceStart = intent.getIntExtra(EXTRA_FORCE, 0);
        LogUtil.d(TAG, "forceStart = " + forceStart);
        if (forceStart != 0 || isSystemTypeAllow()) {
            int customTimeout = intent.getIntExtra(EXTRA_TIMEOUT, -1);
            LogUtil.d(TAG, "custom minTimeout = " + customTimeout);
            startWatchDogs(customTimeout);
        } else {
            LogUtil.d(TAG, "system type not allow start , return !");
        }
    }

    /** 仅 debug 版系统允许在未强制指定时启动 WatchDogs。 */
    private boolean isSystemTypeAllow() {
        String systemType = SystemInfoUtils.getSystemProperty(SystemProperty.BUILD_TYPE);
        boolean isDebug = systemType != null && systemType.toLowerCase().contains("debug");
        LogUtil.d(TAG, "system type = " + systemType + " , isDebug = " + isDebug);
        return isDebug;
    }

    private void startWatchDogs(int minTimeout) {
        WatchDogs.getInstance(minTimeout).start();
    }

    /** 调试用的自定义线程，仅打印启动/运行日志。 */
    static class CustomThread extends Thread {

        @Override
        public synchronized void start() {
            System.out.println("start:" + getName() + ":" + getId());
            super.start();
        }

        @Override
        public void run() {
            super.run();
            System.out.println("run:" + getName() + ":" + getId());
        }
    }
}