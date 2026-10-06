package com.xtc.httplib.rxadapter;

import android.content.Context;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.cache.RequestCacheManager;
import com.xtc.httplib.okhttp.DefaultOkHttpClient;
import com.xtc.httplib.okhttp.LogInterceptor;
import com.xtc.httplib.util.HttpUtil;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.NetworkUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;

/** Base on-subscribe that parks requests while the token is invalid. */
public abstract class BaseXtcOnSubscribe<T> implements XtcOnSubscribe<Response<T>> {

    /** Error code reported when there is no network connectivity. */
    private static final int ERROR_NO_NETWORK = 1005;
    /** Error code reported when the token could not be refreshed in time. */
    private static final int ERROR_TOKEN_EXPIRE = 1007;

    protected final Call<T> originalCall;

    BaseXtcOnSubscribe(Call<T> call) {
        this.originalCall = call;
    }

    @Override
    public void call(Subscriber<? super Response<T>> subscriber) {
        Context context = HttpManager.getInstance(ContextUtils.getContext()).getContext();
        if (context != null && !NetworkUtils.isConnected(context)) {
            LogUtil.w(LogInterceptor.TAG, "net not connected");
            callOnTokenExpireError(subscriber, ERROR_NO_NETWORK);
        } else if (RequestCacheManager.getInstance().checkHttpTokenValid(this.originalCall, this, subscriber)) {
            callRealRequest(subscriber, null);
        }
    }

    @Override
    public void callOnTokenExpireError(Subscriber<? super Response<T>> subscriber) {
        callOnTokenExpireError(subscriber, ERROR_TOKEN_EXPIRE);
    }

    private void callOnTokenExpireError(Subscriber<? super Response<T>> subscriber, int errorCode) {
        Call<T> call = this.originalCall.clone();
        XtcCallArbiter<T> arbiter = new XtcCallArbiter<>(call, subscriber);
        subscriber.add(arbiter);
        subscriber.setProducer(arbiter);
        try {
            StringBuilder builder = new StringBuilder();
            builder.setLength(0);
            Request request = call.request();
            boolean hideResponseLog = LogInterceptor.isRequestHideResponseLog(request);
            builder.append("--> ");
            DefaultOkHttpClient.LOG_INTERCEPTOR.buildRequestMessage(request, builder, null);
            builder.append('\n');
            long start = System.nanoTime();
            okhttp3.Response errorResponse = HttpUtil.createErrorResponse(request, request.url().toString(), errorCode, null);
            DefaultOkHttpClient.LOG_INTERCEPTOR.buildResponseMessage(errorResponse,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), builder, hideResponseLog);
            LogUtil.i(LogInterceptor.TAG, builder.toString());
            arbiter.emitError(new HttpTokenExpireException(errorResponse));
        } catch (Throwable t) {
            arbiter.emitError(t);
        }
    }

    public String getUrl() {
        return this.originalCall.request().url().toString();
    }
}