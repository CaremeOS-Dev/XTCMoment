package com.xtc.contactapi.contact.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 位图转换工具，提供 JPEG Base64、YUV 转位图与旋转能力。
 */
public class BitmapUtils {

    /** 将位图压缩为 JPEG 并编码为 Base64。 */
    public static String toBase64Jpeg(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 65, outputStream);
            outputStream.flush();
            outputStream.close();
            return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException inner) {
                inner.printStackTrace();
            }
            return null;
        }
    }

    /** 将 NV21 数据转换为位图。 */
    public static Bitmap fromYuv(byte[] yuvData, int width, int height) {
        YuvImage yuvImage = new YuvImage(yuvData, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, outputStream);
        Bitmap bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /** 旋转位图。 */
    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}