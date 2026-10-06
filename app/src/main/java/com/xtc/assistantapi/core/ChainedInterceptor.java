package com.xtc.assistantapi.core;

import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 链式拦截器，按注册顺序依次拦截指令。
 */
public class ChainedInterceptor implements DirectiveInterceptor {

    private static final String TAG = LogTag.of("ChainedInterceptor");

    private final List<DirectiveInterceptor> interceptors = new LinkedList<>();

    public void addInterceptor(DirectiveInterceptor interceptor) {
        if (interceptor != null) {
            this.interceptors.add(interceptor);
        }
    }

    @Override
    public void intercept(DirectiveRequest request, DirectiveCallback callback) {
        intercept(this.interceptors.iterator(), request, callback);
    }

    private void intercept(final Iterator<DirectiveInterceptor> iterator, DirectiveRequest request,
                           final DirectiveCallback callback) {
        if (iterator.hasNext()) {
            iterator.next().intercept(request, new DirectiveCallback() {
                @Override
                public void onNext(DirectiveRequest request) {
                    intercept(iterator, request, callback);
                }

                @Override
                public void onComplete(DirectiveResponse response) {
                    callback.onComplete(response);
                }

                @Override
                public void onError(DirectiveResponse response) {
                    callback.onError(response);
                }
            });
        } else {
            callback.onNext(request);
        }
    }
}