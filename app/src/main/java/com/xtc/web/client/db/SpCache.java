package com.xtc.web.client.db;

import com.xtc.utils.storage.SharedManager;

/** web.client 使用的本地缓存键读写。 */
public class SpCache {

    private static final String KEY_URL_CACHE_LAST_TIME = "common_web_last_time";
    private static final String KEY_WATCH_ID = "common_web_watch_id";

    /** 保存 H5 url 缓存的上次刷新时间。 */
    public static void saveUrlCacheLastTime(SharedManager sharedManager, long lastTime) {
        sharedManager.putLong(KEY_URL_CACHE_LAST_TIME, lastTime);
    }

    public static long getUrlCacheLastTime(SharedManager sharedManager) {
        return sharedManager.getLong(KEY_URL_CACHE_LAST_TIME, 0L);
    }

    /** 保存当前手表 id。 */
    public static void saveWatchId(SharedManager sharedManager, String watchId) {
        sharedManager.putString(KEY_WATCH_ID, watchId);
    }

    public static String getWatchId(SharedManager sharedManager) {
        return sharedManager.getString(KEY_WATCH_ID, "");
    }
}