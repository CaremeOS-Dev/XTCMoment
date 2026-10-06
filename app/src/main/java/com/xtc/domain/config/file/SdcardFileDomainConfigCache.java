package com.xtc.domain.config.file;

import android.content.Context;

import com.xtc.domain.DomainConfig;
import com.xtc.domain.config.DomainConfigCache;
import com.xtc.domain.config.DomainConfigProvider;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.FolderManager;

import java.io.File;

/** Persists the domain config to the AppConfig file. */
public class SdcardFileDomainConfigCache implements DomainConfigCache, DomainConfigProvider {

    private static final String TAG = "SdcardFileDomainConfigCache";

    private final Context context;
    private final String packageName;
    private final File file = new File(FolderManager.getInstance().getConfigFilePath());

    public SdcardFileDomainConfigCache(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    @Override
    public boolean load(DomainConfigCache cache) {
        return false;
    }

    @Override
    public void update(DomainConfig domainConfig) {
        if (this.context.getPackageName().equals(this.packageName)) {
            LogUtil.i(TAG, "update: " + domainConfig);
            DomainConfig.writeToFile(this.file, domainConfig);
        }
    }
}