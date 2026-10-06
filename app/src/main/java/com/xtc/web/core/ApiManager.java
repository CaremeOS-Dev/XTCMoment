package com.xtc.web.core;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.xtc.log.LogUtil;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqJsBase;
import com.xtc.web.core.data.req.ReqSupportMethod;
import com.xtc.web.core.data.resp.RespJsBase;
import com.xtc.web.core.utils.JSONUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/** JS 桥接核心：把 JS 的 call(nameSpace.method, args) 反射分发到注册的 native 对象。 */
public class ApiManager {

    private static final int API_VERSION = 5;
    private static final String TAG = CoreConstants.TAG + ApiManager.class.getSimpleName();
    /** 方法类型：同步 / 异步 / 全部。 */
    private static final String TYPE_ALL = "all";
    private static final String TYPE_ASYNC = "async";
    private static final String TYPE_SYNC = "sync";

    private Map<String, Object> javaScriptNamespaceInterfaces = new HashMap<>();
    private XtcWebView xtcWebView;

    public ApiManager(XtcWebView xtcWebView) {
        this.xtcWebView = xtcWebView;
    }

    @JavascriptInterface
    public int getSdkVersion(Object arg) {
        return API_VERSION;
    }

    @JavascriptInterface
    public void canWindowSwipeToDimiss(Object arg) {
        LogUtil.i(TAG, "canWindowSwipe" + arg);
        if (arg.equals(Integer.valueOf(0))) {
            this.xtcWebView.setForbidSwipeToGoback(true);
        } else {
            this.xtcWebView.setForbidSwipeToGoback(false);
        }
    }

    @JavascriptInterface
    public void canWindowSwipeToActivity(Object arg) {
        LogUtil.i(TAG, "canWindowSwipeToActivity" + arg);
        if (arg.equals(Integer.valueOf(0))) {
            this.xtcWebView.setForbidSwipeToActivity(true);
        } else {
            this.xtcWebView.setForbidSwipeToActivity(false);
        }
    }

    /** 探测 native 是否实现了指定方法（name 形如 namespace.method）。 */
    @JavascriptInterface
    public boolean hasNativeMethod(Object arg) {
        ReqSupportMethod reqSupportMethod = JSONUtil.fromJSON(arg.toString(), ReqSupportMethod.class);
        String[] namespace = parseNamespace(reqSupportMethod.getName());
        Object apiObject = this.javaScriptNamespaceInterfaces.get(namespace[0]);
        if (apiObject == null) {
            return false;
        }
        Class<?> apiClass = apiObject.getClass();
        Method method;
        boolean isAsyncMethod;
        try {
            method = apiClass.getMethod(namespace[1], Object.class, CompletionHandler.class);
            isAsyncMethod = true;
        } catch (Exception e) {
            try {
                method = apiClass.getMethod(namespace[1], Object.class);
            } catch (Exception inner) {
                method = null;
            }
            isAsyncMethod = false;
        }
        if (method == null || method.getAnnotation(JavascriptInterface.class) == null) {
            return false;
        }
        String type = reqSupportMethod.getType();
        return TYPE_ALL.equals(type) || (isAsyncMethod && TYPE_ASYNC.equals(type))
                || (!isAsyncMethod && TYPE_SYNC.equals(type));
    }

    @JavascriptInterface
    public void closePage(Object arg) {
        this.xtcWebView.closePage(arg);
    }

    @JavascriptInterface
    public void dsInit(Object arg) {
        this.xtcWebView.dispatchStartupQueue();
    }

    @JavascriptInterface
    public void returnValue(Object arg) {
        this.xtcWebView.returnValue(arg);
    }

