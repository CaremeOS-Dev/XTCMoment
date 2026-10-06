package com.xtc.web.client;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.stmt.query.SimpleComparison;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.WatchModelUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;
import com.xtc.web.client.api.XTCWatchApi;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.manager.JsEruptManager;
import com.xtc.web.client.manager.JsPushManager;
import com.xtc.web.client.manager.UrlCacheManager;
import com.xtc.web.client.manager.WakeLockManager;
import com.xtc.web.client.utils.AsyncThreadUtil;
import com.xtc.web.client.utils.FileUtil;
import com.xtc.web.core.WebManager;
import com.xtc.web.core.XtcWebView;
import com.xtc.web.core.manager.SimpleShakeManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/** web.client 对外入口：为 H5 页面挂载 XTCWatchApi、解析入口 url 并管理生命周期。 */
public class WebClientManager {

    private static final String TAG = Constants.TAG + WebClientManager.class.getSimpleName();

    private Context context;
    private UrlCacheManager urlCacheManager;
    private WebManager webManager;

    public WebClientManager(WebManager webManager, final Context context, String databaseName) {
        this.webManager = webManager;
        this.context = context;
        this.webManager.addJavascriptObject(new XTCWatchApi(context), "");
        UrlCacheManager.dbName = databaseName;
        this.webManager.setInitWebViewListener(new WebManager.InitWebViewListener() {
            @Override
            public void onWebViewInit(XtcWebView xtcWebView) {
                setWebViewWithManager(xtcWebView, context);
            }
        });
    }

    /** WebView 创建完成后把实例分发给各 JS 管理器。 */
    private void setWebViewWithManager(final XtcWebView xtcWebView, final Context context) {
        AsyncThreadUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                JsPushManager.getInstance().setXtcWebView(xtcWebView);
                JsEruptManager.getInstance(context.getApplicationContext()).setXtcWebView(xtcWebView);
                SimpleShakeManager.getInstance(context.getApplicationContext()).setXtcWebView(xtcWebView);
                LogUtil.d(TAG, "setWebViewWithManager");
            }
        });
    }

    /** 直接加载指定 url。 */
    public void requestFromUrl(String url) {
        this.webManager.loadUrl(url);
    }

    public void requestFromType(int type) {
        requestFromType(type, null);
    }

    /** 按入口类型（对应 DbWebUrl.type）解析 url 后加载。 */
    public void requestFromType(final int type, final HashMap<String, String> params) {
        this.webManager.addLoadingView();
        Observable.just(Boolean.valueOf(this.urlCacheManager != null))
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean initialized) {
                        if (!initialized.booleanValue()) {
                            urlCacheManager = UrlCacheManager.getInstance(context.getApplicationContext());
                        }
                        return Boolean.valueOf(true);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<String>>() {
                    @Override
                    public Observable<String> call(Boolean value) {
                        return urlCacheManager.getWebUrl(type);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String url) {
                        getUrlSuccess(url, params);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getUrlFail(throwable);
                    }
                });
    }

    public void setPreLoadWebViewKernel(boolean preLoad, WebManager.PreLoadWebViewKernelListener listener) {
        this.webManager.setPreLoadKernel(preLoad, listener);
    }

    private void getUrlSuccess(String url, HashMap<String, String> params) {
        LogUtil.d(TAG, "get url success = " + url);
        if (!this.webManager.isReleased()) {
            this.webManager.loadUrl(addParams(url, params));
        }
        this.urlCacheManager.updateUrlCache();
    }

    /** 追加语言等公共参数。 */
    private String addParams(String url, HashMap<String, String> params) {
        HashMap<String, String> queryParams = params;
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }
        if (!queryParams.containsKey(Constants.LANGUAGE)) {
            String language = WatchModelUtil.getLanguage();
            String country = Locale.getDefault().getCountry();
            if (!TextUtils.isEmpty(country)) {
                language = language + ScreenshotUtils.SEPARATOR + country;
            }
            queryParams.put(Constants.LANGUAGE, language);
        }
        boolean firstParam = !url.contains("?");
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            url = url.concat(firstParam ? "?" : "&")
                    .concat(entry.getKey() + SimpleComparison.EQUAL_TO_OPERATION + entry.getValue());
            firstParam = false;
        }
        return url;
    }

    private void getUrlFail(Throwable throwable) {
        throwable.printStackTrace();
        LogUtil.e(TAG, "get url fail = ", throwable);
        if (!this.webManager.isReleased()) {
            this.webManager.addErrorView();
        }
        this.urlCacheManager.updateUrlCache();
    }

    /** 释放所有 JS 管理器与 WebView，并按需清理缓存。 */
    public void release() {
        LogUtil.i(TAG, "webClientManager release");
        JsPushManager.getInstance().release();
        JsEruptManager.getInstance(this.context).release();
        SimpleShakeManager.getInstance(this.context).release();
        WakeLockManager.getInstance(this.context).release();
        AsyncThreadUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (FileUtil.isOverLimit(context) || !webManager.isCacheOpen()) {
                    FileUtil.deleteWebViewCache(context);
                }
                AsyncThreadUtil.removeCallbacksAndMessages();
            }
        });
        this.webManager.release();
    }
}