package com.xtc.moment.module.theme;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * 全局主题背景注入：在 Activity 创建与回到前台时把当前主题背景设置到窗口上。
 *
 * <p>{@code onActivityResumed} 处的重刷是必要的：部分页面（如好友圈首页）会在权限回调里才
 * 调用 {@code setContentView}，会覆盖创建阶段设置的结果。
 */
public class ThemeActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ThemeBackgroundHelper.applyIfEnabled(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ThemeBackgroundHelper.applyIfEnabled(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
