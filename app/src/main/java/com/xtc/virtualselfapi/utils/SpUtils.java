package com.xtc.virtualselfapi.utils;

import com.xtc.utils.storage.SharedManager;
import com.xtc.virtualselfapi.constants.Constants;

/**
 * 虚拟形象偏好设置读写工具。
 */
public class SpUtils {

    private final SharedManager sharedManager;

    public SpUtils(SharedManager sharedManager) {
        this.sharedManager = sharedManager;
    }

    public int getVersion() {
        return this.sharedManager.getInt(Constants.SpKey.VERSION, 0);
    }

    public void saveVersion(int version) {
        this.sharedManager.putInt(Constants.SpKey.VERSION, version);
    }

    public long getCustomUpdateTime() {
        return this.sharedManager.getLong(Constants.SpKey.CUSTOM_UPDATE_TIME, 0L);
    }

    public void saveCustomUpdateTime(long updateTime) {
        this.sharedManager.putLong(Constants.SpKey.CUSTOM_UPDATE_TIME, updateTime);
    }

    public float getDynamicsScale() {
        return this.sharedManager.getFloat(Constants.SpKey.DYNAMICS_SCALE, 0.0f);
    }

    public void saveDynamicsScale(float scale) {
        this.sharedManager.putFloat(Constants.SpKey.DYNAMICS_SCALE, scale);
    }
}