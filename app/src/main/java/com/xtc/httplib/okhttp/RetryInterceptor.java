package com.xtc.httplib.okhttp;

import android.content.Context;

import com.xtc.httplib.LogTag;
import com.xtc.httplib.annotation.Retry;
import com.xtc.httplib.strategy.RetryInterface;
import com.xtc.httplib.strategy.SwitchNetwork;
import com.xtc.log.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.Invocation;

/** Retries the requests annotated with {@link Retry}. */
public class RetryInterceptor extends BaseInterceptor {

    private static final String TAG = LogTag.tag("RetryInterceptor");

    private final List<RetryInterface> retryInterfaces;

    RetryInterceptor(Context context) {
        super(context);
        this.retryInterfaces = new ArrayList<>();
        this.retryInterfaces.add(new SwitchNetwork(context));
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        if (!isNeedRetry(chain)) {
            return chain.proceed(chain.request());
        }
        LogUtil.i(TAG, "intercept() needRetry = [" + chain.request().url().toString() + "]");
        Iterator<RetryInterface> iterator = this.retryInterfaces.iterator();
        while (iterator.hasNext()) {
            iterator.next().startRequest(chain);
        }
        return realRetry(chain);
    }

    private Response realRetry(Interceptor.Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (Exception e) {
            LogUtil.d(TAG, "intercept() " + e.getMessage());
            boolean retried = false;
            Iterator<RetryInterface> iterator = this.retryInterfaces.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().tryRetry(null, chain, e)) {
                    retried = true;
                    break;
                }
            }
            LogUtil.d(TAG, "intercept() retry = " + retried);
            if (retried) {
                return chain.proceed(chain.request());
            }
            throw e;
        }
    }

    private boolean isNeedRetry(Interceptor.Chain chain) {
        Invocation invocation = chain.request().tag(Invocation.class);
        return invocation != null && invocation.method().getAnnotation(Retry.class) != null;
    }
}