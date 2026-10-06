package com.xtc.assistantapi.core;

import android.content.Context;
import android.util.Log;

import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

import java.util.Iterator;

/**
 * 根指令处理器，管理全部子处理器并回调处理结果。
 */
public class RootDirectiveHandler extends ChainedHandler {

    private static final String TAG = LogTag.of("RootDirectiveHandler");

    private final Context context;
    private OnCompleteListener completeListener;

    public RootDirectiveHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    public Context getContext() {
        return context;
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.completeListener = listener;
    }

    public OnCompleteListener getOnCompleteListener() {
        return completeListener;
    }

    @Override
    public RootDirectiveHandler addHandler(DirectiveHandler handler, int priority) {
        return (RootDirectiveHandler) super.addHandler(handler, priority);
    }

    @Override
    public RootDirectiveHandler addHandler(DirectiveHandler handler) {
        return addHandler(handler, 0);
    }

    /** 查找指定类型的处理器。 */
    public <T extends DirectiveHandler> T findHandler(Class<T> handlerClass) {
        Iterator<DirectiveHandler> iterator = getHandlers().iterator();
        while (iterator.hasNext()) {
            DirectiveHandler handler = iterator.next();
            if (handlerClass.isInstance(handler)) {
                return (T) handler;
            }
        }
        return null;
    }

    private void notifyComplete(DirectiveRequest request) {
        OnCompleteListener listener = this.completeListener;
        if (listener != null) {
            listener.onComplete(request);
        }
    }

    private void notifyError(DirectiveRequest request, int errorCode) {
        OnCompleteListener listener = this.completeListener;
        if (listener != null) {
            listener.onError(request, errorCode);
        }
    }

    /** 处理指令。 */
    public void process(DirectiveRequest request) {
        process(request, new RootDirectiveCallback(request));
    }

    /**
     * 根处理器回调，把结果转发给 OnCompleteListener。
     */
    protected class RootDirectiveCallback implements DirectiveCallback {

        private final DirectiveRequest request;

        public RootDirectiveCallback(DirectiveRequest request) {
            this.request = request;
        }

        @Override
        public void onNext(DirectiveRequest request) {
            Log.d(TAG, "onNext");
        }

        @Override
        public void onComplete(DirectiveResponse response) {
            Log.d(TAG, "onComplete: response = " + response);
            RootDirectiveHandler.this.notifyComplete(this.request);
        }

        @Override
        public void onError(DirectiveResponse response) {
            Log.d(TAG, "onError: response = " + response);
            RootDirectiveHandler.this.notifyError(this.request, response.getCode());
        }
    }
}