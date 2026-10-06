package com.xtc.moment;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.system.account.WatchAccountBase;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

/**
 * 动态模块的全局状态持有者。
 */
public class MomentApp {

    private static final String TAG = "XTC_MOMENT_MomentApp";
    private static final long TWO_HOUR = TimeUnit.HOURS.toMillis(2);

    private static Context appContext;
    private static String watchId;

    public static volatile boolean isSendingVideo;
    private static volatile long lastSendVideoTime;

    public static boolean isSendingVideo() {
        if (SystemClock.elapsedRealtime() - lastSendVideoTime > TWO_HOUR) {
            isSendingVideo = false;
        }
        return isSendingVideo;
    }

    public static void setIsSendingVideo(boolean sendingVideo) {
        if (sendingVideo) {
            lastSendVideoTime = SystemClock.elapsedRealtime();
        }
        isSendingVideo = sendingVideo;
    }

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static String getWatchId() {
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.i(TAG, "getWatchId: watchId is null.");
            Context context = appContext;
            if (context != null) {
                setWatchId(WatchAccountBase.getAccountWatchId(context));
            }
        }
        return watchId;
    }

    public static void setWatchId(String id) {
        watchId = id;
    }

    public static void initFriendInfo(final Context context) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                FriendInfoServeImpl.getInstance(context).onContactChange();
            }
        });
    }

    public static void syncLauncherData() {
        EventBus.getDefault().post(new EventType(3));
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void refreshAccount() {
        AccountInfoServerImpl.getInstance(getAppContext()).initAllInfoDirectly();
        setWatchId(WatchAccountBase.getAccountWatchId(appContext));
    }
}