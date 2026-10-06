package com.xtc.shareapi.share.interfaces;

import android.net.Uri;

/**
 * 监听本应用 apk 信息变化的回调。
 */
public interface IObserverChangeListener {

    /** apk 观察者收到变更通知。 */
    void onApkObserverChange(Uri uri);
}