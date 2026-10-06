package com.xtc.web.core.utils;

import android.app.Activity;
import android.content.Context;

import com.xtc.log.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/** 在 25 版设备上提前拉起 WebView 内核（通过反射触发内核初始化），缩短首屏时间。 */
public class WebViewKernelHookManager {

    private static final String TAG = "WebViewKernelHookManager";

    /** 内核预加载结果回调。 */
    public interface PreLoadWebViewListener {
        void onPreLoadFail();

        void onPreLoadFinish();
    }

    /** 异步预加载 WebView 内核，并在主线程回调结果。 */
    public static void initWebViewKernel(final Activity activity, final PreLoadWebViewListener listener) {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return Boolean.valueOf(preLoadWebViewKernel(activity.getApplicationContext()));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        if (activity.isDestroyed() || activity.isFinishing()) {
                            LogUtil.i(TAG, "initWebViewKernel but activity is destroyed,don't deal");
                            return;
                        }
                        if (listener == null) {
                            return;
                        }
                        if (success.booleanValue()) {
                            listener.onPreLoadFinish();
                        } else {
                            listener.onPreLoadFail();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (listener == null) {
                            return;
                        }
                        listener.onPreLoadFail();
                    }
                });
    }

    /** 反射调用 WebViewFactory 的 provider 初始化入口，触发内核加载。 */
    private static boolean preLoadWebViewKernel(Context context) {
        try {
            Class<?> webViewFactoryClass = Class.forName("android.webkit.WebViewFactory");
            Method getProviderMethod = webViewFactoryClass.getDeclaredMethod("getProvider", new Class[0]);
            getProviderMethod.setAccessible(true);
            Object provider = getProviderMethod.invoke(webViewFactoryClass, new Object[0]);
            Class<?> providerClass = provider.getClass();
            Method startChromiumMethod = providerClass.getDeclaredMethod("a", Boolean.TYPE);
            startChromiumMethod.setAccessible(true);
            startChromiumMethod.invoke(provider, true);
            Field awInitField = providerClass.getDeclaredField("mAwInit");
            Object awInit = awInitField.get(provider);
            Method initMethod = awInitField.getType().getDeclaredMethod("b", Boolean.TYPE);
            initMethod.setAccessible(true);
            initMethod.invoke(awInit, true);
            return true;
        } catch (Exception e) {
            LogUtil.i(TAG, "preLoadWebViewResource", e);
            return false;
        }
    }
}