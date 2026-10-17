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
            // state 是 RecyclerView 的滚动状态：0 = 停止、1 = 手指拖动、2 = 惯性滑动。
            // 只有彻底停下来（0）才恢复加载；拖动和惯性滑动期间一律暂停，避免快速滑动时
            // 把图片请求堆在后台。之前 0/1 走暂停、2 走恢复，导致一停手就被暂停，
            // 停下来之后新绑定的卡片永远等不到加载，必须再滑一下（进入 2）才会恢复。
            if (state == 0) {
                Glide.with(context).resumeRequests();
                return;
            }
            Glide.with(context).pauseRequests();
        } catch (Exception e) {
            LogUtil.e(TAG, "setImageDelayedLoad: ", e);
        }
    }
}