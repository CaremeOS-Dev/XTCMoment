package com.xtc.web.core.manager;

import android.os.SystemClock;
import android.util.Log;

import com.xtc.web.core.CoreConstants;

/** H5 启动耗时打点，用于统计从 loadUrl 到首屏完成的时间。 */
public class LaunchManager {

    private static final String TAG = CoreConstants.TAG + LaunchManager.class.getSimpleName();
    private static long startLaunchTime;

    /** 记录启动起点。 */
    public static void launch(String url) {
        Log.d(TAG, "start launch url = " + url);
        startLaunchTime = SystemClock.elapsedRealtime();
    }

    /** 打印某个阶段的耗时。 */
    public static void trace(String stage) {
        long costTime = SystemClock.elapsedRealtime() - startLaunchTime;
        Log.i(TAG, stage + " [" + costTime + "] ms");
    }
}