package com.xtc.moment.util;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.constants.FunSwitchConstant;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.WatchAccountBase;

/**
 * 趣味拍照功能可用性判断。
 */
public class FunPhotoUtils {

    private static final String TAG = "FunPhotoUtils";

    private static final int OPEN = 0;
    private static final int CLOSE = 1;

    public static boolean isAvailable(Context context) {
        boolean moduleSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(context,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_FUN_PHOTO_IS_AVAILABLE, false);
        boolean funSwitch = WatchAccountBase.queryFunSwitchByPackageName(context, FunSwitchConstant.FUN_PHOTO, false)
                .getSwitchStatus().intValue() == OPEN;
        LogUtil.d(TAG, "isAvailable: moduleSwitch = " + moduleSwitch + "; funSwitch = " + funSwitch + ";   context = " + context);
        return moduleSwitch && funSwitch;
    }
}