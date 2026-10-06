package com.xtc.httplib;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.httplib.auth.HttpAuthManager;
import com.xtc.httplib.auth.HttpTokenExpireManager;
import com.xtc.httplib.bean.AppInfo;
import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.netstate.NetStateDataManager;
import com.xtc.httplib.okhttp.DefaultOkHttpClient;
import com.xtc.httplib.okhttp.MonitorInterceptor;
import com.xtc.httplib.okhttp.OnGetAppInfoListener;
import com.xtc.httplib.okhttp.OnHttpSuccessMonitorListener;
import com.xtc.httplib.okhttp.PreprocessorInterceptor;
import com.xtc.im.transpond.TranspondAdapter;
import com.xtc.im.transpond.TranspondManager;
import com.xtc.log.LogUtil;
import com.xtc.system.account.AppInfoImpl;
import com.xtc.system.account.InitServiceData;
import com.xtc.system.account.bean.AppInfoBase;
import com.xtc.system.account.bean.HttpConfig;
import com.xtc.system.account.utils.AbsAsyncBroadcastReceiver;
import com.xtc.utils.system.SystemLanguageUtils;
import com.xtc.utils.system.SystemTimeUtils;
import com.xtc.utils.system.model.I18n;

import java.util.List;

/** Entry point of the HTTP stack. */
public class HttpManager {

    private static final String CONTENT_WATCHID_TYPE = "content://com.xtc.provider/BaseDataProvider/watchId/1";
    private static final long SUCCESS_MONITOR_LIMIT = 5000;
    private static final String TAG = LogTag.tag("HttpManager");
    private static volatile HttpManager httpManager;

    public String acceptLanguage;
    private AppInfoReceiver appInfoReceiver;
    protected Context context;
    private HttpClient httpClient;
    private long lastHttpMonitorTime;
    private OnGetAppInfoListener onGetAppInfoListener;
    private OnHttpSuccessMonitorListener onHttpSuccessMonitorListener;
    public String watchTimeZone;

    public static HttpManager getInstance(Context context) {
        if (httpManager == null) {
            synchronized (HttpManager.class) {
                if (httpManager == null) {
                    httpManager = new HttpManager(context);
                }
            }
        }
        return httpManager;
    }

    private HttpManager(Context context) {
        this.context = context.getApplicationContext();
        initHttpClient();
        this.appInfoReceiver = new AppInfoReceiver();
        this.appInfoReceiver.register(this.context);
        HttpAuthManager.getInstance().setContext(context);
        HttpTokenExpireManager.getInstance().setContext(context);
        NetStateDataManager.getInstance().init(context);
    }

    private void initHttpClient() {
        registerHttpBridge(this.context);
        this.httpClient = new DefaultOkHttpClient(this.context);
    }

    public void setOnGetAppInfoListener(OnGetAppInfoListener listener) {
        this.onGetAppInfoListener = listener;
    }

    public void setOnHttpSuccessMonitorListener(OnHttpSuccessMonitorListener listener) {
        this.onHttpSuccessMonitorListener = listener;
    }

    /** Rate-limited callback fired after a successful request. */
    public void onHttpSuccessMonitor() {
        if (this.onHttpSuccessMonitorListener == null) {
            return;
        }
        long elapsed = SystemClock.elapsedRealtime();
        if (elapsed - this.lastHttpMonitorTime < SUCCESS_MONITOR_LIMIT) {
            return;
        }
        this.lastHttpMonitorTime = elapsed;
        this.onHttpSuccessMonitorListener.onHttpSuccessMonitor();
    }

    AppInfo getAppInfo() {
        AppInfoImpl defaultInstance = AppInfoImpl.getDefaultInstance(this.context);
        AppInfo appInfo = new AppInfo();
        appInfo.setEncSwitch(defaultInstance.getEncSwitch());
        appInfo.setGrey(defaultInstance.getGrey());
        appInfo.setRsaPublicKey(defaultInstance.getRsaPublicKey());
        appInfo.setVersion(defaultInstance.getVersion());
        appInfo.setSelfRsaPublicKeyAndId(defaultInstance.getSelfRsaPublicKey());
        appInfo.setHttpHeadParam(defaultInstance.getHttpHeadParam());
        appInfo.setEncryptEebbkKey(defaultInstance.getAe());
        appInfo.setHttpTokenState(defaultInstance.getTs());
        return appInfo;
    }

