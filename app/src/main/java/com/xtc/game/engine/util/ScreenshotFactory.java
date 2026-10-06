package com.xtc.game.engine.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.xtc.log.LogUtil;

import java.nio.ByteBuffer;

/**
 * 截图工厂：从 GL 画面抓取像素并保存为图片，同时校验图片是否有效。
 */
public class ScreenshotFactory {

    private static final String TAG = "ScreenshotFactory";

    /** 截图保存回调。 */
    public interface SaveScreenshotListener {

        /** 保存失败。 */
        void onSaveFailed();

        /** 保存成功。 */
        void onSaveSuccess(String filePath);
    }

    public static void saveScreenshot(int x, int y, int width, int height, boolean flipY, String filePath,
                                      final SaveScreenshotListener listener) {
        FileHandle fileHandle;
        try {
            fileHandle = new FileHandle(filePath);
        } catch (Exception e) {
            LogUtil.e(TAG, "saveScreenshot error:", e);
            if (listener != null) {
                MainHandlerUtil.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSaveFailed();
                    }
                });
            }
            return;
        }
        if (fileHandle.exists()) {
            return;
        }
        Pixmap pixmap = capturePixmap(x, y, width, height, flipY);
        PixmapIO.writePNG(fileHandle, pixmap);
        pixmap.dispose();
        final boolean valid = isFileValid(filePath);
        if (listener != null) {
            MainHandlerUtil.post(new Runnable() {
                @Override
                public void run() {
                    if (valid) {
                        listener.onSaveSuccess(filePath);
                    } else {
                        listener.onSaveFailed();
                    }
                }
            });
        }
    }

    private static Pixmap capturePixmap(int x, int y, int width, int height, boolean flipY) {
        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x, y, width, height);
        if (flipY) {
            ByteBuffer buffer = pixmap.getPixels();
            byte[] rowBytes = new byte[width * height * 4];
            int rowSize = width * 4;
            for (int row = 0; row < height; row++) {
                buffer.position(((height - row) - 1) * rowSize);
                buffer.get(rowBytes, row * rowSize, rowSize);
            }
            buffer.clear();
            buffer.put(rowBytes);
        }
        return pixmap;
    }

    public static boolean isFileValid(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap == null) {
            return false;
        }
        byte[] pixels = toRgbBytes(bitmap);
        bitmap.recycle();
        LogUtil.i(TAG, "bytes size:" + pixels.length);
        for (byte pixel : pixels) {
            if (pixel != 0) {
                return true;
            }
        }
        LogUtil.w(TAG, "saveScreenshot fileValid error");
        return false;
    }

    public static byte[] toRgbBytes(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        byte[] rgba = buffer.array();
        byte[] rgb = new byte[(rgba.length / 4) * 3];
        int pixelCount = rgba.length / 4;
        for (int index = 0; index < pixelCount; index++) {
            int rgbIndex = index * 3;
            int rgbaIndex = index * 4;
            rgb[rgbIndex] = rgba[rgbaIndex];
            rgb[rgbIndex + 1] = rgba[rgbaIndex + 1];
            rgb[rgbIndex + 2] = rgba[rgbaIndex + 2];
        }
        return rgb;
    }
}