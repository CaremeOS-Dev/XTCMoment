package com.xtc.shareapi.share.manager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.bean.AppInfo;
import com.xtc.shareapi.share.bean.ModuleSwitch;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.utils.BitmapUtil;
import com.xtc.shareapi.share.utils.ShareUtil;

/**
 * 分享策略抽象基类，负责组装分享 Intent 并提供各版本策略的差异化能力。
 */
public abstract class ShareStrategy {

    public final String TAG = OpenApiConstant.TAG + getClass().getSimpleName();
    public Context context;
    public String appName;
    public byte[] appIcon;

    public ShareStrategy(Context context, String appName, byte[] appIcon) {
        this.context = context;
        this.appName = appName;
        this.appIcon = appIcon;
    }

    /** 是否满足基础版本要求。 */
    public abstract boolean checkBaseVersion(int sceneType);

    /** 查询模块开关状态。 */
    public abstract ModuleSwitch getModuleSwitch(Context context, int sceneType);

    /** 获取目标 Activity 类名。 */
    public abstract String getTargetClassName(int sceneType);

    /** 从 XTC 查询应用 token。 */
    public abstract AppInfo getTokenFromXTC(Context context, Scene scene);

    /** 设置目标 Activity。 */
    public abstract boolean setClassName(Intent intent, Scene scene);

    /** 执行分享。 */
    public abstract void share(SendMessageToXTC.Request request, String appKey);

    /** 由请求组装分享 Intent。 */
    public Intent getShareIntentFromRequest(String appKey, SendMessageToXTC.Request request) {
        String appName = this.appName;
        if (TextUtils.isEmpty(appName)) {
            appName = ShareUtil.getAppName(context);
        }
        byte[] iconData = this.appIcon;
        if (iconData == null || iconData.length <= 0) {
            iconData = BitmapUtil.bitmapToByteArray(ShareUtil.getBitmap(context));
        }
        if (iconData == null) {
            Log.e(TAG, "app icon is null!");
            return null;
        }
        Log.d(TAG, "app icon size is = " + iconData.length);
        if (iconData.length > BitmapUtil.THUMB_LENGTH) {
            Log.e(TAG, "app icon data too large!");
            return null;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        request.toBundle(bundle);
        Scene scene = request.getScene();
        if (scene != null && !setClassName(intent, scene)) {
            Log.e(TAG, "set class name error!");
            return null;
        }
        intent.putExtras(bundle);
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_VERSION, OpenApiConstant.SdkVersionCode.SDK_VERSION_CODE);
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_PACKAGE, context.getPackageName());
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_CLASSNAME, context.getClass().getName());
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_NAME, appName);
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_ICON, iconData);
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_KEY, appKey);
        intent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_JUMP_FLAG, request.getFlag());
        return intent;
    }

    /** 从 token 查询结果中读取应用信息。 */
    public void setAppInfo(AppInfo appInfo, Cursor cursor) {
        int packageColumn = cursor.getColumnIndex(OpenApiConstant.TokenConstant.SHARE_APP_PACKAGE);
        int allowColumn = cursor.getColumnIndex(OpenApiConstant.TokenConstant.SHARE_APP_ALLOW);
        int tokenColumn = cursor.getColumnIndex(OpenApiConstant.TokenConstant.SHARE_APP_TOKEN);
        appInfo.setPackageName(cursor.getString(packageColumn));
        appInfo.setAllow(cursor.getInt(allowColumn));
        appInfo.setToken(cursor.getString(tokenColumn));
    }
}