package com.xtc.assistantapi.core;

import com.xtc.assistantapi.message.DirectiveRequest;

/**
 * 指令拦截器，可在真正处理前拦截或预处理指令。
 */
public interface DirectiveInterceptor {

    /** 拦截处理指令。 */
    void intercept(DirectiveRequest request, DirectiveCallback callback);
}