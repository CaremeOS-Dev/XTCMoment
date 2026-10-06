package com.xtc.moment.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.ui.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    private static final String TAG = "ImageUtil";
    private static final String TMP = "_tmp";
    private static final int MAX_RATIO = 50;

    private static long startTime;

    public static String compressByScale(String filePath, String suffix, int maxWidth, int maxHeight) throws Throwable {
        resetStartTime();
        String tmpPath = getTmpPath(filePath, suffix);
        if (TextUtils.isEmpty(tmpPath)) {
            LogUtil.i(TAG, "compressByScale: resultPath is empty");
            return null;
        }
        File tmpFile = new File(tmpPath);
        if (FileUtils.exists(tmpFile)) {
            LogUtil.i(TAG, "compressByScale: compressImageFile is already exist");
            return tmpPath;
        }
        if (!FileUtils.exists(FileUtils.toFile(filePath))) {
            LogUtil.i(TAG, "compressByScale: filePath is not exist. filePath: " + filePath);
            return null;
        }
        new BitmapFactory.Options().inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = compressBitmap(filePath, maxWidth, maxHeight);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, outputStream);
        recycledBitmap(bitmap);
        try {
            FileUtils.writeBytes(tmpFile, outputStream.toByteArray(), false);
            return tmpPath;
        } catch (IOException e) {
            LogUtil.e(TAG, "compressByScale: ", e);
            return null;
        }
    }

    private static Bitmap compressBitmap(String filePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = getCommonBitmapOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = caculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static BitmapFactory.Options getCommonBitmapOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return options;
    }

    public static final int caculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        if (maxWidth == 0 || maxHeight == 0) {
            return 1;
        }
        int sampleSize;
        if (outHeight > maxHeight || outWidth > maxWidth) {
            int heightRatio = Math.round(outHeight / maxHeight);
            int widthRatio = Math.round(outWidth / maxWidth);
            sampleSize = heightRatio >= widthRatio ? heightRatio : widthRatio;
        } else {
            sampleSize = 1;
        }
        if (sampleSize <= 1) {
            return 0;
        }
        return sampleSize;
    }

    private static void compress(int width, int height, ByteArrayOutputStream outputStream, Bitmap bitmap) {
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        new Canvas(target).drawBitmap(bitmap, (Rect) null, new RectF(0.0f, 0.0f, width, height), (Paint) null);
        printTime("drawBitmap");
        target.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        printTime("compressTime");
        recycledBitmap(target);
    }

    private static void compressByScale(int width, int height, ByteArrayOutputStream outputStream, Bitmap bitmap) {
        Bitmap scaled = ImageUtils.compressByScale(bitmap, width, height, true);
        printTime("compressByScale");
        scaled.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        printTime("compressTime");
        recycledBitmap(scaled);
    }

    private static String getTmpPath(String filePath, String suffix) {
        return FileManager.getImageThumbnailPath() + FileUtils.getBaseName(filePath) + TMP + suffix;
    }

    private static void recycledBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }

    private static void compressBySampleSize(int width, int height, ByteArrayOutputStream outputStream, Bitmap bitmap) {
        int sampleSize = (int) calculateInSampleSize(bitmap.getWidth(), bitmap.getHeight(), width, height);
        LogUtil.i(TAG, "compressByScale: sampleSize: " + sampleSize);
        Bitmap scaled = ImageUtils.compressBySampleSize(bitmap, sampleSize, true);
        scaled.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        printTime("compressTime");
        recycledBitmap(scaled);
    }

    public static float calculateInSampleSize(int width, int height, int maxWidth, int maxHeight) {
        if (height > maxHeight || width > maxWidth) {
            return Math.min(height / maxHeight, width / maxWidth);
        }
        return 1.0f;
    }

    private static void resetStartTime() {
        startTime = System.currentTimeMillis();
        LogUtil.i(TAG, "resetStartTime: startTime: " + startTime);
    }

    private static void printTime(String tag) {
        LogUtil.i(TAG, "printTime: " + tag + ": " + (System.currentTimeMillis() - startTime) + " ms");
        resetStartTime();
    }

    public static boolean checkDrawableSizeInvalid(Drawable drawable) {
        if (drawable == null) {
            return false;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return false;
        }
        return Math.max(intrinsicWidth / intrinsicHeight, intrinsicHeight / intrinsicWidth) > MAX_RATIO;
    }
}