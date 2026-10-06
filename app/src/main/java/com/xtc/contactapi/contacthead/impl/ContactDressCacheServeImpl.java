package com.xtc.contactapi.contacthead.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.contactapi.contact.manager.ContactApi;
import com.xtc.contactapi.contacthead.bean.DressBean;
import com.xtc.contactapi.contacthead.config.ContactDressConfig;
import com.xtc.contactapi.contacthead.interfaces.IContactDressCache;
import com.xtc.contactapi.contacthead.util.LoadBitmapUtil;
import com.xtc.contactapi.contacthead.util.LruCacheUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 联系人装扮缓存服务实现，按 watchId + 尺寸缓存装扮位图。
 */
public class ContactDressCacheServeImpl implements IContactDressCache, LruCacheUtil.CacheChangeListener<String, BitmapDrawable> {

    /** 缓存 key 分隔符。 */
    public static final String KEY_SEPARATOR = "\\*\\*";

    private static final String TAG = ContactDressCacheServeImpl.class.getSimpleName();

    private static volatile ContactDressCacheServeImpl instance;

    private final String cacheKeyFormat = "%s**%s**%s";

    private final Context context;
    private final LruCacheUtil<String, BitmapDrawable> dressCache;

    private ContactDressCacheServeImpl(Context context) {
        this.context = context.getApplicationContext();
        this.dressCache = new LruCacheUtil<>(this.context);
        this.dressCache.setCacheChangeListener(this);
    }

    /** 全部缓存 key。 */
    public Set<String> getAllKeys() {
        return dressCache.keySet();
    }

    public static ContactDressCacheServeImpl getInstance(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        if (instance == null) {
            synchronized (ContactHeadCacheServeImpl.class) {
                if (instance == null) {
                    instance = new ContactDressCacheServeImpl(context);
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized Observable<BitmapDrawable> loadDressBitmap(String watchId, int width, int height, ContactDressConfig config) {
        if (!TextUtils.isEmpty(watchId) && ContactApi.isDefaultHeadLoaded()) {
            String dressPath = getDressPath(watchId);
            if (TextUtils.isEmpty(dressPath)) {
                return null;
            }
            String cacheKey = String.format(cacheKeyFormat, dressPath, width, height);
            BitmapDrawable cachedDrawable = dressCache.get(cacheKey);
            boolean recycled = cachedDrawable != null && cachedDrawable.getBitmap().isRecycled();
            if (cachedDrawable != null && !recycled) {
                Log.i(TAG, "装扮直接命中缓存 queryKey:" + cacheKey);
                return justBitmap(cachedDrawable);
            }
            return loadDressBitmapFromFile(watchId, width, height, config);
        }
        return null;
    }

    private Observable<BitmapDrawable> justBitmap(final BitmapDrawable drawable) {
        return Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.immediate());
    }

    private Observable<BitmapDrawable> loadDressBitmapFromFile(final String watchId, final int width, final int height,
                                                              final ContactDressConfig config) {
        return Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                String dressPath = getDressPath(watchId);
                Bitmap bitmap = LoadBitmapUtil.loadLocalImage(dressPath, width, height, config.cacheDress, config.bitmapConfig);
                BitmapDrawable drawable = bitmap != null ? new BitmapDrawable(context.getResources(), bitmap) : null;
                if (drawable != null) {
                    String cacheKey = String.format(cacheKeyFormat, dressPath, width, height);
                    Log.i(TAG, "当前缓存未存在该头像,从本地加载数据 装扮key:" + cacheKey + "  value:" + dressPath);
                    dressCache.put(cacheKey, drawable);
                } else {
                    Log.e(TAG, "localImage is null");
                }
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public String getDressPath(String watchId) {
        DressBean dressBean = ContactApi.getDressCache().get(watchId);
        if (dressBean == null || TextUtils.isEmpty(dressBean.getDressPath())) {
            Log.w(TAG, "dress Path is null");
            return null;
        }
        String dressPath = dressBean.getDressPath();
        if (new File(dressPath).exists()) {
            return dressPath;
        }
        Log.w(TAG, "dress Path's file don't find");
        return null;
    }

    @Override
    public Observable<BitmapDrawable> observeDressBitmap(final String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return null;
        }
        final String biggestCacheKey = findBiggestCacheKey(watchId);
        return Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(),
                        LoadBitmapUtil.loadLocalImage(context, watchId));
                if (!TextUtils.isEmpty(biggestCacheKey)) {
                    dressCache.containsEqualValue(biggestCacheKey, drawable);
                }
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    private String findBiggestCacheKey(String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return null;
        }
        List<String> keyList = dressCache.findKeysContaining(watchId);
        if (keyList == null || keyList.size() == 0) {
            return null;
        }
        List<int[]> sizeList = new ArrayList<>();
        for (String key : keyList) {
            if (!TextUtils.isEmpty(key)) {
                String[] parts = key.split(KEY_SEPARATOR);
                if (parts.length >= 3) {
                    sizeList.add(new int[]{Integer.parseInt(parts[1]), Integer.parseInt(parts[2])});
                }
            }
        }
        if (sizeList.isEmpty()) {
            return null;
        }
        Collections.sort(sizeList, new Comparator<int[]>() {
            @Override
            public int compare(int[] first, int[] second) {
                int firstSum = 0;
                for (int value : first) {
                    firstSum += value;
                }
                int secondSum = 0;
                for (int value : second) {
                    secondSum += value;
                }
                return secondSum - firstSum;
            }
        });
        int[] biggest = sizeList.get(0);
        return String.format(cacheKeyFormat, watchId, biggest[0], biggest[1]);
    }

    @Override
    public boolean hasDressCache(String watchId, int width, int height) {
        return dressCache.containsKey(String.format(watchId, width, height));
    }

    @Override
    public boolean isDressCacheInit() {
        return dressCache.evictAll();
    }

    /** 移除指定 watchId 的全部缓存。 */
    public void removeCache(String watchId) {
        Iterator<String> iterator = dressCache.findKeysContaining(watchId).iterator();
        while (iterator.hasNext()) {
            dressCache.remove(iterator.next());
        }
    }

    @Override
    public boolean removeDressCache(String watchId) {
        if (watchId == null) {
            return false;
        }
        if (watchId.equals("") || watchId.length() < 3) {
            return false;
        }
        String suffix = "";
        char[] chars = watchId.trim().toCharArray();
        int index = chars.length - 1;
        for (int count = 3; count > 0; count--) {
            if (index < 0 || index >= chars.length) {
                break;
            }
            suffix = chars[index] + suffix;
            index--;
        }
        return suffix.equalsIgnoreCase("gif");
    }

    @Override
    public void onEntryEvicted(String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
        Log.i(TAG, "缓存被LRU策略移除，contactServerId = " + key);
    }

    @Override
    public void onEntryRemoved(String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
        Log.i(TAG, "缓存被更新，key = " + key);
    }
}