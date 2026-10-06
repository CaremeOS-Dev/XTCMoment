package com.xtc.system.account;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.constant.ActionConstants;
import com.xtc.system.account.utils.AbsAsyncBroadcastReceiver;

/** Receives the fun-data init-completed broadcast and refreshes all feature switches. */
public class FunSwitchInitReceiver extends AbsAsyncBroadcastReceiver {

    private static final String TAG = "FunSwitchInitReceiver";

    @Override
    protected void onReceiveAsync(Context context, Intent intent) {
        try {
            if (intent == null) {
                LogUtil.e(TAG, "onReceive intent is null");
                return;
            }
            if (TextUtils.isEmpty(intent.getAction())) {
                LogUtil.e(TAG, "onReceive intent action is empty");
                return;
            }
            String action = intent.getAction();
            LogUtil.i(TAG, "action = " + action);
            if (!ActionConstants.ACTION_FUN_DATA_INIT_COMPLETED.equals(action)) {
                return;
            }
            FunManager.getInstance(context).initFunSwitch();
        } catch (Exception e) {
            LogUtil.e(TAG, "error", e);
        }
    }
}