package com.xtc.web.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import com.xtc.log.LogUtil;
import com.xtc.moment.receiver.RecordVideoReceiver;
import com.xtc.system.account.constant.ActionConstants;
import com.xtc.web.client.manager.JsEruptManager;

/** 监听系统事件并转成 H5 可识别的 eruptData 事件。 */
public class CommonReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM_CLOCK = "com.xtc.alarmclock.action.ALARM_VIEW_SHOWING";
    public static final String ACTION_CLASS_MODE = "com.xtc.setting.action.CLASS.ACTION";
    public static final String ACTION_POWER_KEY = "com.xtc.i3launcher.module.powerkey.event.broadcast";
    public static final String ACTION_POWER_LOW = "xtc.setting.action.POWER_SAVE_CHANGE";
    public static final String ACTION_WATCH_LOSS = "com.xtc.setting.WATCH.LOSS";
    public static final String ACTION_WORKPLAN_START = "com.xtc.workplan.ring.start";
    public static final String BROADCAST_VIDEO_CALL = "com.xtc.videochat.callin";
    public static final String HIGH_TEMPERATURE1 = "android.intent.action.KILL_APP";
    public static final String HIGH_TEMPERATURE2 = "android.intent.action.BT_SHUT_DOWN";
    public static final String PHONE_CALL = "android.intent.action.PHONE_STATE";
    private static final String TAG = "WebClient_CommonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            LogUtil.d(TAG, "onReceive: but intent.getAction is null");
            return;
        }
        LogUtil.d(TAG, "onReceive: intent.getAction: " + action);
        if (ACTION_ALARM_CLOCK.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(1);
        } else if (ACTION_WORKPLAN_START.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(2);
        } else if (HIGH_TEMPERATURE1.equals(action) || HIGH_TEMPERATURE2.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(3);
        } else if (ACTION_POWER_LOW.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(4);
        } else if (PHONE_CALL.equals(action)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                LogUtil.d(TAG, "onReceive: PHONE_CALL but tManager is null");
                return;
            }
            int callState = telephonyManager.getCallState();
            LogUtil.i(TAG, "CameraCommonReceiver, phoneState:" + callState);
            if (callState == TelephonyManager.CALL_STATE_RINGING || callState == TelephonyManager.CALL_STATE_OFFHOOK) {
                JsEruptManager.getInstance(context).eruptData(5);
            }
        } else if (BROADCAST_VIDEO_CALL.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(6);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(7);
        } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(8);
        } else if (ACTION_WATCH_LOSS.equals(action)) {
            if (intent.getBooleanExtra(ActionConstants.EXTRA_STATE, false)) {
                JsEruptManager.getInstance(context).eruptData(9);
            } else {
                LogUtil.i(TAG, "监听到关闭手表挂失设置");
            }
        } else if (ACTION_CLASS_MODE.equals(action)) {
            if (intent.getBooleanExtra(ActionConstants.EXTRA_STATE, false)) {
                JsEruptManager.getInstance(context).eruptData(10);
            } else {
                LogUtil.i(TAG, "监听到上课禁用设置，但当前并不进行上课禁用");
            }
        } else if (ACTION_POWER_KEY.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(11);
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            JsEruptManager.getInstance(context).eruptData(12);
        }
    }

    /** 系统事件过滤器。 */
    public static IntentFilter getCameraCommonFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_WORKPLAN_START);
        intentFilter.addAction(ACTION_ALARM_CLOCK);
        intentFilter.addAction(ACTION_CLASS_MODE);
        intentFilter.addAction(ACTION_POWER_LOW);
        intentFilter.addAction(HIGH_TEMPERATURE1);
        intentFilter.addAction(HIGH_TEMPERATURE2);
        intentFilter.addAction(PHONE_CALL);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(RecordVideoReceiver.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(BROADCAST_VIDEO_CALL);
        intentFilter.addAction(ACTION_WATCH_LOSS);
        intentFilter.addAction(ACTION_POWER_KEY);
        return intentFilter;
    }
}