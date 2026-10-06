package com.xtc.web.client.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.data.response.RespWakeLock;
import com.xtc.web.core.callback.CompletionHandler;

/** H5 申请/释放 WakeLock，保持屏幕常亮。 */
public class WakeLockManager {

    private static final String TAG = Constants.TAG + WakeLockManager.class.getSimpleName();
    private static WakeLockManager instance;

    private Context context;
    private Handler mTimeHandler = new Handler(Looper.getMainLooper());
    private PowerManager.WakeLock wakeLock;

    public WakeLockManager(Context context) {
        this.context = context;
    }

    public static synchronized WakeLockManager getInstance(Context context) {
        if (instance == null) {
            instance = new WakeLockManager(context);
        }
        return instance;
    }

    /** 申请指定时长的 WakeLock。 */
    public void wakeLock(final long timeout, CompletionHandler<RespWakeLock> completionHandler) {
        Context context = this.context;
        if (context != null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (this.wakeLock == null && powerManager != null) {
                this.wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "webCore-wakeLock");
                Runnable acquireTask = new Runnable() {
                    @Override
                    public void run() {
                        wakeLock.acquire(timeout);
                    }
                };
                this.mTimeHandler.removeCallbacks(acquireTask);
                this.mTimeHandler.post(acquireTask);
                RespWakeLock response = new RespWakeLock();
                response.setCode(RespWakeLock.Code.SUCCESS);
                completionHandler.complete(response);
                return;
            }
            RespWakeLock response = new RespWakeLock();
            response.setCode(RespWakeLock.Code.FAIL);
            completionHandler.complete(response);
            return;
        }
        LogUtil.i(TAG, "context is null ");
    }

    public void releaseWakeLock(CompletionHandler<RespWakeLock> completionHandler) {
        release();
        RespWakeLock response = new RespWakeLock();
        response.setCode(RespWakeLock.Code.SUCCESS);
        completionHandler.complete(response);
    }

    /** 释放当前 WakeLock。 */
    public void release() {
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock == null || !wakeLock.isHeld()) {
            return;
        }
        this.wakeLock.release();
        this.wakeLock = null;
    }
}