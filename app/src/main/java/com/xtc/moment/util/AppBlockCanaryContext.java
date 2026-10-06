package com.xtc.moment.util;

import android.content.Context;

import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.system.location.core.LocationRequest;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class AppBlockCanaryContext extends BlockCanaryContext {

    private static final String TAG = "AppBlockCanaryContext";

    private static Integer blockThreshold = 500;

    @Override
    public List<String> concernPackages() {
        return null;
    }

    @Override
    public boolean deleteFilesInWhiteList() {
        return true;
    }

    @Override
    public boolean displayNotification() {
        return false;
    }

    @Override
    public boolean filterNonConcernStack() {
        return false;
    }

    @Override
    public int provideMonitorDuration() {
        return -1;
    }

    @Override
    public String provideNetworkType() {
        return "unknown";
    }

    @Override
    public String provideUid() {
        return LocationRequest.ServiceParams.UID;
    }

    @Override
    public boolean zip(File[] srcFiles, File destFile) {
        return false;
    }

    @Override
    public String provideQualifier() {
        return "block threshold is " + blockThreshold + " ms";
    }

    @Override
    public int provideBlockThreshold() {
        return blockThreshold.intValue();
    }

    @Override
    public int provideDumpInterval() {
        return provideBlockThreshold();
    }

    @Override
    public String providePath() {
        return "/blockcanary/" + provideContext().getPackageName() + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER;
    }

    @Override
    public void upload(File file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> provideWhiteList() {
        LinkedList<String> whiteList = new LinkedList<>();
        whiteList.add("org.chromium");
        return whiteList;
    }

    @Override
    public void onBlock(Context context, BlockInfo blockInfo) {
        LogUtil.e(TAG, blockInfo.toString());
    }
}