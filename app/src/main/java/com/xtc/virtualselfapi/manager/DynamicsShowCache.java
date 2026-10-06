package com.xtc.virtualselfapi.manager;

import com.xtc.log.LogUtil;

/**
 * 动态装扮缩放倍数缓存。
 */
public class DynamicsShowCache {

    private static final String TAG = "Virtual_Self_Api_DynamicsShowCache";

    private static float scale;

    public static void setScale(float scale) {
        LogUtil.d(TAG, "设置动态装扮放大倍数：" + scale);
        DynamicsShowCache.scale = scale;
    }

    public static float getScale() {
        float current = scale;
        if (current <= 0.0f) {
            return 0.8f;
        }
        return current;
    }
}