package com.xtc.bigdata.collector.init;

import android.os.Build;
import android.os.Process;

import com.xtc.bigdata.collector.AppLifeCycleCallback;
import com.xtc.bigdata.collector.BehaviorCollector;
import com.xtc.bigdata.collector.ShareHelper;
import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.collector.encapsulation.entity.BaseAttr;
import com.xtc.bigdata.collector.exception.CrashHandler;
import com.xtc.bigdata.collector.utils.QueueUtils;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.bigdata.common.utils.ProcessUtils;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.constants.Constants;

/** Entry point that boots the big-data collector. */
public class InitManager {

    private static final String TAG = "InitManager";

    /** Initializes the collector on the background queue. */
    public static void init() {
        if (ContextUtils.isEmpty()) {
            LogUtil.e(TAG, "error , context = null !");
        } else {
            QueueUtils.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        new BaseAttr().genBaseAttr();
                        InitManager.initOthers();
                        LogUtil.i(TAG, "bigdata init completed !");
                    } catch (Exception e) {
                        LogUtil.e(TAG, "bigdata init error !!!");
                        CrashHandler.getInstance().handleExec(e);
                        try {
                            Thread.sleep(Constants.DEFAULT_INIT_DELAY_TIME);
                        } catch (InterruptedException ignored) {
                        } finally {
                            LogUtil.w(TAG, "kill current process to restart !");
                            Process.killProcess(Process.myPid());
                        }
                    }
                }
            });
        }
    }

    private static void initOthers() {
        LogUtil.i(TAG, "initOthers begin");
        FileUtils.ensureSdCardPath();
        if (Build.VERSION.SDK_INT >= 14) {
            ContextUtils.getContext().registerActivityLifecycleCallbacks(new AppLifeCycleCallback());
        }
        if (ConfigAgent.getBehaviorConfig().crashUsable) {
            CrashHandler.getInstance().registerCrashHandler();
        }
        String curProcessName = ProcessUtils.getCurProcessName(ContextUtils.getContext());
        String hostProcessName = ContextUtils.getContext().getApplicationInfo().processName;
        LogUtil.d(TAG, "curProcessName = " + curProcessName + " , HOST_APP_ID = " + com.xtc.bigdata.common.constants.Constants.HOST_APP_ID + ", PACKAGE_NAME: " + com.xtc.bigdata.common.constants.Constants.PACKAGE_NAME + " , processName = " + hostProcessName);
        if (hostProcessName.equals(curProcessName)) {
            BehaviorCollector.getInstance().appLaunch();
        }
        if (com.xtc.bigdata.common.constants.Constants.deviceType.equals(com.xtc.bigdata.common.constants.Constants.PHONE)) {
            ShareHelper.getInstance().appLaunchNotify();
        }
    }

    private InitManager() {
    }
}