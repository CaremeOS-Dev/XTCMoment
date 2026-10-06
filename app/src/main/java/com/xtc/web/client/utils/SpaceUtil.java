package com.xtc.web.client.utils;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.WatchModelUtil;

import java.io.File;

/** WebView 缓存目录大小统计与清理。 */
public class SpaceUtil {

    private static final long GAOTONG_CACHE_SIZE = 10485760L;
    private static final String TAG = "SpaceUtil";
    private static final long ZHANXUN_CACHE_SIZE = 5242880L;
    public static String cachePath = "/data/data/com.xtc.i3launcher/cache/org.chromium.android_webview";

    /** 缓存是否已超出平台上限。 */
    public static boolean isSpaceEnough() {
        long folderSize = getFolderSize(cachePath);
        LogUtil.d(TAG, "cache size:" + folderSize);
        if (WatchModelUtil.isGaotong()) {
            return folderSize > GAOTONG_CACHE_SIZE;
        }
        return folderSize > ZHANXUN_CACHE_SIZE;
    }

    /** 递归统计目录大小。 */
    public static long getFolderSize(String path) {
        long totalSize = 0L;
        File directory = new File(path);
        if (!directory.exists()) {
            return 0L;
        }
        try {
            File[] files = directory.listFiles();
            for (File file : files) {
                totalSize += file.isDirectory() ? getFolderSize(file.getAbsolutePath()) : file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    /** 删除目录及其内容。 */
    public static void delFolder(String path) {
        try {
            delAllFile(path);
            new File(path).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean delAllFile(String path) {
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }
        String[] fileNames = directory.list();
        boolean deleted = false;
        for (String fileName : fileNames) {
            File file = path.endsWith(File.separator) ? new File(path + fileName)
                    : new File(path + File.separator + fileName);
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                String childPath = path + File.separator + fileName;
                delAllFile(childPath);
                delFolder(childPath);
                deleted = true;
            }
        }
        LogUtil.d(TAG, "delAllFile：" + deleted);
        return deleted;
    }
}