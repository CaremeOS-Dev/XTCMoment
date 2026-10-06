package com.xtc.moment.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.xtc.log.LogUtil;

import java.net.URL;
import java.util.HashMap;

/**
 * 点赞动效图缓存：按 URL 或资源 id 缓存 Drawable。
 */
public class LikeDrawableCache {

    private static final String TAG = "LikeDrawableCache";

    private Context context;
    private HashMap<String, Drawable> mDrawableHashMap = new HashMap<>();

    public LikeDrawableCache(Context context) {
        this.context = context;
    }

    public void loadImageFromNetToCache(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        if (this.mDrawableHashMap.containsKey(imageUrl)) {
            LogUtil.d(TAG, "had drawable");
            return;
        }
        LogUtil.d(TAG, "loadImageFromNetToCache: imageUrl = [" + imageUrl + "]");
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
        } catch (Exception e) {
            LogUtil.e(TAG, "loadImageFromNetwork: ", e);
        }
        if (drawable == null) {
            LogUtil.d(TAG, "null drawable");
        } else {
            this.mDrawableHashMap.put(imageUrl, drawable);
            LogUtil.d(TAG, "not null drawable");
        }
    }

    public Drawable getCacheDrawable(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        return this.mDrawableHashMap.get(imageUrl);
    }

    public Drawable getCacheDrawableById(int resId) {
        if (resId <= 0) {
            return null;
        }
        String key = String.valueOf(resId);
        if (this.mDrawableHashMap.containsKey(key)) {
            return this.mDrawableHashMap.get(key);
        }
        LogUtil.d(TAG, "getCacheDrawableById: resId = [" + resId + "]");
        Drawable drawable = ContextCompat.getDrawable(this.context, resId);
        this.mDrawableHashMap.put(key, drawable);
        return drawable;
    }

    public void clear() {
        LogUtil.d(TAG, "clear");
        this.mDrawableHashMap.clear();
    }
}