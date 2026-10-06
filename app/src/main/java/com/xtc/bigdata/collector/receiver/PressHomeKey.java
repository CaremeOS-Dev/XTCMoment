package com.xtc.bigdata.collector.receiver;

import android.content.IntentFilter;

import com.xtc.bigdata.collector.ShareHelper;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.error.ErrorCode;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.log.LogUtil;

/**
 * Home 键监听注册器（单例）。
 */
public class PressHomeKey {

    private static final String TAG = PressHomeKey.class.getName();

    private final HomePressReceiver homePressReceiver;
    private boolean isRegister;

    private static class InstanceHolder {
        private static final PressHomeKey mInstance = new PressHomeKey();

        private InstanceHolder() {
        }
    }

    public static PressHomeKey getInstance() {
        return InstanceHolder.mInstance;
    }

    public void registerHomePress() {
        if (ContextUtils.isEmpty()) {
            LogUtil.w(TAG, ErrorCode.REPORT_HOME_KEY_REG);
            return;
        }
        try {
            if (this.isRegister) {
                return;
            }
            ContextUtils.getContext().registerReceiver(this.homePressReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            this.isRegister = true;
        } catch (Exception e) {
            LogUtil.w(TAG, e.toString());
            LogUtil.w(TAG, ErrorCode.REPORT_HOME_KEY_REG);
        }
    }

    public void unRegisterHomePress() {
        if (ContextUtils.isEmpty()) {
            LogUtil.w(TAG, ErrorCode.REPORT_HOME_KEY_UNREG);
            return;
        }
        try {
            if (this.isRegister) {
                ContextUtils.getContext().unregisterReceiver(this.homePressReceiver);
                this.isRegister = false;
            }
        } catch (Exception e) {
            LogUtil.w(TAG, e.toString());
            LogUtil.w(TAG, ErrorCode.REPORT_HOME_KEY_UNREG);
        }
    }

    private PressHomeKey() {
        this.isRegister = false;
        this.homePressReceiver = new HomePressReceiver(new HomePressReceiver.HomePressListener() {
            @Override
            public void onHomePressed() {
                if (Constants.isDebug) {
                    LogUtil.v(PressHomeKey.TAG, "按下了Home键");
                }
                ShareHelper.getInstance().pressHomeKeyNotify();
                PressHomeKey.this.unRegisterHomePress();
            }
        });
    }
}