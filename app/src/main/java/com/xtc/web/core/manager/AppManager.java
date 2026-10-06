package com.xtc.web.core.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.AppInfo;
import com.xtc.web.core.data.resp.RespAppInfo;

/** 查询指定包名的安装信息并回调给 H5。 */
public class AppManager {

    /** 查询应用信息。 */
    public void request(Context context, String packageName, CompletionHandler<RespAppInfo> completionHandler) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfo.setVersionCode(packageInfo.versionCode);
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setAppName(String.valueOf(packageManager.getApplicationLabel(packageInfo.applicationInfo)));
            RespAppInfo response = new RespAppInfo();
            response.setCode(RespAppInfo.Code.SUCCESS);
            response.setData(appInfo);
            completionHandler.complete(response);
        } catch (PackageManager.NameNotFoundException ignored) {
            RespAppInfo response = new RespAppInfo();
            response.setCode(RespAppInfo.Code.NOT_EXIST);
            completionHandler.complete(response);
        }
    }
}