package com.xtc.httplib.strategy;

import okhttp3.Interceptor;
import okhttp3.Response;

/** Strategy hook used by the retry interceptor. */
public interface RetryInterface {
    void startRequest(Interceptor.Chain chain);

    boolean tryRetry(Response response, Interceptor.Chain chain, Exception exception);
}