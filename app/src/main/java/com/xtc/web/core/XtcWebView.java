package com.xtc.web.core;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xtc.dataservice.api.SessionConstant;
import com.xtc.log.LogUtil;
import com.xtc.web.core.api.BaseApi;
import com.xtc.web.core.callback.ASRCallback;
import com.xtc.web.core.callback.OnReturnValue;
import com.xtc.web.core.callback.RecordCallback;
import com.xtc.web.core.data.req.ReqNativeCallJs;
import com.xtc.web.core.data.resp.RespNativeCallJs;
import com.xtc.web.core.utils.JSONUtil;
import com.xtc.web.core.utils.WebUtils;

import java.util.ArrayList;
import java.util.Iterator;

/** H5 容器 WebView：负责 JS 桥接、native 调 JS、侧滑返回与内核初始化。 */
public class XtcWebView extends WebView {

    private static final String TAG = CoreConstants.TAG + XtcWebView.class.getSimpleName();

    private ApiManager apiManager;
    private ASRCallback asrCallback;
    private BaseApi baseApi;
    private int callID;
    private ArrayList<ReqNativeCallJs> callInfoList;
    private Context context;
    private float downX;
    private float downY;
    private boolean forbidSwipeToActivity;
    private boolean forbidSwipeToGoback;
    private SparseArray<OnReturnValue> handlerMap;
    private float lastX;
    private float lastY;
    private Handler mainHandler;
    private boolean needGoBack;
    private int touchSlop;

