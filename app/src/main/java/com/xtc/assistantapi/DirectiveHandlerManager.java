package com.xtc.assistantapi;

import android.util.Log;

import com.xtc.assistantapi.core.RootDirectiveHandler;
import com.xtc.assistantapi.launch.ComponentLaunch;
import com.xtc.assistantapi.launch.DefaultComponentLaunch;

/**
 * 指令处理器管理器，持有全局根处理器与组件启动器。
 */
public class DirectiveHandlerManager {

    private static final String TAG = LogTag.of("DirectiveHandlerManager");

    private static RootDirectiveHandler rootDirectiveHandler;
    private static ComponentLaunch componentLaunch;

    private DirectiveHandlerManager() {
    }

    /** 初始化根处理器。 */
    public static void init(RootDirectiveHandler handler) {
        if (rootDirectiveHandler == null) {
            rootDirectiveHandler = handler;
        } else {
            Log.d(TAG, "rootDirectiveHandler 已初始化过");
        }
    }

    public static RootDirectiveHandler getRootDirectiveHandler() {
        if (rootDirectiveHandler == null) {
            Log.d(TAG, "请先调用init初始化UriRouter");
        }
        return rootDirectiveHandler;
    }

    public static ComponentLaunch getComponentLaunch() {
        if (componentLaunch == null) {
            componentLaunch = new DefaultComponentLaunch();
        }
        return componentLaunch;
    }

    public static void setComponentLaunch(ComponentLaunch launch) {
        componentLaunch = launch;
    }
}