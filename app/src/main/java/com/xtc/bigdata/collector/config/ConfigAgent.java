package com.xtc.bigdata.collector.config;

import com.xtc.bigdata.common.error.ErrorCode;
import com.xtc.log.LogUtil;

/** Holds the global behaviour configuration. */
public class ConfigAgent {

    private static final String TAG = ConfigAgent.class.getName();
    private static BehaviorConfig config;

    public static void saveConfig() {
    }

    public static void setBehaviorConfig(BehaviorConfig behaviorConfig) {
        if (behaviorConfig == null) {
            LogUtil.w(TAG, ErrorCode.CONTROL_NULL_POINTER_BEHAVIOR_CONFIG);
        } else {
            config = behaviorConfig;
            saveConfig();
        }
    }

    public static BehaviorConfig getBehaviorConfig() {
        if (config == null) {
            config = new BehaviorConfig();
        }
        return config;
    }

    private ConfigAgent() {
    }
}