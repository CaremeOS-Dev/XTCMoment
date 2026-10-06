package com.xtc.assistantapi.common;

import android.text.TextUtils;
import android.util.Log;

import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.core.DirectiveCallback;
import com.xtc.assistantapi.core.DirectiveInterceptor;
import com.xtc.assistantapi.message.Directive;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

/**
 * 指令预校验拦截器，校验请求、上下文、指令头是否完整。
 */
public class PreCheckInterceptor implements DirectiveInterceptor {

    private static final String TAG = LogTag.of("PreCheckInterceptor");

    @Override
    public void intercept(DirectiveRequest request, DirectiveCallback callback) {
        if (request == null) {
            callback.onError(new DirectiveResponse(500, "request == null"));
            return;
        }
        if (request.getContext() == null) {
            callback.onError(new DirectiveResponse(500, "getContext == null"));
            return;
        }
        if (request.getDirective() == null) {
            callback.onError(new DirectiveResponse(500, "getDirective == null"));
            return;
        }
        Directive directive = request.getDirective();
        Log.d(TAG, "handleByDefault: directive = " + directive);
        if (directive == null) {
            callback.onError(new DirectiveResponse(400, "directive is null"));
            return;
        }
        Log.d(TAG, "handleByDefault: header = " + directive.header);
        if (directive.header == null) {
            callback.onError(new DirectiveResponse(400, "directive header is null"));
            return;
        }
        String name = directive.header.getName();
        String namespace = directive.header.getNamespace();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(namespace)) {
            callback.onError(new DirectiveResponse(400, "directive name is empty"));
        } else {
            callback.onNext(request);
        }
    }
}