package com.xtc.contactapi.contacthead.interfaces;

import android.graphics.drawable.BitmapDrawable;

import com.xtc.contactapi.contacthead.config.ContactDressConfig;

import rx.Observable;

/**
 * 联系人装扮缓存接口。
 */
public interface IContactDressCache {

    /** 获取装扮缓存路径。 */
    String getDressPath(String watchId);

    /** 异步加载装扮位图。 */
    Observable<BitmapDrawable> loadDressBitmap(String watchId, int width, int height, ContactDressConfig config);

    /** 判断是否存在指定尺寸的装扮缓存。 */
    boolean hasDressCache(String watchId, int width, int height);

    /** 观察装扮位图。 */
    Observable<BitmapDrawable> observeDressBitmap(String watchId);

    /** 装扮缓存是否已初始化。 */
    boolean isDressCacheInit();

    /** 移除指定装扮缓存。 */
    boolean removeDressCache(String watchId);
}