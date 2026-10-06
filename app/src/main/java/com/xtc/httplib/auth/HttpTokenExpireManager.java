package com.xtc.httplib.auth;

import android.content.Context;
import android.content.Intent;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;

import java.util.concurrent.TimeUnit;

/** Notifies the launcher when the session token expires. */
public class HttpTokenExpireManager {

    public static final String ACTION_TOKEN_EXPIRE = "com.xtc.watch.http.TOKEN_EXPIRE";
    public static final String HTTP_TOKEN_EXPIRE_CODE = "000018";
    public static final String IS_EXPIRE = "is_expire";

    private static final String SP_TOKEN_EXPIRE_NOTIFY_TIME = "TokenExpireNotifyTime";
    private static final String TAG = LogTag.tag("HttpTokenExpireManager");
    private static final long DEFAULT_LIMIT_TIME = TimeUnit.SECONDS.toMillis(10);

    private static volatile HttpTokenExpireManager instance = null;

    private Context context;
    private SharedManager sharedManager;
    private long lastTokenExpireNotifyTime = 0;
    private volatile int errorCount = 0;

    public static HttpTokenExpireManager getInstance() {
        if (instance == null) {
            synchronized (HttpTokenExpireManager.class) {
                if (instance == null) {
                    instance = new HttpTokenExpireManager();
                }
            }
        }
        return instance;
    }

    /** Marks the token expired and notifies the launcher, with back-off. */
    public void checkTokenExpire(boolean expire) {
        if (this.lastTokenExpireNotifyTime == 0) {
            this.lastTokenExpireNotifyTime = getLocalLastTokenExpireNotifyTime();
        }
        if (expire) {
            HttpManager.getInstance(ContextUtils.getContext()).getHttpClient().getAppInfo().setHttpTokenState(3);
        }
        long now = System.currentTimeMillis();
        if (now < this.lastTokenExpireNotifyTime) {
            this.lastTokenExpireNotifyTime = 0L;
        }
        long limit = ((long) Math.pow(2.0d, this.errorCount)) * DEFAULT_LIMIT_TIME;
        if (now - this.lastTokenExpireNotifyTime < limit) {
            LogUtil.d(TAG, "checkTokenExpire is in limit: lastTokenExpireNotifyTime = ["
                    + this.lastTokenExpireNotifyTime + "]， limitInterval = [" + limit + "]");
            return;
        }
        this.errorCount++;
        if (this.errorCount > 8) {
            this.errorCount = 0;
        }
        this.lastTokenExpireNotifyTime = now;
        saveTokenExpireErrorNotifyTime(now);
        sendHttpTokenExpireBroadcast(expire);
    }

    /** Resets the back-off counter after a successful request. */
    public void resetCount() {
        this.errorCount = 0;
    }

    private long getLocalLastTokenExpireNotifyTime() {
        Context ctx = this.context;
        if (ctx == null) {
            return 0L;
        }
        if (this.sharedManager == null) {
            this.sharedManager = SharedManager.getInstance(ctx);
        }
        return this.sharedManager.getLong(SP_TOKEN_EXPIRE_NOTIFY_TIME, 0L);
    }

    private void saveTokenExpireErrorNotifyTime(long time) {
        Context ctx = this.context;
        if (ctx == null) {
            return;
        }
        if (this.sharedManager == null) {
            this.sharedManager = SharedManager.getInstance(ctx);
        }
        this.sharedManager.putLong(SP_TOKEN_EXPIRE_NOTIFY_TIME, time);
    }

    private void sendHttpTokenExpireBroadcast(boolean expire) {
        if (this.context == null) {
            return;
        }
        LogUtil.d(TAG, "sendHttpTokenExpireBroadcast: isExpire = " + expire);
        Intent intent = new Intent(ACTION_TOKEN_EXPIRE);
        intent.setPackage("com.xtc.i3launcher");
        intent.putExtra(IS_EXPIRE, expire);
        this.context.sendBroadcast(intent);
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}