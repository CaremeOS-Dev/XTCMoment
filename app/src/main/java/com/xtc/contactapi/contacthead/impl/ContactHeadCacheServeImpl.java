package com.xtc.contactapi.contacthead.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.utils.BitmapUtils;
import com.xtc.contactapi.contacthead.config.ContactHeadManagerConfig;
import com.xtc.contactapi.contacthead.interfaces.IContactHeadCache;
import com.xtc.contactapi.contacthead.util.LoadBitmapUtil;
import com.xtc.contactapi.contacthead.util.LruCacheUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 联系人头像缓存服务实现，按 contactServerId + 尺寸缓存头像位图。
 */
public class ContactHeadCacheServeImpl implements IContactHeadCache, LruCacheUtil.CacheChangeListener<String, BitmapDrawable> {

    private static final String TAG = ContactHeadCacheServeImpl.class.getSimpleName();

    private static volatile ContactHeadCacheServeImpl instance;

    private final String cacheKeyFormat = "%s_%s_%s";

    private final Context context;
    private volatile LruCacheUtil<String, BitmapDrawable> headCache;

    private ContactHeadCacheServeImpl(Context context) {
        if (headCache == null) {
            headCache = new LruCacheUtil<>(context);
            headCache.setCacheChangeListener(this);
            this.context = context.getApplicationContext();
        } else {
            this.context = context.getApplicationContext();
        }
    }

