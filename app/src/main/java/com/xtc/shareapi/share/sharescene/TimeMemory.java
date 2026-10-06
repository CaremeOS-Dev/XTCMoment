package com.xtc.shareapi.share.sharescene;

import android.os.Bundle;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;
import com.xtc.shareapi.share.interfaces.Scene;

/**
 * 时光记忆分享场景（占位实现，仅提供类型）。
 */
public class TimeMemory implements Scene {

    @Override
    public void toBundle(Bundle bundle) {
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        return null;
    }

    @Override
    public BaseResponse checkArgs() {
        return null;
    }

    @Override
    public String getAppName() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public String getTargetClassName() {
        return null;
    }

    @Override
    public int getType() {
        return TYPE_TIME_MEMORY;
    }
}