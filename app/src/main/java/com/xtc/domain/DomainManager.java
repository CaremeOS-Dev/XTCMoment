package com.xtc.domain;

import android.content.Context;

import com.xtc.domain.config.http.DomainResp;
import com.xtc.domain.config.http.EnvResp;
import com.xtc.domain.config.http.HttpConfigApi;
import com.xtc.domain.config.http.RouteReq;
import com.xtc.domain.config.http.RouteResp;
import com.xtc.domain.provider.BuiltInDomainClient;
import com.xtc.domain.provider.ChainedDomainProvider;
import com.xtc.domain.provider.DomainProvider;
import com.xtc.domain.provider.MemoryDomainProvider;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.system.account.WatchDevice;
import com.xtc.utils.system.WatchModelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Single;
import rx.functions.Func1;

/** Resolves the service domains for the current environment. */
public class DomainManager {

    private static DomainManager instance;
    private final Context context;
    private final DomainProvider provider;

    public static synchronized DomainManager getInstance(Context context) {
        if (instance == null) {
            instance = new DomainManager(context, "com.xtc.i3launcher");
        }
        return instance;
    }

    private DomainManager(Context context, String packageName) {
        this.context = context.getApplicationContext();
        this.provider = new ChainedDomainProvider(new MemoryDomainProvider(context, packageName),
                new BuiltInDomainClient());
    }

    /** @return true when the built-in environment is active. */
    public boolean isBuiltInEnv() {
        Integer envId = this.provider.getEnvId();
        return envId != null && envId.intValue() == BuiltInDomainClient.ENV_ID;
    }

    public String getDataCenterCode() {
        return this.provider.getDataCenterCode();
    }

    public Integer getEnvId() {
        return this.provider.getEnvId();
    }

    public String getEnvName() {
        return this.provider.getEnvName();
    }

    /** First domain of the given service. */
    public String getDomain(Domain domain) {
        return getDomain(domain.getName());
    }

    /** First domain of the given service. */
    public String getDomain(String name) {
        List<String> domains = getDomainList(name);
        if (domains == null || domains.isEmpty()) {
            return null;
        }
        return domains.get(0);
    }

    public List<String> getDomainList(Domain domain) {
        return getDomainList(domain.getName());
    }

    /** All domains of the given service. */
    public List<String> getDomainList(String name) {
        String value = this.provider.getDomain(name);
        if (value == null) {
            return null;
        }
        return Arrays.asList(value.split(","));
    }

    /** Fetches the route configuration from the server. */
    public Single<List<DomainConfig>> fetchRouteConfigs() {
        return Single.fromCallable(new Callable<RouteReq>() {
            @Override
            public RouteReq call() {
                WatchDevice watchDevice = new WatchDevice(DomainManager.this.context);
                return new RouteReq(WatchModelUtil.getWatchInnerModel(), watchDevice.getBindNumber(),
                        watchDevice.getWatchVersion(), 2);
            }
        }).flatMap(new Func1<RouteReq, Single<? extends List<DomainConfig>>>() {
            @Override
            public Single<? extends List<DomainConfig>> call(RouteReq routeReq) {
                return ((HttpConfigApi) HttpManager.getInstance(DomainManager.this.context).getHttpClient()
                        .request(BaseUrlManager.getRouteUrl(DomainManager.this.context), HttpConfigApi.class))
                        .getRouteInfo(routeReq)
                        .map(new HttpRxJavaCallback())
                        .map(new Func1<RouteResp, List<DomainConfig>>() {
                            @Override
                            public List<DomainConfig> call(RouteResp routeResp) {
                                return toDomainConfigs(routeResp);
                            }
                        });
            }
        });
    }

    private List<DomainConfig> toDomainConfigs(RouteResp routeResp) {
        String dataCenterCode = routeResp.getDataCenterCode();
        if (dataCenterCode == null || routeResp.getEnvs() == null) {
            return Collections.emptyList();
        }
        ArrayList<DomainConfig> configs = new ArrayList<>();
        for (EnvResp envResp : routeResp.getEnvs()) {
            if (envResp.getDomains() != null) {
                HashMap<String, String> domains = new HashMap<>();
                for (DomainResp domainResp : envResp.getDomains()) {
                    domains.put(domainResp.getServiceName(), domainResp.getServiceDomain());
                }
                configs.add(new DomainConfig(dataCenterCode, envResp.getEnvId(), envResp.getEnvName(), domains));
            }
        }
        return configs;
    }
}