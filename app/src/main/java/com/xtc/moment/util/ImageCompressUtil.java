package com.xtc.moment.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

import com.xtc.log.LogUtil;

/**
 * 图片压缩尺寸计算：按可用内存和屏幕尺寸决定采样后的宽高。
 */
public class ImageCompressUtil {

    private static final String TAG = "ImageCompressUtil";

    private static final int MEMORY_256 = 256;
    private static final int MEMORY_512 = 512;
    private static final float MAX_DISPLAY_256M = 2.5f;
    private static final float MAX_DISPLAY_512M = 5.0f;
    private static final float MAX_DISPLAY_1G = 10.0f;
    private static final int IMAGE_MAX_WIDTH = 4096;
    private static final int IMAGE_MAX_HEIGHT = 4096;

    private static Display display;
    private static Point point;

    private static Point getPoint(Context context) {
        if (display == null) {
            display = ((Activity) context).getWindowManager().getDefaultDisplay();
        }
        if (point == null) {
            point = new Point();
        }
        display.getSize(point);
        return point;
    }

    public static int getScreenWidth(Context context) {
        return getPoint(context).x;
    }

    public static int getScreenHeight(Context context) {
        return getPoint(context).y;
    }

    public static ImageSize calculateFitDisPlaySize(Context context, BitmapFactory.Options options) {
        ActivityManager.MemoryInfo memoryInfo = getMemoryInfo(context);
        float maxDisplayMemory = MAX_DISPLAY_512M;
        if (memoryInfo != null) {
            float totalMemoryMb = ((memoryInfo.totalMem * 1.0f) / 1024.0f) / 1024.0f;
            float lowMemoryFactor = memoryInfo.lowMemory ? 0.5f : 1.0f;
            if (totalMemoryMb > MEMORY_512) {
                maxDisplayMemory = MAX_DISPLAY_1G;
            } else if (totalMemoryMb <= MEMORY_256) {
                maxDisplayMemory = MAX_DISPLAY_256M;
            }
            maxDisplayMemory *= lowMemoryFactor;
        }
        float imageMemoryMb = ((((options.outWidth * options.outHeight) * 2) * 1.0f) / 1024.0f) / 1024.0f;
        LogUtil.i(TAG, " 图片内存占用大小:" + imageMemoryMb + " MB maxSize:" + maxDisplayMemory);
        if (imageMemoryMb <= maxDisplayMemory) {
            return generateCalculateSize(options.outWidth, options.outHeight);
        }
        float scaleRatio = (float) (Math.floor(imageMemoryMb / maxDisplayMemory) + 1.0d);
        LogUtil.i(TAG, " scaleRatio" + scaleRatio);
        int width = options.outWidth;
        int height = options.outHeight;
        int targetWidth;
        int targetHeight;
        if (height > width) {
            targetHeight = (int) (height / scaleRatio);
            targetWidth = width > getScreenWidth(context) * 2
                    ? (int) (width / scaleRatio)
                    : Math.min(width, getScreenWidth(context));
        } else if (width > height) {
            targetWidth = (int) (width / scaleRatio);
            targetHeight = height > getScreenHeight(context) * 2
                    ? (int) (height / scaleRatio)
                    : Math.min(height, getScreenHeight(context));
        } else {
            targetWidth = (int) (width / scaleRatio);
            targetHeight = (int) (height / scaleRatio);
        }
        LogUtil.i(TAG, " calculateFitDisPlaySize width:" + targetWidth + " height:" + targetHeight);
        return generateCalculateSize(targetWidth, targetHeight);
    }

    private static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return null;
        }
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    private static ImageSize generateCalculateSize(int width, int height) {
        return new ImageSize(Math.min(width, IMAGE_MAX_WIDTH), Math.min(height, IMAGE_MAX_HEIGHT));
    }

    /** 计算后的图片宽高。 */
    public static class ImageSize {
        public int width;
        public int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}