package com.xtc.moment.util;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;

/**
 * 动态模块开关封装。
 */
public class ModuleSwitch {

    private static final String TAG = "ModuleSwitch";

    public static boolean isContentSupervision(Context context) {
        boolean enabled = ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_CONTENT_SUPERVISION, false);
        LogUtil.d(TAG, "好友圈发布内容管控打开吗 ：" + enabled);
        return enabled;
    }

    public static boolean getDressHeadSwitch(Context context) {
        if (context == null) {
            return false;
        }
        boolean enabled = ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_SWITCH_DRESS_HEAD, false);
        LogUtil.i(TAG, "isDisplayDressHead: " + enabled);
        return enabled;
    }
}