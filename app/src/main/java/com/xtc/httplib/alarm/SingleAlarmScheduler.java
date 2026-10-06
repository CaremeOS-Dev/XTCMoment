package com.xtc.httplib.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;
import com.xtc.system.account.constant.NotificationFlag;

/** Schedules a single cache-timeout alarm at a time. */
public class SingleAlarmScheduler {

    public static final String TAG = LogTag.tag("SingleAlarmScheduler");
    /** {@code AlarmManager.ELAPSED_REALTIME_WAKEUP}. */
    private static final int TYPE_ELAPSED_REALTIME_WAKEUP = 2;

    protected AlarmManager alarmManager;
    private volatile boolean isAlarmNow = false;
    private CacheTimeOutAlarmReceive cacheTimeOutAlarmReceive;
    private Context context;

    public SingleAlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        this.cacheTimeOutAlarmReceive = new CacheTimeOutAlarmReceive(context);
    }

    /** Schedules the alarm {@code delayMillis} from now. */
    public void addAlarm(int hashCode, long delayMillis) {
        if (this.isAlarmNow) {
            LogUtil.w(TAG, "there are already have a alarm");
            return;
        }
        this.isAlarmNow = scheduler(hashCode, delayMillis);
        LogUtil.d(TAG, "addAlarm : isAlarmNow = " + this.isAlarmNow);
    }

    private boolean scheduler(int hashCode, long delayMillis) {
        try {
            PendingIntent alarmIntent = getAlarmIntent(hashCode);
            LogUtil.d(TAG, "scheduler realDelayTime = " + delayMillis + ", hashCode = " + hashCode);
            long triggerAt = SystemClock.elapsedRealtime() + delayMillis;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                this.alarmManager.setExact(TYPE_ELAPSED_REALTIME_WAKEUP, triggerAt, alarmIntent);
                return true;
            }
            this.alarmManager.set(TYPE_ELAPSED_REALTIME_WAKEUP, triggerAt, alarmIntent);
            return true;
        } catch (Throwable t) {
            LogUtil.e(TAG, "scheduler error: ", t);
            return false;
        }
    }

    private PendingIntent getAlarmIntent(int hashCode) {
        Intent intent = new Intent(CacheTimeOutAlarmReceive.ACTION_TIMEOUT);
        intent.putExtra(CacheTimeOutAlarmReceive.CACHE_HASH_CODE, hashCode);
        return PendingIntent.getBroadcast(this.context, hashCode, intent, NotificationFlag.NOTIFICATION_FLAG_HEADER);
    }

    public void cancel(int hashCode) {
        this.alarmManager.cancel(getAlarmIntent(hashCode));
        this.isAlarmNow = false;
    }

    public boolean isAlarmNow() {
        return this.isAlarmNow;
    }

    public void setAlarmNow(boolean isAlarmNow) {
        this.isAlarmNow = isAlarmNow;
    }
}