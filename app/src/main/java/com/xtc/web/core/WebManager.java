package com.xtc.web.core;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.qiniu.android.common.Constants;
import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.web.core.callback.ASRCallback;
import com.xtc.web.core.callback.RecordCallback;
import com.xtc.web.core.loading.LoadingView;
import com.xtc.web.core.manager.LaunchManager;
import com.xtc.web.core.utils.NetworkUtils;
import com.xtc.web.core.utils.WebUtils;
import com.xtc.web.core.utils.WebViewKernelHookManager;
import com.xtc.web.core.verify.VerifyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/** WebView 容器管理：负责加载 url、加载中/失败/不支持等状态视图、白名单拦截与生命周期释放。 */
public class WebManager {

    private static final String DEFAULT_HTML = "watch/xtc-white-h5/illegal.html";
    private static final String TAG = CoreConstants.TAG + WebManager.class.getSimpleName();
    private static final String VUE_ROUTER = "vue&router.min.js";
    private static final String ENCODING_UTF8 = "UTF-8";
    private static WebManager instance;

    private ASRCallback asrCallback;
    private Activity context;
    private InitWebViewListener initWebViewListener;
    private LoadFailListener loadFailListener;
    private LoadSuccessListener loadSuccessListener;
    private LoadingView loadingView;
    private boolean needDelayRemoveLoading;
    private HashMap<Object, String> pendingJsObjectMap;
    private String pendingUrl;
    private boolean preLoadKernel;
    private PreLoadWebViewKernelListener preLoadWebViewKernelListener;
    private boolean released;
    private RelativeLayout rlNoNetwork;
    private RelativeLayout rlNoSupportWeb;
    private RelativeLayout rlRoot;
    private boolean success;
    private XtcWebView webView;
    private boolean isOpenCheckWhiteList = true;
    private volatile boolean isCacheOpen = true;

    /** WebView 创建完成回调。 */
    public interface InitWebViewListener {
        void onWebViewInit(XtcWebView xtcWebView);
    }

    /** 加载失败回调。 */
    public interface LoadFailListener {
        void onLoadFail(String url);
    }

    /** 加载成功回调。 */
    public interface LoadSuccessListener {
        void onLoadSuccess(String url);
    }

    /** 内核预加载回调。 */
    public interface PreLoadWebViewKernelListener {
        void onPreLoadFail();

        void onPreLoadFinish();
    }

    public static WebManager getInstance(Activity activity, RelativeLayout root) {
        if (instance == null) {
            instance = new WebManager(activity, root);
        }
        return instance;
    }

    public static WebManager hasInit() {
        return instance;
    }

