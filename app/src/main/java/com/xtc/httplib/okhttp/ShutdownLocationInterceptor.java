package com.xtc.httplib.okhttp;

import android.os.SystemClock;

import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.SystemPropertyUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/** Aborts the request when the watch is shutting down for a location report. */
public class ShutdownLocationInterceptor implements Interceptor {

    private static final String TAG = "ShutdownLocationInterceptor";
    private static final String PROP_POFFMODE = "ro.boot.xtc.poffmode";
    private static final String VALUE_SHUTDOWN_LOCATION = "shutdown-position";

    public static boolean enable = true;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        long startTime = SystemClock.elapsedRealtime();
        if (enable && VALUE_SHUTDOWN_LOCATION.equals(SystemPropertyUtil.get(PROP_POFFMODE, ""))) {
            LogUtil.i(TAG, "intercept, because shutdown location");
            throw new IOException("shutdown location");
        }
        HttpRequestEvent dnsRecord = BigdataClientManager.getInstance().getDnsReqRecord(chain.request().header("uuid"));
        if (dnsRecord != null) {
            dnsRecord.setInterceptorShutdownLctTime(String.valueOf(SystemClock.elapsedRealtime() - startTime));
        }
        return chain.proceed(chain.request());
    }
}