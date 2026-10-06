package com.xtc.web.client.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.media.session.PlaybackStateCompat;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.WatchModelUtil;
import com.xtc.web.core.data.bean.CacheModuleSwitchExtra;
import com.xtc.web.core.utils.WebUtils;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/** 文件大小统计与 WebView 缓存清理。 */
public class FileUtil {

    public static final int SIZETYPE_B = 1;
    public static final int SIZETYPE_GB = 4;
    public static final int SIZETYPE_KB = 2;
    public static final int SIZETYPE_MB = 3;
    private static final String TAG = "FileUtil";
    private static final long GAOTONG_CACHE_SIZE = 10485760L;
    private static final long ZHANXUN_CACHE_SIZE = 5242880L;
    public static String cachePath = "/data/data/com.xtc.i3launcher/cache/org.chromium.android_webview";

    /** WebView 缓存是否超过平台限制，超限时上报埋点。 */
    public static boolean isOverLimit(Context context) {
        long gaotongCacheSize;
        long zhanxunCacheSize;
        CacheModuleSwitchExtra cacheConfig = WebUtils.getGaotongCacheSize(context);
        if (cacheConfig != null) {
            gaotongCacheSize = cacheConfig.getGtCacheSize() * 1024L * 1024L;
            zhanxunCacheSize = cacheConfig.getZxCacheSize() * 1024L * 1024L;
        } else {
            gaotongCacheSize = GAOTONG_CACHE_SIZE;
            zhanxunCacheSize = ZHANXUN_CACHE_SIZE;
        }
        long folderSize = SpaceUtil.getFolderSize(cachePath);
        LogUtil.d(TAG, "cache size:" + folderSize + "gaoTongCacheSize:" + gaotongCacheSize
                + ",zhanXunCacheSize:" + zhanxunCacheSize);
        if (WatchModelUtil.isGaotong()) {
            boolean overLimit = folderSize > gaotongCacheSize;
            if (overLimit) {
                BehaviorEvent.deleteWebViewCache(context, gaotongCacheSize, folderSize);
            }
            return overLimit;
        }
        boolean overLimit = folderSize > zhanxunCacheSize;
        if (overLimit) {
            BehaviorEvent.deleteWebViewCache(context, zhanxunCacheSize, folderSize);
        }
        return overLimit;
    }

    /** 外置存储根目录，未挂载时返回 null。 */
    public static String getSdcardPath() {
        File externalStorage = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStorageDirectory()
                : null;
        if (externalStorage != null) {
            return externalStorage.toString();
        }
        return null;
    }

    /** 目录不存在时创建。 */
    public static void judgeFileExist(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return;
        }
        file.mkdirs();
    }

    /** 文件或目录大小，按 sizeType 换算单位。 */
    public static double getFileOrFilesSize(String path, int sizeType) {
        long fileSize;
        File file = new File(path);
        try {
            fileSize = file.isDirectory() ? getFileSizes(file) : getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取文件大小", "获取失败!");
            fileSize = 0;
        }
        return formatFileSize(fileSize, sizeType);
    }

    /** 文件或目录大小，自动带单位。 */
    public static String getAutoFileOrFilesSize(String path) {
        long fileSize;
        File file = new File(path);
        try {
            fileSize = file.isDirectory() ? getFileSizes(file) : getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取文件大小", "获取失败!");
            fileSize = 0;
        }
        return formatFileSize(fileSize);
    }

    private static long getFileSize(File file) throws Exception {
        long size = 0;
        FileInputStream inputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                size = inputStream.available();
            } else {
                LogUtil.e("获取文件大小", "文件不存在!");
            }
        } catch (Exception e) {
            LogUtil.e("获取文件大小", "文件计算error = " + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return size;
    }

    /** 递归统计目录大小。 */
    public static long getFileSizes(File file) throws Exception {
        long totalSize = 0;
        File[] files = file.listFiles();
        for (File child : files) {
            totalSize += child.isDirectory() ? getFileSizes(child) : getFileSize(child);
        }
        return totalSize;
    }

    private static String formatFileSize(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        if (size == 0) {
            return "0B";
        }
        if (size < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            return decimalFormat.format(size) + "B";
        }
        if (size < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
            return decimalFormat.format(size / 1024.0d) + "KB";
        }
        if (size < 1073741824) {
            return decimalFormat.format(size / 1048576.0d) + "MB";
        }
        return decimalFormat.format(size / 1.073741824E9d) + "GB";
    }

    private static double formatFileSize(long size, int sizeType) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.CHINA));
        if (sizeType == SIZETYPE_B) {
            return Double.valueOf(decimalFormat.format(size)).doubleValue();
        }
        if (sizeType == SIZETYPE_KB) {
            return Double.valueOf(decimalFormat.format(size / 1024.0d)).doubleValue();
        }
        if (sizeType == SIZETYPE_MB) {
            return Double.valueOf(decimalFormat.format(size / 1048576.0d)).doubleValue();
        }
        if (sizeType != SIZETYPE_GB) {
            return 0.0d;
        }
        return Double.valueOf(decimalFormat.format(size / 1.073741824E9d)).doubleValue();
    }

    /** 异步清空 WebView 缓存目录。 */
    public static void deleteWebViewCache(Context context) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                File cacheDirectory = new File(cachePath);
                if (cacheDirectory.exists()) {
                    deleteFile(cacheDirectory);
                }
                subscriber.onNext(Boolean.valueOf(true));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        LogUtil.d(TAG, "deleteWebViewCache：" + success);
                    }
                });
    }

    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteFile(child);
                }
            }
            file.delete();
            return;
        }
        if (file.exists()) {
            file.delete();
        }
    }
}