    public static ContactHeadCacheServeImpl getInstance(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        if (instance == null) {
            synchronized (ContactHeadCacheServeImpl.class) {
                if (instance == null) {
                    instance = new ContactHeadCacheServeImpl(context);
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized Observable<BitmapDrawable> loadHeadBitmap(final ContactBean contactBean, final int width, final int height,
                                                                 final ContactHeadManagerConfig config) {
        if (!isValidContact(contactBean)) {
            return null;
        }
        String contactServerId = contactBean.getContactServerId();
        if (contactServerId == null || contactServerId.equals("")) {
            return null;
        }
        final String cacheKey = String.format(cacheKeyFormat, contactBean.getContactServerId(), width, height);
        final BitmapDrawable cachedDrawable = headCache.get(cacheKey);
        boolean recycled = cachedDrawable != null
                && (cachedDrawable.getBitmap() == null || cachedDrawable.getBitmap().isRecycled());
        if (cachedDrawable != null && !recycled) {
            Log.i(TAG, " salutation:" + contactBean.getName() + "  contactServerId:" + cacheKey
                    + " 头像数据存在于缓存容器中直接返回");
            return Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
                @Override
                public void call(Subscriber<? super BitmapDrawable> subscriber) {
                    subscriber.onNext(cachedDrawable);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.immediate());
        }
        if (contactBean.getPhotoPath() == null || contactBean.getPhotoPath().equals("")) {
            Log.e(TAG, " salutation:" + contactBean.getName() + "  contactServerId:" + cacheKey + " PhotoPath is null");
            return null;
        }
        if (recycled) {
            Log.w(TAG, " salutation:" + contactBean.getName() + "  contactServerId:" + cacheKey
                    + " 的bitmap已经在被回收，重新加载bitmap数据");
        }
        return Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                Bitmap bitmap = LoadBitmapUtil.loadLocalImage(contactBean.getPhotoPath(), width, height,
                        config.loadHead, config.bitmapConfig);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                if (bitmap != null) {
                    Log.i(TAG, "当前缓存未存在该头像,从本地加载数据 头像key:" + cacheKey + "  value:" + contactBean.getPhotoPath());
                    headCache.put(cacheKey, drawable);
                } else {
                    Log.e(TAG, "localImage is null");
                }
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public synchronized boolean hasHeadCache(ContactBean contactBean, int width, int height) {
        if (!isValidContact(contactBean)) {
            return false;
        }
        return headCache.containsKey(String.format(cacheKeyFormat, contactBean.getContactServerId(), width, height));
    }

    @Override
    public synchronized Observable<BitmapDrawable> loadHeadBitmap(final ContactBean contactBean) {
        if (!isValidContact(contactBean)) {
            return null;
        }
        final String biggestCacheKey = findBiggestCacheKey(contactBean);
        return Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                Bitmap bitmap = LoadBitmapUtil.loadLocalImage(context, contactBean.getPhotoPath());
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                if (!TextUtils.isEmpty(biggestCacheKey)) {
                    String newHash = BitmapUtils.toBase64Jpeg(bitmap);
                    BitmapDrawable oldDrawable = headCache.get(biggestCacheKey);
                    if (oldDrawable != null) {
                        String oldHash = BitmapUtils.toBase64Jpeg(oldDrawable.getBitmap());
                        if (newHash != null && !newHash.equals(oldHash)
                                && headCache.containsEqualValue(biggestCacheKey, drawable)) {
                            subscriber.onNext(drawable);
                            subscriber.onCompleted();
                            return;
                        }
                    }
                }
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    /** 找出该联系人所有缓存 key 中尺寸最大的一个。 */
    private String findBiggestCacheKey(ContactBean contactBean) {
        if (contactBean == null || TextUtils.isEmpty(contactBean.getContactServerId())) {
            return null;
        }
        List<String> keyList = headCache.findKeysContaining(contactBean.getContactServerId());
        if (keyList == null || keyList.size() == 0) {
            return null;
        }
        List<int[]> sizeList = new ArrayList<>();
        for (String key : keyList) {
            if (!TextUtils.isEmpty(key)) {
                String[] parts = key.split("_");
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
        return String.format(cacheKeyFormat, contactBean.getContactServerId(), biggest[0], biggest[1]);
    }

    @Override
    public synchronized Observable<Boolean> removeHeadCacheOnChange(final ContactBean oldContactBean, ContactBean newContactBean) {
        if (!isValidContact(oldContactBean) || !isValidContact(newContactBean)) {
            return null;
        }
        return Observable.just(newContactBean).map(new Func1<ContactBean, Boolean>() {
            @Override
            public Boolean call(ContactBean latestContactBean) {
                if (!isHeadChanged(oldContactBean, latestContactBean)) {
                    Log.i(TAG, "do not need to removeCacheHead ");
                    return true;
                }
                List<String> keyList = getCacheKeys(latestContactBean);
                Log.i(TAG, "removeCacheHead removeKeyList: " + keyList);
                if (keyList != null && !keyList.isEmpty()) {
                    for (String key : keyList) {
                        if (!TextUtils.isEmpty(key)) {
                            boolean removed = headCache.remove(key);
                            Log.i(TAG, "removeCacheHead deleteCache: " + key + ", result: " + removed);
                        }
                    }
                }
                return true;
            }
        }).subscribeOn(Schedulers.io());
    }

    private boolean isHeadChanged(ContactBean oldContactBean, ContactBean newContactBean) {
        if (newContactBean == null) {
            return false;
        }
        if (oldContactBean.getLastUpdatedTimestamp() == null || newContactBean.getLastUpdatedTimestamp() == null) {
            return true;
        }
        return !oldContactBean.getLastUpdatedTimestamp().equals(newContactBean.getLastUpdatedTimestamp());
    }

    private List<String> getCacheKeys(ContactBean contactBean) {
        if (contactBean == null || TextUtils.isEmpty(contactBean.getContactServerId())) {
            return null;
        }
        return headCache.findKeysContaining(contactBean.getContactServerId());
    }

    @Override
    public void onEntryEvicted(String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
        Log.i(TAG, "缓存被LRU策略移除，contactServerId = " + key);
    }

    @Override
    public void onEntryRemoved(String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
        Log.i(TAG, "缓存被更新，contactServerId = " + key);
    }

    @Override
    public boolean isHeadCacheInit() {
        return headCache.evictAll();
    }

    private boolean isValidContact(ContactBean contactBean) {
        return contactBean != null && contactBean.getContactServerId() != null
                && !contactBean.getContactServerId().equals("")
                && contactBean.getPhotoPath() != null
                && !contactBean.getPhotoPath().equals("");
    }
}