package com.xtc.contactapi.contacthead.interfaces;

import android.graphics.drawable.BitmapDrawable;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contacthead.config.ContactHeadManagerConfig;

import rx.Observable;

/**
 * 联系人头像缓存接口。
 */
public interface IContactHeadCache {

    /** 加载联系人头像。 */
    Observable<BitmapDrawable> loadHeadBitmap(ContactBean contactBean);

    /** 加载指定尺寸的联系人头像。 */
    Observable<BitmapDrawable> loadHeadBitmap(ContactBean contactBean, int width, int height, ContactHeadManagerConfig config);

    /** 联系人头像发生变化时移除旧缓存。 */
    Observable<Boolean> removeHeadCacheOnChange(ContactBean oldContactBean, ContactBean newContactBean);

    /** 头像缓存是否已初始化。 */
    boolean isHeadCacheInit();

    /** 判断是否存在指定尺寸的头像缓存。 */
    boolean hasHeadCache(ContactBean contactBean, int width, int height);
}