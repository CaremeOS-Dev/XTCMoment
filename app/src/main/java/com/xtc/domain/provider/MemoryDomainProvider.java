package com.xtc.domain.provider;

import android.content.Context;

import com.xtc.domain.Domain;
import com.xtc.domain.DomainConfig;
import com.xtc.domain.config.ChainedDomainConfigProvider;
import com.xtc.domain.config.DomainConfigCache;
import com.xtc.domain.config.DomainConfigProvider;
import com.xtc.domain.config.file.FileDomainConfigProvider;
import com.xtc.domain.config.file.SdcardFileDomainConfigCache;
import com.xtc.domain.config.http.HttpDomainConfigProvider;
import com.xtc.domain.config.provider.ProviderDomainConfigProvider;
import com.xtc.log.LogUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import rx.Completable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/** In-memory cache of the domain configuration. */
public class MemoryDomainProvider implements DomainConfigCache, DomainProvider {

    private static final String TAG = "MemoryDomainProvider";
    private static final long REFRESH_INTERVAL_MILLIS = TimeUnit.DAYS.toMillis(1);

    private final AtomicLong lastUpdateTime = new AtomicLong(0);
    private final AtomicReference<DomainConfig> config = new AtomicReference<>(null);
    private final DomainConfigProvider provider;

    public MemoryDomainProvider(Context context, String packageName) {
        this.provider = new ChainedDomainConfigProvider(
                new SdcardFileDomainConfigCache(context, packageName),
                new FileDomainConfigProvider(context, packageName),
                new ProviderDomainConfigProvider(context, packageName),
                new HttpDomainConfigProvider(context));
    }

    @Override
    public String getDataCenterCode() {
        ensureFresh();
        DomainConfig current = this.config.get();
        return current == null ? null : current.getDataCenterCode();
    }

    @Override
    public Integer getEnvId() {
        ensureFresh();
        DomainConfig current = this.config.get();
        return current == null ? null : current.getDomainId();
    }

    @Override
    public String getEnvName() {
        ensureFresh();
        DomainConfig current = this.config.get();
        return current == null ? null : current.getName();
    }

    @Override
    public String getDomain(String name) {
        if (!Domain.ROUTE.getName().equals(name)) {
            ensureFresh();
        } else {
            LogUtil.i(TAG, "fetch route domain, skip update");
        }
        DomainConfig current = this.config.get();
        Map<String, String> domains = current == null ? null : current.getAppDomain();
        if (domains == null) {
            return null;
        }
        return domains.get(name);
    }

    private void ensureFresh() {
        if (System.currentTimeMillis() - this.lastUpdateTime.get() < REFRESH_INTERVAL_MILLIS) {
            return;
        }
        if (this.config.get() == null) {
            try {
                fetch();
            } catch (Throwable t) {
                LogUtil.i(TAG, "fetch failure", t);
            }
            return;
        }
        Completable.fromAction(new Action0() {
            @Override
            public void call() {
                fetch();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action0() {
            @Override
            public void call() {
                LogUtil.i(TAG, "fetch success");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.i(TAG, "fetch failure", throwable);
            }
        });
    }

    private void fetch() {
        LogUtil.i(TAG, "try fetch config");
        if (this.provider.load(this)) {
            return;
        }
        LogUtil.i(TAG, "fetched config is expired");
        this.lastUpdateTime.set(0L);
    }

    @Override
    public void update(DomainConfig domainConfig) {
        LogUtil.i(TAG, "update config: " + domainConfig);
        this.lastUpdateTime.set(System.currentTimeMillis());
        this.config.set(domainConfig);
    }
}