    private WebManager(Activity activity, RelativeLayout root) {
        this.context = activity;
        this.rlRoot = root;
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                isCacheOpen = WatchAccountBase.queryModuleSwitchByBoolean(context,
                        CoreConstants.ModuleSwitch.MODULE_SWITCH_CACHE, true);
                LogUtil.i(TAG, "isCacheOpen:" + isCacheOpen);
                VerifyManager.getInstance(context);
            }
        });
    }

    /** 加载 H5 页面。 */
    public void loadUrl(String url) {
        if (this.context == null) {
            LogUtil.w(TAG, "the activity is null");
            return;
        }
        LaunchManager.launch(url);
        if (WebUtils.checkSupport(this.context)) {
            loadUrlForSupport(url);
        } else {
            addUnSupportView();
        }
    }

    public void setPreLoadKernel(boolean preLoad, PreLoadWebViewKernelListener listener) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.N) {
            LogUtil.i(TAG, "setPreLoadKernel but device version not support version:" + Build.VERSION.SDK_INT);
            return;
        }
        this.preLoadKernel = preLoad;
        this.preLoadWebViewKernelListener = listener;
    }

    private void loadUrlForSupport(String url) {
        Activity activity = this.context;
        if (activity == null) {
            LogUtil.w(TAG, "the activity is null");
            return;
        }
        if (NetworkUtils.isConnected(activity) && !TextUtils.isEmpty(url)) {
            addLoadingView();
            if (this.preLoadKernel) {
                preLoadWebViewKernel(url);
            } else {
                initWebView(url);
            }
            return;
        }
        loadFail(url);
    }

    private void preLoadWebViewKernel(final String url) {
        Activity activity = this.context;
        if (activity == null) {
            LogUtil.w(TAG, "the activity is null");
            return;
        }
        WebViewKernelHookManager.initWebViewKernel(activity,
                new WebViewKernelHookManager.PreLoadWebViewListener() {
                    @Override
                    public void onPreLoadFinish() {
                        initWebView(url);
                        LaunchManager.trace("loadWebViewKernel finish");
                        if (preLoadWebViewKernelListener != null) {
                            preLoadWebViewKernelListener.onPreLoadFinish();
                        }
                    }

                    @Override
                    public void onPreLoadFail() {
                        initWebView(url);
                        LaunchManager.trace("loadWebViewKernel fail");
                        if (preLoadWebViewKernelListener != null) {
                            preLoadWebViewKernelListener.onPreLoadFail();
                        }
                    }
                });
    }

    private void addUnSupportView() {
        Activity activity = this.context;
        if (activity == null) {
            LogUtil.w(TAG, "the activity is null");
            return;
        }
        if (this.rlNoSupportWeb == null) {
            this.rlNoSupportWeb = WebUtils.getUnSupportWebView(activity, this.rlRoot);
        }
        if (this.rlNoSupportWeb.getParent() == null) {
            this.rlRoot.addView(this.rlNoSupportWeb);
        }
    }

    /** 显示加载中遮罩。 */
    public void addLoadingView() {
        LoadingView loadingView = this.loadingView;
        if (loadingView == null || loadingView.getParent() != null) {
            return;
        }
        LogUtil.d(TAG, "start loading!");
        this.loadingView.setLayoutParams(WebUtils.getMatchLayoutParams());
        this.rlRoot.addView(this.loadingView);
        this.loadingView.startLoading();
    }

    private void initWebView(String url) {
        addWebView();
        initPendingJsObject();
        initWebClient();
        LaunchManager.trace("WebView init finish");
        this.webView.loadUrl(url);
    }

    private void addWebView() {
        Activity activity = this.context;
        if (activity == null) {
            LogUtil.w(TAG, "the activity is null");
            return;
        }
        if (this.webView == null) {
            this.webView = new XtcWebView(activity);
            this.webView.setCacheOpen(this.isCacheOpen);
            this.webView.setBackgroundColor(0xFF000000);
            this.webView.setLayoutParams(WebUtils.getMatchLayoutParams());
            this.webView.setVisibility(View.INVISIBLE);
        }
        if (this.webView.getParent() == null) {
            this.rlRoot.addView(this.webView, 0);
        }
        if (this.initWebViewListener != null) {
            this.initWebViewListener.onWebViewInit(this.webView);
        }
    }

    private void initPendingJsObject() {
        HashMap<Object, String> pendingMap = this.pendingJsObjectMap;
        if (pendingMap == null) {
            return;
        }
        for (Map.Entry<Object, String> entry : pendingMap.entrySet()) {
            this.webView.addJavascriptObject(entry.getKey(), entry.getValue());
        }
        this.pendingJsObjectMap.clear();
    }

    private void initWebClient() {
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (!checkUrlValid(request)) {
                    return getDefaultWebResponse();
                }
                WebResourceResponse interceptResponse = getInterceptWebResponse(request);
                return interceptResponse == null ? super.shouldInterceptRequest(view, request) : interceptResponse;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LaunchManager.trace("onPageStart");
                pendingUrl = url;
                success = true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogUtil.e(TAG, "onReceivedError = " + error.getErrorCode() + "description: "
                        + error.getDescription() + " getLocalImage = " + request.getUrl());
                if (request.getUrl().toString().equals(pendingUrl)) {
                    success = false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.d(TAG, "onPageFinished() called with: view = [" + view + "], url = [" + url + "]");
                super.onPageFinished(view, url);
                if (!success) {
                    loadFail(url);
                } else {
                    loadSuccess(url);
                }
                if (!needDelayRemoveLoading) {
                    removeLoadingView(false);
                }
            }
        });
    }

    private boolean checkUrlValid(WebResourceRequest request) {
        boolean urlValid;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Uri url = request.getUrl();
            if (url == null) {
                LogUtil.i(TAG, "uri is empty");
                return false;
            }
            urlValid = VerifyManager.getInstance(this.context).checkUrlIsValid(url.toString(), url.getHost());
        } else {
            urlValid = true;
        }
        if (!VerifyManager.getInstance(this.context).getIntercepterSwitchValue()) {
            return true;
        }
        if (this.isOpenCheckWhiteList) {
            return urlValid;
        }
        Log.i(TAG, "close checkWhiteList release this request");
        return true;
    }

    private WebResourceResponse getDefaultWebResponse() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    BaseUrlManager.getH5Url(this.context) + DEFAULT_HTML).openConnection();
            connection.connect();
            return new WebResourceResponse(connection.getContentType(), connection.getHeaderField("encoding"),
                    connection.getInputStream());
        } catch (Exception e) {
            Log.e(TAG, "getDefaultWebResponse error", e);
            return null;
        }
    }

    private WebResourceResponse getInterceptWebResponse(WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String url = request.getUrl().toString();
            if (url.contains(CoreConstants.FileConstant.FILE_PREFIX)) {
                String localPath = url.replace(CoreConstants.FileConstant.FILE_PREFIX, "");
                LogUtil.i(TAG, "本地图片路径：" + localPath.trim());
                try {
                    WebResourceResponse response = new WebResourceResponse(getMimeType(localPath), ENCODING_UTF8,
                            new FileInputStream(new File(localPath.trim())));
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("access-control-allow-origin", "*");
                    response.setResponseHeaders(headers);
                    return response;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                String lastPathSegment = request.getUrl().getLastPathSegment();
                WebResourceResponse response = VUE_ROUTER.equals(lastPathSegment)
                        ? interceptCommonJs(lastPathSegment)
                        : null;
                if (response != null) {
                    LogUtil.d(TAG, "intercept request success!");
                    return response;
                }
            }
        }
        return null;
    }

    public void setASRCallback(ASRCallback asrCallback) {
        this.asrCallback = asrCallback;
        setWebViewASRCallback();
    }

    public void setRecordCallback(RecordCallback recordCallback) {
        XtcWebView xtcWebView = this.webView;
        if (xtcWebView == null) {
            LogUtil.i(TAG, "setRecordCallback error, webView is null");
        } else {
            xtcWebView.setRecordCallBack(recordCallback);
        }
    }

    private void setWebViewASRCallback() {
        XtcWebView xtcWebView = this.webView;
        if (xtcWebView == null) {
            return;
        }
        xtcWebView.setAsrCallback(this.asrCallback);
    }

    private String getMimeType(String path) {
        String fileExtension = WebUtils.getFileExtension(path);
        LogUtil.i(TAG, "getMimeType:" + fileExtension);
        if (CoreConstants.FileType.TYPE_AMR.equals(fileExtension)
                || CoreConstants.FileType.TYPE_AAC.equals(fileExtension)) {
            return CoreConstants.MimeType.TYPE_AMR;
        }
        return null;
    }

    /** 拦截内置的 vue router js，改为从 assets 读取。 */
    private WebResourceResponse interceptCommonJs(String fileName) {
        Activity activity = this.context;
        if (activity == null) {
            LogUtil.w(TAG, "the activity is null");
            return null;
        }
        try {
            return new WebResourceResponse("text/javascript", Constants.UTF_8, activity.getAssets().open(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "intercept common js error = " + e);
            return null;
        }
    }

    /** 移除加载中遮罩。 */
    public void removeLoadingView(boolean showWebView) {
        XtcWebView xtcWebView = this.webView;
        if (xtcWebView != null && showWebView) {
            xtcWebView.setVisibility(View.VISIBLE);
        }
        LoadingView loadingView = this.loadingView;
        if (loadingView == null || loadingView.getParent() == null) {
            return;
        }
        LogUtil.d(TAG, "remove loading!");
        this.loadingView.stopLoading();
        this.rlRoot.removeView(this.loadingView);
    }

    private void loadFail(String url) {
        XtcWebView xtcWebView = this.webView;
        if (xtcWebView != null) {
            xtcWebView.setVisibility(View.INVISIBLE);
        }
        addErrorView();
        if (this.loadFailListener != null) {
            this.loadFailListener.onLoadFail(url);
        }
    }

    /** 显示加载失败重试页。 */
    public void addErrorView() {
        Activity activity = this.context;
        if (activity == null) {
            LogUtil.w(TAG, "the activity is null");
            return;
        }
        if (this.rlNoNetwork == null) {
            this.rlNoNetwork = WebUtils.getNoNetWork(activity, this.rlRoot, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rlRoot.removeView(rlNoNetwork);
                    loadUrl(pendingUrl);
                }
            });
        }
        if (this.rlNoNetwork.getParent() == null) {
            this.rlRoot.addView(this.rlNoNetwork);
        }
    }

    private void loadSuccess(String url) {
        LaunchManager.trace("onPageFinished");
        this.webView.setVisibility(View.VISIBLE);
        if (this.loadSuccessListener != null) {
            this.loadSuccessListener.onLoadSuccess(url);
        }
    }

    public XtcWebView getWebView() {
        return this.webView;
    }

    public boolean isCacheOpen() {
        return this.isCacheOpen;
    }

    /** 释放容器与 WebView。 */
    public void release() {
        this.released = true;
        this.initWebViewListener = null;
        this.loadFailListener = null;
        this.loadSuccessListener = null;
        this.preLoadWebViewKernelListener = null;
        instance = null;
        this.context = null;
        this.loadingView = null;
        XtcWebView xtcWebView = this.webView;
        if (xtcWebView != null) {
            xtcWebView.release();
        }
    }

    /** 注册 JS 对象，WebView 尚未创建时先缓存。 */
    public void addJavascriptObject(Object object, String name) {
        XtcWebView xtcWebView = this.webView;
        if (xtcWebView != null) {
            xtcWebView.addJavascriptObject(object, name);
            return;
        }
        if (this.pendingJsObjectMap == null) {
            this.pendingJsObjectMap = new HashMap<>();
        }
        this.pendingJsObjectMap.put(object, name);
    }

    public void setInitWebViewListener(InitWebViewListener initWebViewListener) {
        this.initWebViewListener = initWebViewListener;
    }

    public void setLoadFailListener(LoadFailListener loadFailListener) {
        this.loadFailListener = loadFailListener;
    }

    public void setLoadSuccessListener(LoadSuccessListener loadSuccessListener) {
        this.loadSuccessListener = loadSuccessListener;
    }

    public void setLoadingView(LoadingView loadingView) {
        this.loadingView = loadingView;
    }

    public boolean isReleased() {
        return this.released;
    }

    public void delayRemoveLoading(boolean delay) {
        this.needDelayRemoveLoading = delay;
    }

    public void setOpenCheckWhiteList(boolean openCheckWhiteList) {
        Log.i(TAG, "setOpenCheckWhiteList, isOpenCheckWhiteList = " + openCheckWhiteList);
        this.isOpenCheckWhiteList = openCheckWhiteList;
    }
}