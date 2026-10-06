package com.xtc.moment.util.glidecache;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.system.WatchModelUtil;

public class CustomAppGlideModule extends AppGlideModule {

    private static final int MEMORY_CACHE_SIZE_QCOM = 5242880;
    private static final int MEMORY_CACHE_SIZE_SPRD = 2097152;
    private static final int DISK_CACHE_SIZE_QCOM = 20971520;
    private static final int DISK_CACHE_SIZE_SPRD = 6291456;

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(final Context context, final GlideBuilder glideBuilder) {
        super.applyOptions(context, glideBuilder);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                initGlideCache(context, glideBuilder);
            }
        });
    }

    private void initGlideCache(Context context, GlideBuilder glideBuilder) {
        if (WatchModelUtil.isGaotong()) {
            glideBuilder.setMemoryCache(new LruResourceCache(MEMORY_CACHE_SIZE_QCOM));
            glideBuilder.setBitmapPool(new LruBitmapPool(MEMORY_CACHE_SIZE_QCOM));
            glideBuilder.setDiskCache(new DiskLruCacheFactory(context.getFilesDir().getPath(), "moment_glide_cache", DISK_CACHE_SIZE_QCOM));
        } else {
            glideBuilder.setMemoryCache(new LruResourceCache(MEMORY_CACHE_SIZE_SPRD));
            glideBuilder.setBitmapPool(new LruBitmapPool(MEMORY_CACHE_SIZE_SPRD));
            glideBuilder.setDiskCache(new DiskLruCacheFactory(context.getFilesDir().getPath(), "moment_glide_cache", DISK_CACHE_SIZE_SPRD));
        }
        glideBuilder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565).autoClone());
    }
}