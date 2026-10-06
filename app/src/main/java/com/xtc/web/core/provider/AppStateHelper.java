package com.xtc.web.core.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.xtc.utils.encode.JSONUtil;
import com.xtc.web.core.data.resp.RespAppState;

import java.util.concurrent.atomic.AtomicReference;

/** 通过 ContentProvider 向应用市场查询应用状态（是否支持/是否可下载）。 */
public class AppStateHelper extends ContentObserver {

    private static final String KEY_APP_STATE_DATA = "appStateResponse";
    private static final String METHOD_GET_APP_STATE = "getAppState";
    private static final String PROVIDER_AUTHORITY = "com.xtc.appupdate.AppInfoProvider";
    private static final String URI_GET_APP_STATE = "content://com.xtc.appupdate.AppInfoProvider/getAppState";
    private static final Uri URI = Uri.parse(URI_GET_APP_STATE);
    private static final AtomicReference<AppStateHelper> INSTANCE = new AtomicReference<>();

    /** 应用状态查询结果回调。 */
    public interface AppStateListener {
        void onResultState(RespAppState respAppState);
    }

    private AppStateListener appStateListener;
    private ContentResolver contentResolver;
    private Context mContext;

    public static AppStateHelper getInstance(Context context) {
        AppStateHelper helper;
        do {
            AppStateHelper cached = INSTANCE.get();
            if (cached != null) {
                return cached;
            }
            helper = new AppStateHelper(context);
        } while (!INSTANCE.compareAndSet(null, helper));
        return helper;
    }

    private AppStateHelper(Context context) {
        super(new Handler(Looper.getMainLooper()));
        this.mContext = context.getApplicationContext();
        this.contentResolver = this.mContext.getContentResolver();
    }

    public void setAppStateListener(AppStateListener appStateListener) {
        this.appStateListener = appStateListener;
    }

    public void registerObserver() {
        unregisterObserver();
        this.contentResolver.registerContentObserver(URI, true, this);
    }

    public void unregisterObserver() {
        this.contentResolver.unregisterContentObserver(this);
    }

    /** 发起一次应用状态查询。 */
    public void getAppState(String packageName) {
        this.contentResolver.call(URI, METHOD_GET_APP_STATE, packageName, (Bundle) null);
    }

    /** 应用市场是否提供 AppInfoProvider。 */
    public boolean checkHasAppInfoProvider() {
        return this.mContext.getPackageManager().resolveContentProvider(PROVIDER_AUTHORITY, 0) != null;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (uri == null || TextUtils.isEmpty(uri.toString()) || !uri.toString().contains(URI_GET_APP_STATE)) {
            return;
        }
        RespAppState respAppState = JSONUtil.fromJSON(uri.getQueryParameter(KEY_APP_STATE_DATA),
                RespAppState.class);
        AppStateListener listener = this.appStateListener;
        if (listener != null) {
            listener.onResultState(respAppState);
        }
    }
}