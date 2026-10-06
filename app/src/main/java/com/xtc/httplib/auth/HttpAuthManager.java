package com.xtc.httplib.auth;

import android.content.Context;
import android.content.Intent;

import com.xtc.httplib.LogTag;
import com.xtc.httplib.bigdata.RsaKeyBigData;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** Notifies the launcher when the auth code is rejected. */
public class HttpAuthManager {

    public static final String ACTION_AUTH_ERROR = "com.xtc.watch.http.AUTH_ERROR";
    public static final String AUTH_ERROR_CODE = "000016";

    private static final String SP_AUTH_ERROR_NOTIFY_TIME = "AuthErrorNotifyTime";
    private static final String TAG = LogTag.tag("HttpAuthManager");
    private static final long DEFAULT_LIMIT_TIME = TimeUnit.SECONDS.toMillis(30);

    private static volatile HttpAuthManager instance = null;

    private Context context;
    private SharedManager sharedManager;
    private long lastAuthErrorNotifyTime = 0;
    private int errorCount = 0;

    public static HttpAuthManager getInstance() {
        if (instance == null) {
            synchronized (HttpAuthManager.class) {
                if (instance == null) {
                    instance = new HttpAuthManager();
                }
            }
        }
        return instance;
    }

    /** Handles an auth-related response code. */
    public void checkAuth(String code, int source) {
        if (Objects.equals(HttpTokenExpireManager.HTTP_TOKEN_EXPIRE_CODE, code)) {
            HttpTokenExpireManager.getInstance().checkTokenExpire(true);
            return;
        }
        if (Objects.equals(AUTH_ERROR_CODE, code)) {
            if (this.lastAuthErrorNotifyTime == 0) {
                this.lastAuthErrorNotifyTime = getLocalLastAuthErrorNotifyTime();
            }
            long now = System.currentTimeMillis();
            if (now < this.lastAuthErrorNotifyTime) {
                this.lastAuthErrorNotifyTime = 0L;
            }
            long limit = ((long) Math.pow(2.0d, this.errorCount)) * DEFAULT_LIMIT_TIME;
            if (now - this.lastAuthErrorNotifyTime < limit) {
                LogUtil.d(TAG, "checkAuth is in limit: lastAuthErrorNotifyTime = [" + this.lastAuthErrorNotifyTime
                        + "]， limitInterval = [" + limit + "]");
                return;
            }
            this.errorCount++;
            this.lastAuthErrorNotifyTime = now;
            saveLastAuthErrorNotifyTime(now);
            sendAuthErrorBroadcast();
            RsaKeyBigData.uploadKeyError(this.context, source);
        }
    }

    private long getLocalLastAuthErrorNotifyTime() {
        Context ctx = this.context;
        if (ctx == null) {
            return 0L;
        }
        if (this.sharedManager == null) {
            this.sharedManager = SharedManager.getInstance(ctx);
        }
        return this.sharedManager.getLong(SP_AUTH_ERROR_NOTIFY_TIME, 0L);
    }

    private void saveLastAuthErrorNotifyTime(long time) {
        Context ctx = this.context;
        if (ctx == null) {
            return;
        }
        if (this.sharedManager == null) {
            this.sharedManager = SharedManager.getInstance(ctx);
        }
        this.sharedManager.putLong(SP_AUTH_ERROR_NOTIFY_TIME, time);
    }

    private void sendAuthErrorBroadcast() {
        if (this.context == null) {
            return;
        }
        LogUtil.d(TAG, "sendAuthErrorBroadcast");
        Intent intent = new Intent(ACTION_AUTH_ERROR);
        intent.setPackage("com.xtc.i3launcher");
        this.context.sendBroadcast(intent);
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}