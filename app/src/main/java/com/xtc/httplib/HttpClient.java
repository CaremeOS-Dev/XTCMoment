package com.xtc.httplib;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.httplib.bean.AppInfo;
import com.xtc.httplib.bean.DeviceInfo;
import com.xtc.httplib.bean.WatchInfo;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/** Base class of the per-app HTTP clients. */
public abstract class HttpClient {

    private static final String TAG = LogTag.tag("HttpClient");

    private AppInfo appInfo;
    protected Context context;
    private DeviceInfo deviceInfo;
    private long registId;
    private String watchId;

    public void addHeadToOkHttpClient(Map<String, String> headers) {
    }

    public abstract String getPackageName();

    public abstract int getVersionCode();

    public abstract String getVersionName();

    public Call newCall(Request request) {
        return null;
    }

    public <T> T request(String url, Class<T> service) {
        return null;
    }

    public <T> T requestSync(String url, Class<T> service) {
        return null;
    }

    public <T> T requestThird(String url, Class<T> service) {
        return null;
    }

    public <T> T requestThirdSync(String url, Class<T> service) {
        return null;
    }

    public HttpClient(Context context) {
        this.context = context.getApplicationContext();
        this.deviceInfo = new WatchInfo(context);
    }

    /** Watch id read from the launcher provider. */
    public String getWatchId() {
        if (TextUtils.isEmpty(this.watchId)) {
            this.watchId = HttpManager.getWatchIdByProvider(this.context);
        }
        return this.watchId;
    }

    HttpClient setWatchId(String watchId) {
        this.watchId = watchId;
        return this;
    }

    public long getRegistId() {
        return this.registId;
    }

    public HttpClient setRegistId(long registId) {
        this.registId = registId;
        return this;
    }

    public DeviceInfo getDeviceInfo() {
        return this.deviceInfo;
    }

    public HttpClient setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    synchronized HttpClient setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        return this;
    }

    public synchronized AppInfo getAppInfo() {
        if (this.appInfo == null) {
            this.appInfo = HttpManager.getInstance(this.context).getAppInfo();
        }
        return this.appInfo;
    }
}