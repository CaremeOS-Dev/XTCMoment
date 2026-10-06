package com.xtc.web.client.manager;

import android.content.Context;
import android.util.SparseArray;

import com.xtc.database.ormlite.RxDao;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.db.DbWebUrl;
import com.xtc.web.client.db.SpCache;
import com.xtc.web.client.net.WebUrlProxy;
import com.xtc.web.core.callback.CompletionHandler;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/** H5 入口 url 缓存管理：内存 -> 数据库 -> 网络三级回退。 */
public class UrlCacheManager {

    private static final String TAG = Constants.TAG + UrlCacheManager.class.getSimpleName();
    private static final long URL_CACHE_TIME = 60000L;
    public static String dbName;
    private static UrlCacheManager instance;

    private RxDao<DbWebUrl> dao;
    private SharedManager shareManager;
    private SparseArray<DbWebUrl> urlCache = new SparseArray<>();
    private WebUrlProxy webUrlProxy;

    public static UrlCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new UrlCacheManager(context);
        }
        return instance;
    }

    private UrlCacheManager(Context context) {
        this.webUrlProxy = new WebUrlProxy(context);
        this.dao = new RxDao<>(context, DbWebUrl.class, dbName);
        this.shareManager = SharedManager.getInstance(context);
    }

    /** 读取数据库中缓存的 url。 */
    private Observable<DbWebUrl> getDbUrl(final int type) {
        return Observable.create(new Observable.OnSubscribe<DbWebUrl>() {
            @Override
            public void call(Subscriber<? super DbWebUrl> subscriber) {
                DbWebUrl dbWebUrl = dao.queryForFirst(DbWebUrl.Key.TYPE, Integer.valueOf(type));
                LogUtil.d(TAG, "load local url = " + dbWebUrl);
                subscriber.onNext(dbWebUrl);
                subscriber.onCompleted();
            }
        });
    }

    /** 从服务端拉取 url 列表并同步数据库。 */
    private Observable<DbWebUrl> getNetUrl(final int type) {
        return this.webUrlProxy.getWebUrlList().map(new Func1<List<DbWebUrl>, DbWebUrl>() {
            @Override
            public DbWebUrl call(List<DbWebUrl> remoteList) {
                LogUtil.d(TAG, "load remote url!");
                List<DbWebUrl> localList = dao.queryForAll();
                SparseArray<DbWebUrl> localCache = new SparseArray<>();
                for (int index = 0; index < localList.size(); index++) {
                    DbWebUrl local = localList.get(index);
                    localCache.put(local.getType(), local);
                }
                DbWebUrl target = null;
                ArrayList<DbWebUrl> insertList = new ArrayList<>();
                ArrayList<DbWebUrl> updateList = new ArrayList<>();
                for (int index = 0; index < remoteList.size(); index++) {
                    DbWebUrl remote = remoteList.get(index);
                    if (localCache.get(remote.getType()) == null) {
                        insertList.add(remote);
                    } else {
                        updateList.add(remote);
                    }
                    urlCache.put(remote.getType(), remote);
                    if (remote.getType() == type) {
                        target = remote;
                    }
                }
                dao.insertForBatch(insertList);
                dao.updateForBatch(updateList);
                SpCache.saveUrlCacheLastTime(shareManager, System.currentTimeMillis());
                return target;
            }
        });
    }

    /** 依次尝试内存、数据库、网络，返回第一个非空 url。 */
    public Observable<String> getWebUrl(int type) {
        return Observable.concat(getMemoryUrl(type), getDbUrl(type), getNetUrl(type))
                .filter(new Func1<DbWebUrl, Boolean>() {
                    @Override
                    public Boolean call(DbWebUrl dbWebUrl) {
                        return Boolean.valueOf(dbWebUrl != null);
                    }
                })
                .map(new Func1<DbWebUrl, String>() {
                    @Override
                    public String call(DbWebUrl dbWebUrl) {
                        return dbWebUrl.getUrl();
                    }
                });
    }

    private Observable<DbWebUrl> getMemoryUrl(int type) {
        return Observable.just(this.urlCache.get(type));
    }

    /** 距上次刷新超过 1 分钟后异步刷新缓存。 */
    public void updateUrlCache() {
        Observable.just(Boolean.valueOf(true))
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean value) {
                        return Boolean.valueOf(System.currentTimeMillis()
                                - SpCache.getUrlCacheLastTime(shareManager) > URL_CACHE_TIME);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<DbWebUrl>>() {
                    @Override
                    public Observable<DbWebUrl> call(Boolean value) {
                        return getNetUrl(0);
                    }
                })
                .subscribe(new Action1<DbWebUrl>() {
                    @Override
                    public void call(DbWebUrl dbWebUrl) {
                        LogUtil.d(TAG, "update cache success!");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.d(TAG, "update cache error = " + throwable);
                    }
                });
    }

    public void cleanCache(CompletionHandler<Boolean> completionHandler) {
        completionHandler.complete(Boolean.valueOf(cleanCache()));
    }

    public boolean cleanCache() {
        boolean result = this.dao.clearTableData();
        LogUtil.d(TAG, "clean table cache result = " + result);
        return result;
    }
}