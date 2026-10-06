package com.xtc.shareapi.share.sharescene;

import android.os.Bundle;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;
import com.xtc.shareapi.share.interfaces.Scene;

/**
 * 好友圈分享场景。
 */
public class Moment implements Scene {

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putInt(OpenApiConstant.SceneConstant.BUNDLE_SCENE_SHARE_TYPE, getType());
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        return this;
    }

    @Override
    public BaseResponse checkArgs() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(1);
        return response;
    }

    @Override
    public String getAppName() {
        return OpenApiConstant.XTCShareAppName.XTC_MOMENT_APP_NAME;
    }

    @Override
    public String getPackageName() {
        return OpenApiConstant.App.MOMENT_PACKAGE_NAME;
    }

    @Override
    public String getTargetClassName() {
        return OpenApiConstant.App.LAUNCHER_MOMENT_ACTIVITY;
    }

    @Override
    public int getType() {
        return TYPE_MOMENT;
    }
}