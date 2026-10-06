package com.xtc.shareapi.share.manager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.bean.AppInfo;
import com.xtc.shareapi.share.bean.ModuleSwitch;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.utils.ShareUtil;
import com.xtc.shareapi.share.view.PopWindowManager;

/**
 * 分享策略 v1：通过 launcher 的分享入口跳转，token 从内容提供者查询。
 */
public class StrategyVersionOne extends ShareStrategy {

    StrategyVersionOne(Context context, String appName, byte[] appIcon) {
        super(context, appName, appIcon);
    }

    @Override
    public void share(SendMessageToXTC.Request request, String appKey) {
        Scene scene = request.getScene();
        if (scene == null) {
            Log.e(TAG, "scene is null!");
            PopWindowManager.getInstance(context).showChooseSceneWindow(appKey, request, this);
            return;
        }
        Intent shareIntent = getShareIntentFromRequest(appKey, request);
        if (shareIntent == null) {
            Log.e(TAG, "get share intent error!");
            ShareUtil.startTargetActivity(context, 6, "get share intent error!");
            return;
        }
        ModuleSwitch moduleSwitch = getModuleSwitch(context, scene.getType());
        if (moduleSwitch != null && !moduleSwitch.isModule()) {
            Log.e(TAG, "current moduleSwitch is not open !");
            ShareUtil.startTargetActivity(context, 10, "current moduleSwitch is not open!");
        } else {
            shareIntent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_TOKEN, getTokenFromXTC(context, null).getToken());
            context.startActivity(shareIntent);
        }
    }

    @Override
    public boolean setClassName(Intent intent, Scene scene) {
        String targetClassName = getTargetClassName(scene.getType());
        if (TextUtils.isEmpty(targetClassName)) {
            Log.e(TAG, "get target class name error!");
            return false;
        }
        intent.setClassName(OpenApiConstant.App.LAUNCHER, targetClassName);
        return true;
    }

    @Override
    public ModuleSwitch getModuleSwitch(Context context, int sceneType) {
        ModuleSwitch moduleSwitch = new ModuleSwitch();
        Bundle args = new Bundle();
        args.putInt("scene", sceneType);
        Bundle result = context.getContentResolver().call(Uri.parse(OpenApiConstant.ModuleSwitch.SHARE_APP_MODULE),
                OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_OPEN, null, args);
        if (result != null) {
            boolean isOpen = result.getBoolean(OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_OPEN);
            String tip = result.getString(OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_TIP);
            moduleSwitch.setModule(isOpen);
            moduleSwitch.setTip(tip);
            Log.d(TAG, "query module result = " + isOpen);
        }
        return moduleSwitch;
    }

    @Override
    public AppInfo getTokenFromXTC(Context context, Scene scene) {
        AppInfo appInfo = new AppInfo();
        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse(OpenApiConstant.TokenConstant.SHARE_APP_URI),
                    null, OpenApiConstant.TokenConstant.QUERY_SELECTION, new String[]{context.getPackageName()}, null);
            if (cursor == null) {
                Log.d(TAG, "cursor is null!");
                return appInfo;
            }
            if (cursor.moveToNext()) {
                setAppInfo(appInfo, cursor);
            }
            Log.d(TAG, "get app info is " + appInfo);
            cursor.close();
            return appInfo;
        } catch (Exception e) {
            Log.e(TAG, "query app info error = " + e);
            return appInfo;
        }
    }

    @Override
    public boolean checkBaseVersion(int sceneType) {
        return !TextUtils.isEmpty(getTargetClassName(sceneType));
    }

    @Override
    public String getTargetClassName(int sceneType) {
        if (sceneType == Scene.TYPE_CHAT) {
            return ShareUtil.getTargetClassName(context, OpenApiConstant.App.LAUNCHER, OpenApiConstant.App.LAUNCHER_CHAT_ACTIVITY);
        }
        if (sceneType == Scene.TYPE_MOMENT) {
            return ShareUtil.getTargetClassName(context, OpenApiConstant.App.LAUNCHER, OpenApiConstant.App.LAUNCHER_MOMENT_ACTIVITY);
        }
        return null;
    }
}