package com.xtc.domain.config.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.xtc.domain.DomainConfig;
import com.xtc.domain.config.DomainConfigCache;
import com.xtc.domain.config.DomainConfigProvider;
import com.xtc.domain.config.file.FileDomainConfigProvider;
import com.xtc.log.LogUtil;

import java.io.InputStream;

/** Loads the domain config from another package's content provider. */
public class ProviderDomainConfigProvider implements DomainConfigProvider {

    private static final String TAG = "ProviderDomainConfigProvider";
    private static final String SCHEME_CONTENT = "content";

    private final Context context;
    private final String packageName;
    private final ContentResolver contentResolver;
    private final Uri uri;

    public ProviderDomainConfigProvider(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
        this.contentResolver = context.getContentResolver();
        this.uri = new Uri.Builder().scheme(SCHEME_CONTENT).authority(packageName + ".domain-provider")
                .path(FileDomainConfigProvider.PATH).build();
    }

    @Override
    public boolean load(DomainConfigCache cache) {
        if (this.context.getPackageName().equals(this.packageName)) {
            return false;
        }
        try {
            InputStream inputStream = this.contentResolver.openInputStream(this.uri);
            if (inputStream == null) {
                return false;
            }
            DomainConfig config = DomainConfig.fromStream(inputStream);
            if (config == null) {
                return false;
            }
            LogUtil.i(TAG, "config update by provider");
            cache.update(config);
            return true;
        } catch (Throwable ignored) {
            LogUtil.i(TAG, "fetch config failure");
            return false;
        }
    }
}