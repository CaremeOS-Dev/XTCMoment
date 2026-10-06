package com.xtc.moment.module.illegal.config;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.encode.JSONUtil;

/**
 * 违规处罚配置读取：从模块开关与开关附加信息里解析出处罚配置。
 */
public class IllegalConfigHandler {

    private static final String TAG = "IllegalConfigHandler";

    public static IllegalSanctionConfig obtainConfig(Context context) {
        if (context == null) {
            return null;
        }
        boolean enableIllegalPunish = ModuleSwitchUtil.queryModuleSwitchByBoolean(context.getApplicationContext(),
                ModuleSwitchConstant.MODULE_ILLEGAL_PUNISH, false);
        String moduleConfig = WatchAccountBase.queryModuleSwitchExtraByInt(context.getApplicationContext(),
                ModuleSwitchConstant.MODULE_ILLEGAL_PUNISH, "");
        LogUtil.d(TAG, "obtainConfig: isSupportIllegalPunish=" + enableIllegalPunish);
        LogUtil.d(TAG, "obtainConfig: moduleConfig=" + moduleConfig);
        IllegalSanctionConfigBean configBean = TextUtils.isEmpty(moduleConfig)
                ? null : JSONUtil.fromJSON(moduleConfig, IllegalSanctionConfigBean.class);
        if (configBean == null) {
            configBean = new IllegalSanctionConfigBean();
        }
        return new IllegalSanctionConfig.Builder()
                .setEnableReview(enableIllegalPunish)
                .setAllowCount(configBean.getAllowCount())
                .setDelaySendDeadLine(configBean.getDelaySendDeadLine())
                .setDelaySendTime(configBean.getDelaySendTime())
                .setDisableSendDeadLine(configBean.getDisableSendDeadLine())
                .setIllegalRecordDeadLine(configBean.getIllegalRecordDeadLine())
                .setPullIllegalCount(configBean.getPullIllegalCount())
                .build();
    }
}