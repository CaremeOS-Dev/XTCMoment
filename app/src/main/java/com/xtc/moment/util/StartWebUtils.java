package com.xtc.moment.util;

import android.content.Context;
import android.content.Intent;

import com.xtc.log.LogUtil;
import com.xtc.moment.share.view.ShowH5Activity;
import com.xtc.moment.share.view.SinglePShowH5Activity;

/**
 * H5 页面启动入口：带启动频率保护。
 */
public class StartWebUtils {

    private static final String TAG = "StartWebUtils";

    public static void startH5Activity(final Context context, final String url) {
        if (SharedTool.getIsRunning(context)) {
            LogUtil.d(TAG, "too fast start web!");
            return;
        }
        SharedTool.saveIsRunning(context, true);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (LaunchWebViewUtil.isLaunchMultiProcess()) {
                    intent = new Intent(context, ShowH5Activity.class);
                } else {
                    intent = new Intent(context, SinglePShowH5Activity.class);
                }
                intent.putExtra(ShowH5Activity.H5_URL, url);
                context.startActivity(intent);
            }
        });
    }
}