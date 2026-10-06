package com.xtc.shareapi.share.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.google.webp.libwebp;
import com.xtc.shareapi.share.constant.OpenApiConstant;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * 分享图标/缩略图处理的位图工具，包含 WebP 压缩与屏幕尺寸获取。
 */
public class BitmapUtil {

    private static final String TAG;
    /** WebP 压缩结果的目标字节长度上限。 */
    public static final int THUMB_LENGTH = 300;
    /** 图标缩放后的尺寸（dp）。 */
    private static final int THUMB_SIZE = 14;

    static {
        System.loadLibrary("webp");
        TAG = OpenApiConstant.TAG + BitmapUtil.class.getSimpleName();
    }

    /** 直接使用系统 WebP 编码器压缩位图。 */
    public static byte[] bitmapToByteArray1(Bitmap bitmap) {
        byte[] empty = new byte[0];
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 10, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, "bitmapToByteArray exception:" + e.getMessage());
            return empty;
        }
    }

    /** 使用 native libwebp 压缩位图，循环调整直到满足长度限制。 */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        byte[] result = null;
        try {
            if (!Objects.equals(bitmap.getConfig(), Bitmap.Config.ARGB_8888)) {
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            }
            byte[] rgbData = convertARGB8888toRGB888(bitmap, bitmap.getWidth(), bitmap.getHeight());
            int attempt = 0;
            while (true) {
                int nextAttempt = attempt + 1;
                if (attempt >= 5) {
                    break;
                }
                result = libwebp.encodeRGB(rgbData, bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3, 10.0f);
                Log.i(TAG, "bitmapToByteArray webp length:" + result.length);
                if (result.length <= THUMB_LENGTH) {
                    break;
                }
                attempt = nextAttempt;
            }
        } catch (Exception e) {
            Log.e(TAG, "bitmapToByteArray exception:" + e.getMessage());
        }
        return result;
    }

    /** 将应用图标缩放并绘制到黑色底图上。 */
    public static Bitmap scaleIcon(Context context, Bitmap bitmap) {
        int size = dpToPx(context, THUMB_SIZE);
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, size, size, true);
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.parseColor("#000000"));
        canvas.drawBitmap(scaled, 0.0f, 0.0f, null);
        return result;
    }

    private static int dpToPx(Context context, float dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    /** 屏幕宽度（像素）。 */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    /** 屏幕高度（像素）。 */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }

    /** 将 ARGB_8888 位图转换为 RGB888 字节数组。 */
    public static byte[] convertARGB8888toRGB888(Bitmap bitmap, int width, int height) {
        byte[] rgbData = new byte[width * height * 3];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int pixel = bitmap.getPixel(column, row);
                int offset = ((row * width) + column) * 3;
                rgbData[offset] = (byte) ((pixel >> 16) & 255);
                rgbData[offset + 1] = (byte) ((pixel >> 8) & 255);
                rgbData[offset + 2] = (byte) (pixel & 255);
            }
        }
        return rgbData;
    }
}