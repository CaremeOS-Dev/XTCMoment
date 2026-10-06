package com.xtc.dns.dnsp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.dns.LogTag;
import com.xtc.dns.beh.DnsBeh;
import com.xtc.dns.bean.HttpDnsPack;
import com.xtc.dns.dnsp.provider.IDnsProvider;
import com.xtc.dns.dnsp.provider.LocalDnsProvider;
import com.xtc.dns.dnsp.provider.TencentHttpDnsProvider;
import com.xtc.dns.storage.DnsStorageServiceImpl;
import com.xtc.dns.storage.db.DomainModel;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DNS 服务实现，按优先级尝试各提供者解析并维护存储缓存。
 */
public class DnsServiceImpl implements IDnsService {

    private static final String TAG = LogTag.tag("DnsServiceImpl");
    private static final String KEY_REQUEST_COUNT = "http_dns_request_count";
    private static final int MAX_UPDATE_COUNT = 10;

    private final Context context;
    private final DnsStorageServiceImpl storageService;
    private final ArrayList<IDnsProvider> providers = new ArrayList<>();
    private final ConcurrentHashMap<String, UpdateTask> updateTasks = new ConcurrentHashMap<>();

    private ConnectivityManager connectivityManager;
    private long lastRequestTime = 0;
    private int updateCount = 0;

    public DnsServiceImpl(Context context) {
        this.context = context.getApplicationContext();
        this.providers.add(new TencentHttpDnsProvider());
        this.providers.add(new LocalDnsProvider());
        this.storageService = new DnsStorageServiceImpl(context);
    }

    @Override
    public List<DomainModel> resolveDomain(String domain) {
        List<DomainModel> domainList = this.storageService.queryDomain(domain);
        if (domainList == null) {
            if (!canRequestHttpDns()) {
                return null;
            }
            requestHttpDns(domain);
            return null;
        }
        LogUtil.i(TAG, "getDomainModel, " + domainList);
        return domainList;
    }

    private void doRequestHttpDns(String domain) {
        this.lastRequestTime = System.currentTimeMillis();
        HttpDnsPack httpDnsPack = requestFromProviders(domain);
        if (httpDnsPack == null) {
            return;
        }
        this.updateCount = 0;
        this.storageService.save(httpDnsPack);
        int requestCount = SharedManager.getInstance(context).getInt(KEY_REQUEST_COUNT, 0) + 1;
        LogUtil.i(TAG, "访问HttpDns总次数：" + requestCount);
        SharedManager.getInstance(context).putInt(KEY_REQUEST_COUNT, requestCount);
    }

    private void requestHttpDns(final String domain) {
        UpdateTask updateTask = this.updateTasks.get(domain);
        if (updateTask == null) {
            UpdateTask newTask = new UpdateTask(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("Get Http Dns Data");
                    doRequestHttpDns(domain);
                    updateTasks.remove(domain);
                }
            });
            this.updateTasks.put(domain, newTask);
            newTask.start();
            return;
        }
        LogUtil.w(TAG, "同一个域名请求还未结束，不发起HttpDns请求，domain = " + domain);
        if (System.currentTimeMillis() - updateTask.getCreateTime() > Constants.NOTIFY_INTERVAL) {
            updateTask.start();
        }
    }

    private HttpDnsPack requestFromProviders(String domain) {
        synchronized (this.providers) {
            for (IDnsProvider provider : this.providers) {
                if (!provider.isAvailable()) {
                    continue;
                }
                long startTime = System.currentTimeMillis();
                HttpDnsPack httpDnsPack = provider.resolve(domain);
                long timeConsumed = System.currentTimeMillis() - startTime;
                if (httpDnsPack != null) {
                    DnsBeh.reportSuccess(context, domain, httpDnsPack.getErrorMsg(), timeConsumed, provider.getTag());
                    return httpDnsPack;
                }
                DnsBeh.reportFail(context, provider.getTag(), domain);
                if (provider instanceof LocalDnsProvider) {
                    this.updateCount++;
                    LogUtil.w(TAG, "LocalDns解析失败，增加指数，updateCount = " + this.updateCount);
                }
            }
            return null;
        }
    }

    @Override
    public void saveDomain(String domain, String ip) {
        if (!isNetworkAvailable()) {
            LogUtil.i(TAG, "checkIpExpire: notwork is unavailable");
            return;
        }
        DomainModel domainModel = this.storageService.queryDomainModel(domain, ip);
        if (domainModel == null) {
            LogUtil.i(TAG, "checkIpExpire: domainModel == null");
            return;
        }
        boolean expired = System.currentTimeMillis() - domainModel.getCreateTime() > domainModel.getDomainTtl() * 1000;
        LogUtil.i(TAG, String.format(Locale.ROOT, "checkIpExpire: %s - %s - %b", domain, ip, expired));
        if (expired && canRequestHttpDns()) {
            requestHttpDns(domain);
        }
    }

    @Override
    public void removeDomain(String ip) {
        if (isNetworkAvailable()) {
            this.storageService.removeDomain(ip);
            return;
        }
        LogUtil.i(TAG, "无网络，不降权ip, ip = " + ip);
    }

    @Override
    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearExpired();
            }
        }).start();
    }

    private void clearExpired() {
        this.storageService.clearExpired(System.currentTimeMillis() - (DnsConfig.maxCacheTime * 1000L));
    }

    @Override
    public void release() {
        this.storageService.init();
    }

    private boolean canRequestHttpDns() {
        long now = System.currentTimeMillis();
        long interval = now - this.lastRequestTime;
        if (this.lastRequestTime != 0) {
            LogUtil.w(TAG, "距上次请求TencentHttpDns->" + interval + "ms,请求指数 updateCount = " + this.updateCount);
        }
        if (interval < Math.pow(2.0d, this.updateCount)) {
            return false;
        }
        if (this.updateCount >= MAX_UPDATE_COUNT) {
            this.updateCount = MAX_UPDATE_COUNT;
        }
        if (isNetworkAvailable()) {
            return true;
        }
        LogUtil.w(TAG, "当前网络断开，不请求HttpDns");
        return false;
    }

    private boolean isNetworkAvailable() {
        if (this.connectivityManager == null) {
            this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        ConnectivityManager manager = this.connectivityManager;
        if (manager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return false;
        }
        return activeNetworkInfo.isAvailable();
    }

    /**
     * 域名解析更新任务。
     */
    private static class UpdateTask {
        private final Runnable runnable;
        private final long createTime = System.currentTimeMillis();

        UpdateTask(Runnable runnable) {
            this.runnable = runnable;
        }

        void start() {
            new Thread(this.runnable).start();
        }

        long getCreateTime() {
            return this.createTime;
        }
    }
}