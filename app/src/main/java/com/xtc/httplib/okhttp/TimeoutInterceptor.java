package com.xtc.httplib.okhttp;

import android.os.SystemClock;

import com.xtc.httplib.annotation.Timeout;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.constant.HttpRequestEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.Invocation;

/** Applies the per-method {@link Timeout} override to the okhttp chain. */
public class TimeoutInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        long startTime = SystemClock.elapsedRealtime();
        String uuid = chain.request().header("uuid");
        Invocation invocation = chain.request().tag(Invocation.class);
        if (invocation == null) {
            record(uuid, startTime);
            return chain.proceed(chain.request());
        }
        Timeout timeout = invocation.method().getAnnotation(Timeout.class);
        if (timeout == null) {
            record(uuid, startTime);
            return chain.proceed(chain.request());
        }
        if (timeout.all() >= 0) {
            chain = chain.withConnectTimeout(timeout.all(), TimeUnit.MILLISECONDS)
                    .withReadTimeout(timeout.all(), TimeUnit.MILLISECONDS)
                    .withWriteTimeout(timeout.all(), TimeUnit.MILLISECONDS);
        }
        if (timeout.connect() >= 0) {
            chain = chain.withConnectTimeout(timeout.connect(), TimeUnit.MILLISECONDS);
        }
        if (timeout.read() >= 0) {
            chain = chain.withReadTimeout(timeout.read(), TimeUnit.MILLISECONDS);
        }
        if (timeout.write() >= 0) {
            chain = chain.withWriteTimeout(timeout.write(), TimeUnit.MILLISECONDS);
        }
        record(uuid, startTime);
        return chain.proceed(chain.request());
    }

    private static void record(String uuid, long startTime) {
        HttpRequestEvent dnsRecord = BigdataClientManager.getInstance().getDnsReqRecord(uuid);
        if (dnsRecord != null) {
            dnsRecord.setInterceptorTimeoutTime(String.valueOf(SystemClock.elapsedRealtime() - startTime));
        }
    }
}