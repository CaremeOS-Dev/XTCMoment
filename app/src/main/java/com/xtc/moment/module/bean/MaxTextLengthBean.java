package com.xtc.moment.module.bean;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.encode.JSONUtil;

/**
 * 单例缓存动态文本的最大字数，字数从模块开关的附加配置里读取。
 */
public class MaxTextLengthBean {

    private static final String MAX_LENGTH_KEY = "maxLength";
    private static final String TAG = "MaxTextLengthBean";
    private static volatile MaxTextLengthBean instance = null;
    private static int maxLength = 40;

    private boolean isLoad;

    private MaxTextLengthBean() {
    }

    public static MaxTextLengthBean getInstance(Context context) {
        if (instance == null) {
            synchronized (MaxTextLengthBean.class) {
                if (instance == null) {
                    getMaxLengthFromModule(context);
                    instance = new MaxTextLengthBean();
                }
            }
        }
        return instance;
    }

    public int getMaxLength() {
        return maxLength;
    }

    private static void getMaxLengthFromModule(Context context) {
        String extra = WatchAccountBase.queryModuleSwitchExtraByInt(context,
                ModuleSwitchConstant.MODULE_SWITCH_TEXT_LENGTH, "");
        if (TextUtils.isEmpty(extra)) {
            return;
        }
        maxLength = ((Integer) JSONUtil.getJSONValue(extra, MAX_LENGTH_KEY)).intValue();
        LogUtil.d(TAG, "getMaxLength: maxLength = " + maxLength);
    }
}