package com.xtc.moment.serve.bean;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;

/**
 * 当前手表账号信息。
 */
public class WatchAccountInfo {

    private String watchId;
    private String name;
    private String icon;

    public String getWatchId(Context context) {
        if (TextUtils.isEmpty(this.watchId)) {
            this.watchId = MomentApp.getWatchId();
        }
        return this.watchId;
    }

    public String getName(Context context) {
        if (!TextUtils.isEmpty(this.name)) {
            return this.name;
        }
        if (context == null) {
            context = MomentApp.getAppContext();
        }
        if (context == null) {
            context = ContextUtils.getContext();
        }
        return context == null ? "" : context.getString(R.string.unknown_watch);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "WatchAccountInfo{, name='" + this.name + "', icon='" + this.icon + "'}";
    }
}