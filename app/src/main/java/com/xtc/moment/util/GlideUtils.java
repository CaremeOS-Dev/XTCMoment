package com.xtc.moment.util;

import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.xtc.log.LogUtil;

/**
 * Glide 请求开关工具：在页面不可见时暂停图片加载。
 */
public class GlideUtils {

    private static final String TAG = "GlideUtils";

    public static void setImageDelayedLoad(Context context, int state) {
        if (context == null) {
            return;
        }
        try {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
            }
            if (state != 0 && state != 1) {
                Glide.with(context).resumeRequests();
                return;
            }
            Glide.with(context).pauseRequests();
        } catch (Exception e) {
            LogUtil.e(TAG, "setImageDelayedLoad: ", e);
        }
    }
}