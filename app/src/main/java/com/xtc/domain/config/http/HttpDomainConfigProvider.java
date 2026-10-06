package com.xtc.domain.config.http;

import android.content.Context;
import android.os.SystemClock;

import com.xtc.domain.DomainConfig;
import com.xtc.domain.DomainManager;
import com.xtc.domain.config.DomainConfigCache;
import com.xtc.domain.config.DomainConfigProvider;
import com.xtc.log.LogUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import rx.functions.Action1;
import rx.functions.Func1;

/** Loads the domain config from the route service. */
public class HttpDomainConfigProvider implements DomainConfigProvider {

    /** Forces the next request to skip the discrete-time delay. */
    public static boolean force = false;

    private static final String TAG = "HttpDomainConfigProvider";
    private static final long MIN_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(10);

    private final Context context;
    private final long discreteTime;
    private final AtomicLong lastRequestTime = new AtomicLong(0);

    public HttpDomainConfigProvider(Context context) {
        this.context = context;
        this.discreteTime = (long) (Math.random() * TimeUnit.HOURS.toMillis(1L));
    }

    private DomainConfig selectConfig(List<DomainConfig> configs) {
        Integer envId = DomainManager.getInstance(this.context).getEnvId();
        if (envId == null) {
            LogUtil.i(TAG, "current envId == null");
            return null;
        }
        for (DomainConfig config : configs) {
            if (Objects.equals(envId, config.getDomainId())) {
                return config;
            }
        }
        LogUtil.i(TAG, "cant not found target config in: " + configs);
        return null;
    }

    @Override
    public boolean load(final DomainConfigCache cache) {
        if (!force && SystemClock.elapsedRealtime() < this.discreteTime) {
            LogUtil.i(TAG, "request in discrete time, skip");
            return false;
        }
        if (System.currentTimeMillis() - this.lastRequestTime.get() < MIN_INTERVAL_MILLIS) {
            LogUtil.i(TAG, "request interval to short, skip");
            return false;
        }
        this.lastRequestTime.set(System.currentTimeMillis());
        DomainManager.getInstance(this.context).fetchRouteConfigs()
                .map(new Func1<List<DomainConfig>, DomainConfig>() {
                    @Override
                    public DomainConfig call(List<DomainConfig> configs) {
                        return selectConfig(configs);
                    }
                })
                .subscribe(new Action1<DomainConfig>() {
                    @Override
                    public void call(DomainConfig domainConfig) {
                        if (domainConfig == null) {
                            LogUtil.i(TAG, "target config == null");
                        } else {
                            LogUtil.i(TAG, "config update by http");
                            cache.update(domainConfig);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.i(TAG, "fetch config failure", throwable);
                    }
                });
        return true;
    }
}