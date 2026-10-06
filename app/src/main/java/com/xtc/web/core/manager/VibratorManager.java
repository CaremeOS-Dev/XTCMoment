package com.xtc.web.core.manager;

import android.content.Context;
import android.os.Vibrator;

import com.xtc.web.core.data.req.ReqVibrator;

/** 触发手表震动。 */
public class VibratorManager {

    /** 按 H5 传入的等待时长与震动时长震动一次。 */
    public static void vibratorWatch(Context context, ReqVibrator reqVibrator) {
        Vibrator vibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{reqVibrator.getWaitTime(), reqVibrator.getRunTime()}, -1);
        }
    }
}