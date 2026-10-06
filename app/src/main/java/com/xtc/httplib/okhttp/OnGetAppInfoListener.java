package com.xtc.httplib.okhttp;

import com.xtc.httplib.bean.AppInfo;

/** Supplies the current {@link AppInfo} to the interceptors. */
public interface OnGetAppInfoListener {
    AppInfo getAppInfo();
}