    public XtcWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.callID = 0;
        init(context);
        this.context = context;
    }

    public XtcWebView(Context context) {
        super(context);
        this.callID = 0;
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        if (context == null) {
            return;
        }
        this.callInfoList = new ArrayList<>();
        this.handlerMap = new SparseArray<>();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;
        initWebSetting();
        this.apiManager = new ApiManager(this);
        addJavascriptInterface(this.apiManager, "commonApi");
        addJavascriptObject(this.apiManager, "commonApi");
        this.baseApi = new BaseApi(context);
        addJavascriptObject(this.baseApi, SessionConstant.Source.XTC);
    }

    private void initWebSetting() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(false);
        settings.setLoadWithOverviewMode(true);
        int chromeVersion = WebUtils.getChromeVersion(settings);
        if (chromeVersion >= CoreConstants.ChromeConstant.CHROME_VERSION_86
                || chromeVersion == CoreConstants.ChromeConstant.ERROR_INDEX) {
            LogUtil.d(TAG, "The chrome version is greater than or equal to 86 || ERROR_INDEX：" + chromeVersion);
            setWebChromeClient(new XtcWebChromeClient());
        }
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /** 设置 WebView 缓存模式。 */
    public void setCacheOpen(boolean cacheOpen) {
        LogUtil.d(TAG, "setCacheOpen: isCacheOpen = [" + cacheOpen + "]");
        if (cacheOpen) {
            getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            clearCache(true);
        }
    }

    @Override
    public void loadUrl(final String url) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                XtcWebView.super.loadUrl(url);
            }
        });
    }

    /** 页面初始化完成后，把排队中的 native->JS 调用一次性发出去。 */
    public synchronized void dispatchStartupQueue() {
        if (this.callInfoList != null) {
            Iterator<ReqNativeCallJs> iterator = this.callInfoList.iterator();
            while (iterator.hasNext()) {
                dispatchJavascriptCall(iterator.next());
            }
            this.callInfoList = null;
        }
    }

    public void callHandler(String method, Object[] args) {
        callHandler(method, args, null);
    }

    public <T> void callHandler(String method, OnReturnValue<T> onReturnValue) {
        callHandler(method, null, onReturnValue);
    }

    /** native 调用 JS 方法，可注册返回值回调。 */
    public synchronized <T> void callHandler(String method, Object[] args, OnReturnValue<T> onReturnValue) {
        int callId = this.callID;
        this.callID = callId + 1;
        ReqNativeCallJs request = new ReqNativeCallJs(method, callId, args);
        if (onReturnValue != null) {
            this.handlerMap.put(request.getCallId(), onReturnValue);
        }
        if (this.callInfoList != null) {
            this.callInfoList.add(request);
        } else {
            dispatchJavascriptCall(request);
        }
    }

    private void dispatchJavascriptCall(ReqNativeCallJs request) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", JSONUtil.toJSON(request)));
    }

    private void evaluateJavascript(final String script) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                XtcWebView.super.evaluateJavascript(script, null);
            }
        });
    }

    /** 探测 JS 是否实现了指定方法。 */
    public void hasJavascriptMethod(String method, OnReturnValue<Boolean> onReturnValue) {
        callHandler("_hasJavascriptMethod", new Object[]{method}, onReturnValue);
    }

    /** 注册 JS 命名空间对象。 */
    public void addJavascriptObject(Object object, String name) {
        String namespace = name == null ? "" : name;
        if (object != null) {
            this.apiManager.putApi(namespace, object);
        }
    }

    public void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            this.mainHandler.post(runnable);
        }
    }

    /** 释放 WebView。 */
    public void release() {
        this.mainHandler.removeCallbacksAndMessages(null);
        stopLoading();
        clearHistory();
        removeAllViews();
        removeThis();
        super.destroy();
    }

    private void removeThis() {
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
    }

    /** JS 返回 native 调用结果。 */
    public void returnValue(final Object value) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                RespNativeCallJs response = JSONUtil.fromJSON(value.toString(), RespNativeCallJs.class);
                OnReturnValue onReturnValue = handlerMap.get(response.getId());
                if (onReturnValue != null) {
                    onReturnValue.onValue(response.getData());
                    if (response.isCompleted()) {
                        handlerMap.remove(response.getId());
                    }
                }
            }
        });
    }

    /** 关闭当前页面。 */
    public void closePage(Object value) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Context context = getContext();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    if (activity.isFinishing()) {
                        return;
                    }
                    activity.onBackPressed();
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        this.needGoBack = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN && canGoBack()) {
            for (ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
                if (parent.getClass().getName().equals("com.android.internal.widget.SwipeDismissLayout")) {
                    this.needGoBack = true;
                    parent.requestDisallowInterceptTouchEvent(true);
                    break;
                }
            }
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (this.needGoBack) {
                this.downX = event.getRawX();
                this.downY = event.getRawY();
                this.lastX = this.downX;
                this.lastY = this.downY;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (this.needGoBack) {
                this.lastX = event.getRawX();
                this.lastY = event.getRawY();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (this.needGoBack) {
                float offsetX = this.lastX - this.downX;
                if (offsetX > Math.abs(this.lastY - this.downY) && offsetX > this.touchSlop
                        && !this.forbidSwipeToGoback) {
                    Log.d(TAG, "go back!");
                    goBack();
                }
            }
        } else if (action == MotionEvent.ACTION_CANCEL && this.needGoBack) {
            float offsetX = this.lastX - this.downX;
            if (offsetX > Math.abs(this.lastY - this.downY)) {
                Log.d(TAG, "go back!");
                goBack();
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean isForbidSwipeToGoback() {
        return this.forbidSwipeToGoback;
    }

    public void setForbidSwipeToGoback(boolean forbidSwipeToGoback) {
        this.forbidSwipeToGoback = forbidSwipeToGoback;
    }

    public void setForbidSwipeToActivity(boolean forbidSwipeToActivity) {
        this.forbidSwipeToActivity = forbidSwipeToActivity;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return this.forbidSwipeToActivity;
    }

    public void setAsrCallback(ASRCallback asrCallback) {
        BaseApi baseApi = this.baseApi;
        if (baseApi == null) {
            return;
        }
        this.asrCallback = asrCallback;
        baseApi.setAsrCallback(asrCallback);
    }

    public void setRecordCallBack(RecordCallback recordCallback) {
        BaseApi baseApi = this.baseApi;
        if (baseApi == null) {
            return;
        }
        baseApi.setRecordCallback(recordCallback);
    }

    /** 打印 JS console 日志。 */
    private class XtcWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            LogUtil.d(TAG, "chromium: [INFO:CONSOLE(" + consoleMessage.lineNumber() + ")] \""
                    + consoleMessage.message() + "\", source: " + consoleMessage.sourceId() + " ("
                    + consoleMessage.lineNumber() + ")");
            return true;
        }
    }
}