    public void destroy() {
        this.appInfoReceiver.unregister(this.context);
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    private void registerHttpBridge(Context context) {
        try {
            TranspondManager.regist((TranspondAdapter) Class.forName("com.xtc.im.transpond.TranspondAdapterImpl")
                    .getConstructor(Context.class).newInstance(context));
            LogUtil.d(TAG, "register http bridge successful");
        } catch (Throwable ignored) {
            // the IM module is optional
        }
    }

    /** Enables or disables transponding for all urls. */
    public void setAllTranspond(boolean allTranspond, List<String> urls) {
        LogUtil.i(TAG, "setAllTranspond allTranspond = " + allTranspond);
        PreprocessorInterceptor.allTranspond = allTranspond;
        if (urls == null || urls.size() == 0) {
            return;
        }
        if (allTranspond) {
            PreprocessorInterceptor.NO_NEED_TRANSPOND.addAll(urls);
        } else {
            PreprocessorInterceptor.NEED_TRANSPOND.addAll(urls);
        }
    }

    public void setUseHttps(boolean useHttps) {
        LogUtil.i(TAG, "setUseHttps use = " + useHttps);
        if (useHttps) {
            BaseUrlManager.PROTOCOL_TYPE = ConfigOptions.ProtocolType.HTTPS;
        } else {
            BaseUrlManager.PROTOCOL_TYPE = ConfigOptions.ProtocolType.HTTP;
        }
    }

    public void setTimeOut(int connectTimeout, int writeTimeout, int readTimeout) {
        LogUtil.i(TAG, "setTimeOut connectTimeout = " + connectTimeout + ",writeTimeout = " + writeTimeout
                + ",readTimeout = " + readTimeout);
        DefaultOkHttpClient.CONNECT_TIMEOUT = connectTimeout;
        DefaultOkHttpClient.WRITE_TIMEOUT = writeTimeout;
        DefaultOkHttpClient.READ_TIMEOUT = readTimeout;
    }

    public void setBootLimitTime(long limitTime, List<String> necessaryUrls) {
        LogUtil.i(TAG, "setBootLimitTime limitTime = " + limitTime);
        MonitorInterceptor.BOOT_LIMIT_TIME = limitTime;
        if (necessaryUrls == null || necessaryUrls.size() == 0) {
            return;
        }
        MonitorInterceptor.NECESSARY_BOOT_URLS.addAll(necessaryUrls);
    }

    public void resetRequestMonitorState() {
        MonitorInterceptor.resetFrequentRequestState = true;
    }

    public void setOpenMonitor(boolean openMonitor) {
        LogUtil.i(TAG, "setOpenMonitor openMonitor = " + openMonitor);
        MonitorInterceptor.openMonitor = openMonitor;
    }

    public void setHaveConfirmPermission(boolean haveConfirmPermission) {
        LogUtil.i(TAG, "setHaveConfirmPermission haveConfirmPermission = " + haveConfirmPermission);
        MonitorInterceptor.haveConfirmPermission = haveConfirmPermission;
    }

    public void setNoNeedMonitor(List<String> urls) {
        if (urls == null || urls.size() == 0) {
            return;
        }
        LogUtil.i(TAG, "setNoNeedMonitor noNeedMonitor = " + urls.size());
        MonitorInterceptor.NO_NEED_MONITOR.addAll(urls);
    }

    /** @deprecated the language is derived from the system locale. */
    @Deprecated
    public void setAcceptLanguage(String acceptLanguage) {
        LogUtil.i(TAG, "setAcceptLanguage acceptLanguage = " + acceptLanguage);
        this.acceptLanguage = acceptLanguage;
    }

    public String getAcceptLanguage() {
        if (TextUtils.isEmpty(this.acceptLanguage)) {
            String language = SystemLanguageUtils.getLocalLanguage(this.context.getApplicationContext());
            if (language.startsWith(I18n.Language.THAI)) {
                language = I18n.Language.THAI;
            }
            this.acceptLanguage = language;
        }
        return this.acceptLanguage;
    }

    /** @deprecated the time zone is derived from the system. */
    @Deprecated
    public void setWatchTimeZone(String watchTimeZone) {
        LogUtil.i(TAG, "setWatchTimeZone watchTimeZone = " + watchTimeZone);
        this.watchTimeZone = watchTimeZone;
    }

    public String getWatchTimeZone() {
        if (TextUtils.isEmpty(this.watchTimeZone)) {
            this.watchTimeZone = SystemTimeUtils.getTimeZoneDisplayName();
            LogUtil.i(TAG, "getWatchTimeZone is empty, reset watchTimeZone = " + this.watchTimeZone);
        }
        return this.watchTimeZone;
    }

    /** Receives app-info updates from the init service. */
    private class AppInfoReceiver extends AbsAsyncBroadcastReceiver {

        private AppInfoReceiver() {
        }

        @Override
        public void onReceiveAsync(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.i("receive app info update receiver,action:" + action);
            if (TextUtils.isEmpty(action)) {
                LogUtil.e("action is null");
                return;
            }
            if (action.equals("com.xtc.initservice.action.NET_PARAM_UPDATE")) {
                String rsa = intent.getStringExtra(AppInfoBase.KEY_RSA);
                String encSwitch = intent.getStringExtra(AppInfoBase.KEY_ENC_SWITCH);
                String grey = intent.getStringExtra(AppInfoBase.KEY_GREY);
                String selfRsaKey = intent.getStringExtra(AppInfoBase.KEY_SELF_RSA_KEY);
                String httpHead = intent.getStringExtra(AppInfoBase.KEY_HTTP_HEAD);
                AppInfo appInfo = HttpManager.this.httpClient.getAppInfo();
                if (appInfo == null) {
                    appInfo = new AppInfo();
                    HttpManager.this.httpClient.setAppInfo(appInfo);
                }
                if (!TextUtils.isEmpty(rsa)) {
                    appInfo.setRsaPublicKey(rsa);
                }
                if (!TextUtils.isEmpty(encSwitch)) {
                    appInfo.setEncSwitch(encSwitch);
                }
                if (!TextUtils.isEmpty(grey)) {
                    appInfo.setGrey(grey);
                }
                if (!TextUtils.isEmpty(selfRsaKey)) {
                    refreshSelfRsaPublicKeyAndId(appInfo);
                }
                if (TextUtils.isEmpty(httpHead)) {
                    return;
                }
                appInfo.setHttpHeadParam(httpHead);
                return;
            }
            if (action.equals("com.xtc.initservice.action.INIT_SUCCESS")) {
                HttpManager.this.httpClient.setWatchId(HttpManager.getWatchIdByProvider(context));
            }
        }

        private void refreshSelfRsaPublicKeyAndId(AppInfo appInfo) {
            if (appInfo == null) {
                LogUtil.w(HttpManager.TAG, "refreshSelfRsaPublicKeyAndId: appInfo is null");
                return;
            }
            HttpConfig httpConfig = InitServiceData.getAppInfo(HttpManager.this.context);
            if (httpConfig == null) {
                LogUtil.w(HttpManager.TAG, "refreshSelfRsaPublicKeyAndId: httpConfig is null");
                return;
            }
            String selfRsaPublicKey = httpConfig.getSelfRsaPublicKey();
            if (TextUtils.isEmpty(selfRsaPublicKey)) {
                appInfo.setEncryptEebbkKey(httpConfig.getAe());
                appInfo.setHttpTokenState(httpConfig.getTs());
            } else {
                appInfo.setSelfRsaPublicKeyAndId(selfRsaPublicKey);
            }
            LogUtil.d(HttpManager.TAG, "refreshSelfRsaPublicKeyAndId");
        }

        public void register(Context context) {
            Context applicationContext = context.getApplicationContext();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.xtc.initservice.action.NET_PARAM_UPDATE");
            intentFilter.addAction("com.xtc.initservice.action.INIT_SUCCESS");
            applicationContext.registerReceiver(this, intentFilter);
        }

        public void unregister(Context context) {
            context.getApplicationContext().unregisterReceiver(this);
        }
    }

    public static String getWatchIdByProvider(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_WATCHID_TYPE));
    }

    public Context getContext() {
        return this.context;
    }
}