    /** JS 调用 native 的统一入口，返回 JSON 结果字符串。 */
    @JavascriptInterface
    public String call(String methodPath, String argumentJson) {
        String[] namespace = parseNamespace(methodPath.trim());
        String methodName = namespace[1];
        Object apiObject = this.javaScriptNamespaceInterfaces.get(namespace[0]);
        RespJsBase response = new RespJsBase();
        if (apiObject == null) {
            Log.e(TAG, "the nameSpace " + namespace[0] + "not register api!");
            response.setCode(RespJsBase.Code.NO_API);
            response.setDesc("the nameSpace not register api!");
            return JSONUtil.toJSON(response);
        }
        ReqJsBase request = ReqJsBase.parseValue(argumentJson);
        Class<?> apiClass = apiObject.getClass();
        Method method;
        boolean isAsyncMethod;
        try {
            method = apiClass.getMethod(methodName, Object.class, CompletionHandler.class);
            isAsyncMethod = true;
        } catch (Exception e) {
            try {
                method = apiClass.getMethod(methodName, Object.class);
            } catch (Exception inner) {
                method = null;
            }
            isAsyncMethod = false;
        }
        if (method == null) {
            Log.e(TAG, "not found this method = " + methodName);
            response.setCode(RespJsBase.Code.NO_METHOD);
            response.setDesc("not found this method!");
            return JSONUtil.toJSON(response);
        }
        if (method.getAnnotation(JavascriptInterface.class) == null) {
            Log.e(TAG, "this method no annotation!");
            response.setCode(RespJsBase.Code.NO_ANNOTATION);
            response.setDesc("this method no annotation!");
            return JSONUtil.toJSON(response);
        }
        method.setAccessible(true);
        try {
            if (isAsyncMethod) {
                final String callbackName = request.getCallbackName();
                method.invoke(apiObject, request.getReqArg(), new CompletionHandler() {
                    @Override
                    public void complete() {
                        complete(null, true);
                    }

                    @Override
                    public void complete(Object data) {
                        complete(data, true);
                    }

                    @Override
                    public void setProgressData(Object data) {
                        complete(data, false);
                    }

                    private void complete(Object data, boolean finished) {
                        RespJsBase result = new RespJsBase();
                        result.setCode(RespJsBase.Code.SUCCESS);
                        result.setDesc("success");
                        result.setData(data);
                        if (callbackName != null) {
                            String script = String.format("%s(%s.data);", callbackName, JSONUtil.toJSON(result));
                            if (finished) {
                                script = script + "delete window." + callbackName;
                            }
                            evaluateJavascript(script);
                        }
                    }
                });
                response.setCode(RespJsBase.Code.SUCCESS);
                response.setDesc("success");
                return JSONUtil.toJSON(response);
            }
            Object returnValue = method.invoke(apiObject, request.getReqArg());
            response.setCode(RespJsBase.Code.SUCCESS);
            response.setDesc("success");
            response.setData(returnValue);
            return JSONUtil.toJSON(response);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "call native error = " + e);
            response.setCode(RespJsBase.Code.CALL_FAIL);
            response.setDesc(e.toString());
            return JSONUtil.toJSON(response);
        }
    }

    /** 把 "namespace.method" 拆成 {namespace, method}。 */
    private String[] parseNamespace(String methodPath) {
        int lastDotIndex = methodPath.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return new String[]{methodPath.substring(0, lastDotIndex), methodPath.substring(lastDotIndex + 1)};
        }
        return new String[]{"", methodPath};
    }

    private void evaluateJavascript(final String script) {
        this.xtcWebView.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                xtcWebView.evaluateJavascript(script, null);
            }
        });
    }

    /** 注册 JS 命名空间对象，同名不同类时直接抛异常。 */
    public void putApi(String namespace, Object apiObject) {
        if (this.javaScriptNamespaceInterfaces.containsKey(namespace)
                && !apiObject.getClass().equals(this.javaScriptNamespaceInterfaces.get(namespace).getClass())) {
            throw new RuntimeException("You already have an API with the same namespace!");
        }
        this.javaScriptNamespaceInterfaces.put(namespace, apiObject);
    }
}