package com.xtc.aitext.net;

import com.xtc.log.LogUtil;

import rx.functions.Action1;

/**
 * 网络错误统一处理 Action。
 */
public class AINetErrorAction implements Action1<Throwable> {

    private static final String TAG = "ai_text_PostNetErrorAction";

    private final ErrorCallback errorCallback;
    private final String methodName;
    private final Class<?> callerClass;

    /** 错误回调。 */
    public interface ErrorCallback {
        void onError(Throwable throwable);
    }

    public AINetErrorAction(Class<?> callerClass, ErrorCallback errorCallback, String methodName) {
        this.errorCallback = errorCallback;
        this.callerClass = callerClass;
        this.methodName = methodName;
    }

    @Override
    public void call(Throwable throwable) {
        if (this.errorCallback != null) {
            LogUtil.e(TAG, callerClass.getName() + "." + methodName, throwable);
            this.errorCallback.onError(throwable);
        }
    }
}