package com.xtc.assistantapi.launch;

import android.content.Context;
import android.content.Intent;

/**
 * 组件启动抽象。
 */
public interface ComponentLaunch {

    /** 启动 Service。 */
    boolean launchService(Context context, Intent intent, String action);

    /** 启动 Activity。 */
    boolean launchActivity(Context context, Intent intent, String packageName, String className, String action);
}