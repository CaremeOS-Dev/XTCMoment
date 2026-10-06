package com.xtc.moment.util.switchs;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.constants.FunSwitchConstant;
import com.xtc.system.wearswitch.function.FunSwitchUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 功能开关变化监听工具，按 Uri path 分发回调。
 */
public class WeiChatFunSwitchUtil {

    private static final String TAG = "WeiChatFunSwitchUtil";

    private static ContentObserver mContentObserver;
    private static final HashMap<String, List<FunSwitchChangeListener>> funSwitchListenerMap = new HashMap<>();
    private static final Handler dealContentHandler = new Handler(Looper.getMainLooper());

    public interface FunSwitchChangeListener {
        void onChange();
    }

    public static void registerContentObserver(Context context, Uri uri) {
        context.getApplicationContext().getContentResolver()
                .registerContentObserver(uri, true, getContentObserver());
    }

    public static void unRegisterContentObserver(Context context) {
        if (mContentObserver == null) {
            return;
        }
        context.getApplicationContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }

    public static void addFunSwitchChangeListener(Uri uri, FunSwitchChangeListener listener) {
        if (listener == null || uri == null) {
            return;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        List<FunSwitchChangeListener> listeners = funSwitchListenerMap.get(path);
        if (CollectionUtil.isEmpty(listeners)) {
            listeners = new ArrayList<>();
            funSwitchListenerMap.put(path, listeners);
        }
        listeners.add(listener);
    }

    public static void removeFunSwitchChangeListener(Uri uri, FunSwitchChangeListener listener) {
        if (listener == null || uri == null) {
            return;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        List<FunSwitchChangeListener> listeners = funSwitchListenerMap.get(path);
        if (CollectionUtil.isEmpty(listeners)) {
            return;
        }
        listeners.remove(listener);
    }

    private static ContentObserver getContentObserver() {
        if (mContentObserver == null) {
            synchronized (WeiChatFunSwitchUtil.class) {
                mContentObserver = new ContentObserver(dealContentHandler) {
                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        super.onChange(selfChange, uri);
                        dealContentObserverWithUri(uri);
                    }
                };
            }
        }
        return mContentObserver;
    }

    private static void dealContentObserverWithUri(Uri uri) {
        if (uri == null) {
            return;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        LogUtil.i(TAG, "path :" + path);
        if (path.contains(FunSwitchConstant.FUN_SWITCH_URI.getPath())) {
            dispatcherFunSwitchChange(path);
        }
    }

    private static void dispatcherFunSwitchChange(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        List<FunSwitchChangeListener> listeners = funSwitchListenerMap.get(path);
        LogUtil.i(TAG, "dispatcherFunSwitchChange changeUriPath +" + path + " size:" + listeners);
        FunSwitchUtil.clearCache();
        if (CollectionUtil.isEmpty(listeners)) {
            return;
        }
        for (int index = 0; index < listeners.size(); index++) {
            listeners.get(index).onChange();
        }
    }
}