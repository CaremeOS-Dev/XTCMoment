package com.xtc.httplib.okhttp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xtc.httplib.net.BaseSubscriber;
import com.xtc.log.LogUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Response;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/** Reacts to the {@code IM-IP-Refresh} / {@code IM-Port} headers by broadcasting a refresh. */
public class IMRefreshDomainInterceptor extends BaseInterceptor {

    public static final String TAG = "IMRefreshDomainInterceptor";

    private static final String ACTION_REFRESH_IM_CONNECT_IP = "com.xtc.watch.im.REFRESH_CONNECT_IP";
    private static final String ACTION_REFRESH_IM_CONNECT_PORT = "com.xtc.watch.im.REFRESH_CONNECT_PORT";
    public static final String HEADER_KEY_IM_IP = "IM-IP-Refresh";
    public static final String HEADER_KEY_IM_PORT = "IM-Port";
    public static final String KEY_IM_IP_VERSION = "key_im_ip_version";

    private final SharedPreferences sharedPreferences;
    private final boolean[] isLog;
    private final PublishSubject<Intent> subjectIp;
    private final PublishSubject<Intent> subjectPort;

    public IMRefreshDomainInterceptor(final Context context) {
        super(context);
        this.subjectIp = PublishSubject.create();
        this.subjectPort = PublishSubject.create();
        this.isLog = new boolean[2];
        this.sharedPreferences = context.getSharedPreferences("com.xtc.watch.http", 0);
        this.subjectIp.throttleFirst(30L, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<Intent>() {
                    @Override
                    public void onNext(Intent intent) {
                        super.onNext(intent);
                        int version = intent.getIntExtra(KEY_IM_IP_VERSION, 0);
                        sharedPreferences.edit().putInt(KEY_IM_IP_VERSION, version).commit();
                        LogUtil.d(TAG, MessageFormat.format(
                                "refreshConnect,send broadcast, action = {0}  version = {1}",
                                intent.getAction(), version));
                        context.sendBroadcast(intent);
                    }
                });
        this.subjectPort.throttleFirst(60L, TimeUnit.SECONDS).observeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<Intent>() {
                    @Override
                    public void onNext(Intent intent) {
                        super.onNext(intent);
                        String port = intent.getStringExtra(HEADER_KEY_IM_PORT);
                        sharedPreferences.edit().putString(HEADER_KEY_IM_PORT, port).commit();
                        LogUtil.d(TAG, MessageFormat.format(
                                "refreshConnect,send broadcast, action = {0}  port = {1}",
                                intent.getAction(), port));
                        context.sendBroadcast(intent);
                    }
                });
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.isSuccessful()) {
            String ipHeader = response.header(HEADER_KEY_IM_IP, "");
            String portHeader = response.header(HEADER_KEY_IM_PORT, "");
            if ("".equals(portHeader) && "".equals(ipHeader)) {
                return response;
            }
            checkIpHeaderUpdate(ipHeader);
            checkPortHeaderUpdate(portHeader);
        }
        return response;
    }

    private void checkPortHeaderUpdate(String port) {
        if ("".equals(port)) {
            return;
        }
        String cachedPort = this.sharedPreferences.getString(HEADER_KEY_IM_PORT, "");
        if (!this.isLog[1]) {
            LogUtil.d(TAG, "refreshConnect,IM-Port-" + port + ", cache -" + cachedPort);
            this.isLog[1] = true;
        }
        if (cachedPort.equals(port)) {
            return;
        }
        LogUtil.d(TAG, "refreshConnect, send port refresh notify.current port-" + port);
        Intent intent = new Intent(ACTION_REFRESH_IM_CONNECT_PORT);
        intent.putExtra(HEADER_KEY_IM_PORT, port);
        currentLimitingSendBroadcast(intent, this.subjectPort);
    }

    private void checkIpHeaderUpdate(String ipVersion) {
        int version;
        if ("".equals(ipVersion)) {
            return;
        }
        try {
            version = Integer.valueOf(ipVersion);
        } catch (Exception e) {
            LogUtil.e(TAG, "refreshConnect: ", e);
            version = 0;
        }
        int cachedVersion = this.sharedPreferences.getInt(KEY_IM_IP_VERSION, 0);
        if (!this.isLog[0]) {
            LogUtil.d(TAG, "refreshConnect,IM-IP-Refresh-" + version + "; cache - " + cachedVersion);
            this.isLog[0] = true;
        }
        if (version > cachedVersion) {
            LogUtil.d(TAG, "refreshConnect,send ip refresh notify.version-" + version);
            Intent intent = new Intent(ACTION_REFRESH_IM_CONNECT_IP);
            intent.putExtra(KEY_IM_IP_VERSION, version);
            currentLimitingSendBroadcast(intent, this.subjectIp);
        }
    }

    private void currentLimitingSendBroadcast(Intent intent, PublishSubject<Intent> subject) {
        if (subject != null) {
            subject.onNext(intent);
        }
    }
}