package com.xtc.moment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtc.log.LogUtil;
import com.xtc.moment.event.MomentVideoData;
import com.xtc.system.account.constant.ActionConstants;

import org.greenrobot.eventbus.EventBus;

/**
 * 录视频时的打断监听：闹钟、来电、上课禁用、低电量等场景下暂停录制。
 */
public class RecordVideoReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM_CLOCK = "com.xtc.alarmclock.action.ALARM_VIEW_SHOWING";
    public static final String ACTION_CLASS_MODE = "com.xtc.setting.action.CLASS.ACTION";
    public static final String ACTION_POWER_LOW = "xtc.setting.action.POWER_SAVE_CHANGE";
    public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private static final String ACTION_WORKPLAN_START = "com.xtc.workplan.ring.start";
    public static final String BROADCAST_VIDEO_CALL = "com.xtc.videochat.callin";
    public static final String HIGH_TEMPERATURE1 = "android.intent.action.KILL_APP";
    public static final String HIGH_TEMPERATURE2 = "android.intent.action.BT_SHUT_DOWN";
    public static final String PHONE_CALL = "android.intent.action.PHONE_STATE";
    private static final String TAG = RecordVideoReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "RecordVideoReceiver onReceive action:" + intent.getAction());
        switch (intent.getAction()) {
            case ACTION_ALARM_CLOCK:
            case ACTION_WORKPLAN_START:
            case HIGH_TEMPERATURE1:
            case HIGH_TEMPERATURE2:
            case ACTION_POWER_LOW:
            case PHONE_CALL:
            case BROADCAST_VIDEO_CALL:
            case "android.intent.action.ACTION_POWER_CONNECTED":
                EventBus.getDefault().post(new MomentVideoData(1));
                break;
            case ACTION_SCREEN_OFF:
                EventBus.getDefault().post(new MomentVideoData(2));
                break;
            case ACTION_CLASS_MODE:
                if (intent.getBooleanExtra(ActionConstants.EXTRA_STATE, false)) {
                    EventBus.getDefault().post(new MomentVideoData(1));
                } else {
                    LogUtil.i(TAG, "监听到上课禁用设置，但当前并不进行上课禁用");
                }
                break;
            default:
                break;
        }
    }

    public static IntentFilter getWeichatCommonFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_WORKPLAN_START);
        intentFilter.addAction(ACTION_ALARM_CLOCK);
        intentFilter.addAction(ACTION_CLASS_MODE);
        intentFilter.addAction(ACTION_POWER_LOW);
        intentFilter.addAction(HIGH_TEMPERATURE1);
        intentFilter.addAction(HIGH_TEMPERATURE2);
        intentFilter.addAction(PHONE_CALL);
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction(ACTION_SCREEN_OFF);
        intentFilter.addAction(BROADCAST_VIDEO_CALL);
        return intentFilter;
    }
}