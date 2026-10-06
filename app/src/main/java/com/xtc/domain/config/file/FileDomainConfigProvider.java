package com.xtc.domain.config.file;

import android.content.Context;
import android.os.SystemClock;

import com.xtc.domain.DomainConfig;
import com.xtc.domain.config.DomainConfigCache;
import com.xtc.domain.config.DomainConfigProvider;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.FolderManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

/** Loads the domain config from the recovery directory. */
public class FileDomainConfigProvider implements DomainConfigCache, DomainConfigProvider {

    public static final String PATH = "domain/domain.config";
    private static final String TAG = "FileDomainConfigProvider";
    private static final String CONFIG_NAME = "last_AppConfig";
    private static final long MAX_AGE_MILLIS = TimeUnit.DAYS.toMillis(1);

    private final File file;

    private static File defaultFile() {
        return new File(FolderManager.getInstance().getRecoveryDir(), CONFIG_NAME);
    }

    /** Writes the config into the default location. */
    public static boolean save(DomainConfig domainConfig) {
        return DomainConfig.writeToFile(defaultFile(), domainConfig);
    }

    public FileDomainConfigProvider(Context context, String packageName) {
        if (context.getPackageName().equals(packageName)) {
            this.file = defaultFile();
        } else {
            this.file = new File(context.getCacheDir(), PATH);
        }
    }

    private boolean isWrittenThisBoot() {
        return this.file.lastModified() > System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    private boolean isExpired() {
        return System.currentTimeMillis() - this.file.lastModified() > MAX_AGE_MILLIS;
    }

    @Override
    public boolean load(DomainConfigCache cache) {
        DomainConfig config = DomainConfig.fromFile(this.file);
        if (config != null) {
            LogUtil.i(TAG, "config update by local file");
            cache.update(config);
        }
        if (config != null && isWrittenThisBoot()) {
            return !isExpired();
        }
        return false;
    }

    @Override
    public void update(DomainConfig domainConfig) {
        LogUtil.i(TAG, "update config: " + domainConfig);
        DomainConfig.writeToFile(this.file, domainConfig);
    }
}