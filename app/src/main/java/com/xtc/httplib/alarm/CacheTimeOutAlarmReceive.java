package com.xtc.httplib.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xtc.httplib.LogTag;
import com.xtc.httplib.cache.RequestCacheManager;
import com.xtc.log.LogUtil;

/** Receives the cache-timeout alarms scheduled by {@link SingleAlarmScheduler}. */
public class CacheTimeOutAlarmReceive extends BroadcastReceiver {

    private static final String TAG = LogTag.tag("CacheTimeOutAlarmReceive");
    public static String ACTION_TIMEOUT = "com.xtc.http.cache.timeout.action";
    public static String CACHE_HASH_CODE = "cache_hash_code";

    public CacheTimeOutAlarmReceive(Context context) {
        register(context);
    }

    private void register(Context context) {
        LogUtil.i(TAG, "register");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TIMEOUT);
        context.registerReceiver(this, intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            LogUtil.e(TAG, "intent is null");
            return;
        }
        if (!ACTION_TIMEOUT.equals(intent.getAction())) {
            LogUtil.w(TAG, "not " + ACTION_TIMEOUT);
            return;
        }
        RequestCacheManager.getInstance().onTimeAlarm(intent.getIntExtra(CACHE_HASH_CODE, -1));
    }
}