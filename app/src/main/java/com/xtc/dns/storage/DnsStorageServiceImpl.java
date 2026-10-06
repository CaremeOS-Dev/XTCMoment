package com.xtc.dns.storage;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.dns.LogTag;
import com.xtc.dns.bean.HttpDnsPack;
import com.xtc.dns.storage.db.DomainModel;
import com.xtc.dns.storage.db.DomainModelDao;
import com.xtc.log.LogUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DNS 存储服务实现，结合内存缓存与数据库。
 */
public class DnsStorageServiceImpl implements IDnsStorageService {

    private static final String TAG = LogTag.tag("DnsStorageServiceImpl");

    private static final int INITIAL_CAPACITY = 8;
    private static final float LOAD_FACTOR = 32.0f;

    private final ConcurrentHashMap<String, List<DomainModel>> memoryCache =
            new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR);

    private final DomainModelDao domainModelDao;

    public DnsStorageServiceImpl(Context context) {
        this.domainModelDao = new DomainModelDao(context);
    }

    /** 按域名与 IP 查询单条记录。 */
    public DomainModel queryDomainModel(String domain, String ip) {
        return this.domainModelDao.queryByDomainAndIp(domain, ip);
    }

    @Override
    public List<DomainModel> queryDomain(String domain) {
        if (TextUtils.isEmpty(domain)) {
            LogUtil.w(TAG, "host is null");
            return null;
        }
        List<DomainModel> cached = this.memoryCache.get(domain);
        if (cached != null) {
            return cached;
        }
        List<DomainModel> domainList = this.domainModelDao.queryByDomain(domain);
        cacheDomainList(domainList);
        if (domainList == null || domainList.isEmpty()) {
            return null;
        }
        return domainList;
    }

    private void cacheDomainList(List<DomainModel> domainList) {
        if (domainList == null || domainList.isEmpty()) {
            return;
        }
        this.memoryCache.put(domainList.get(0).getDomain(), domainList);
    }

    private void removeCache(String domain) {
        if (TextUtils.isEmpty(domain)) {
            return;
        }
        this.memoryCache.remove(domain);
    }

    private void clearCache() {
        this.memoryCache.clear();
    }

    @Override
    public void save(HttpDnsPack httpDnsPack) {
        List<DomainModel> domainList = httpDnsPack.getDomainList();
        if (domainList == null || domainList.size() <= 0) {
            return;
        }
        this.domainModelDao.saveDomainList(httpDnsPack.getDomain(), domainList);
        cacheDomainList(domainList);
        String domain = domainList.get(0).getDomain();
        LogUtil.i(TAG, "设置ip缓存成功!，domain = " + domain);
    }

    @Override
    public void removeDomain(String ip) {
        if (TextUtils.isEmpty(ip)) {
            LogUtil.i(TAG, "decrease ip is invalid");
            return;
        }
        List<DomainModel> domainList = this.domainModelDao.queryByIp(ip);
        if (domainList == null) {
            return;
        }
        for (DomainModel domainModel : domainList) {
            domainModel.setPriority(domainModel.getPriority() - 1);
            this.domainModelDao.updateBatch(domainList);
            removeCache(domainModel.getDomain());
            LogUtil.i(TAG, "ip不可用，降低权值, ip = " + ip);
        }
    }

    @Override
    public void clearExpired(long expireTime) {
        this.domainModelDao.deleteBeforeTime(expireTime);
    }

    @Override
    public void init() {
        LogUtil.i(TAG, "清除所有dns缓存");
        clearCache();
        this.domainModelDao.clearTableData();
    }
}