package com.xtc.assistantapi.core;

import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

/**
 * 指令处理器基类，支持挂载拦截器链。
 */
public abstract class DirectiveHandler {

    private static final String TAG = LogTag.of("DirectiveHandler");

    protected ChainedInterceptor interceptor;

    /** 处理指令。 */
    protected abstract void handle(DirectiveRequest request, DirectiveCallback callback);

    /** 是否可处理该指令。 */
    protected abstract boolean canHandle(DirectiveRequest request);

    public DirectiveHandler addInterceptor(DirectiveInterceptor interceptor) {
        if (interceptor != null) {
            if (this.interceptor == null) {
                this.interceptor = new ChainedInterceptor();
            }
            this.interceptor.addInterceptor(interceptor);
        }
        return this;
    }

    public DirectiveHandler addInterceptors(DirectiveInterceptor... interceptors) {
        if (interceptors != null && interceptors.length > 0) {
            if (this.interceptor == null) {
                this.interceptor = new ChainedInterceptor();
            }
            for (DirectiveInterceptor interceptor : interceptors) {
                this.interceptor.addInterceptor(interceptor);
            }
        }
        return this;
    }

    /** 执行指令处理流程。 */
    public void process(DirectiveRequest request, final DirectiveCallback callback) {
        if (canHandle(request)) {
            ChainedInterceptor interceptor = this.interceptor;
            if (interceptor != null) {
                interceptor.intercept(request, new DirectiveCallback() {
                    @Override
                    public void onNext(DirectiveRequest request) {
                        DirectiveHandler.this.handle(request, callback);
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
                handle(request, callback);
            }
        } else {
            callback.onNext(request);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}