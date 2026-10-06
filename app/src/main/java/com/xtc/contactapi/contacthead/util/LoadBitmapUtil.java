package com.xtc.contactapi.contacthead.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.xtc.utils.ui.ScreenUtils;

/**
 * 本地图片加载工具，按目标尺寸计算采样率以节省内存。
 */
public abstract class LoadBitmapUtil {

    private static final String TAG = "LoadBitmapUtil";

    private static final int DEFAULT_WIDTH = 320;
    private static final int DEFAULT_HEIGHT = 360;

    /** 按指定尺寸加载本地图片。 */
    public static Bitmap loadLocalImage(String path, int width, int height, @Deprecated boolean unused, Bitmap.Config config) {
        if (path == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        Log.d(TAG, "loadLocalImage:width=" + width + ",height=" + height + ",outWidth=" + outWidth + ",outHeight=" + outHeight);
        if (width < 0 || height < 0 || outWidth < 0 || outHeight < 0) {
            return null;
        }
        options.inSampleSize = calculateSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        return BitmapFactory.decodeFile(path, options);
    }

    /** 按屏幕尺寸加载本地图片。 */
    @Deprecated
    public static Bitmap loadLocalImage(String path) {
        return loadLocalImage(null, path);
    }

    public static Bitmap loadLocalImage(Context context, String path) {
        if (path == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        Log.d(TAG, "loadLocalImage:outWidth=" + outWidth + ",outHeight=" + outHeight);
        if (outWidth <= 0 || outHeight <= 0) {
            return null;
        }
        int screenWidth = DEFAULT_WIDTH;
        int screenHeight = DEFAULT_HEIGHT;
        if (context != null) {
            screenWidth = ScreenUtils.getScreenWidth(context);
            screenHeight = ScreenUtils.getScreenHeight(context);
        }
        if (outWidth > screenWidth && outHeight > screenHeight) {
            options.inSampleSize = calculateSampleSize(options, screenWidth, screenHeight);
            Log.d(TAG, "loadLocalImage:screenWidth=" + screenWidth + ",screenHeight=" + screenHeight);
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateSampleSize(BitmapFactory.Options options, int width, int height) {
        int sampleSize = 1;
        if (options != null && width > 0 && height > 0) {
            int outWidth = options.outWidth;
            int outHeight = options.outHeight;
            if (outWidth > height || outHeight > height) {
                int halfWidth = outWidth / 2;
                int halfHeight = outHeight / 2;
                while (halfWidth / sampleSize >= height && halfHeight / sampleSize >= width) {
                    sampleSize *= 2;
                }
            }
        }
        return sampleSize;
    }
}