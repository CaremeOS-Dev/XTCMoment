package com.xtc.assistantapi.launch;

import android.content.Context;
import android.content.Intent;

/**
 * 默认组件启动实现，直接使用 Context 启动。
 */
public class DefaultComponentLaunch implements ComponentLaunch {

    @Override
    public boolean launchActivity(Context context, Intent intent, String packageName, String className, String action) {
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean launchService(Context context, Intent intent, String action) {
        context.startService(intent);
        return true;
    }
}