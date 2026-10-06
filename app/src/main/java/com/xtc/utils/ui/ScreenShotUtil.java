package com.xtc.utils.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/** Captures the visible content of an activity and saves it as a PNG. */
public class ScreenShotUtil {

    /** Saves a screenshot of {@code activity} to {@code file}. */
    public static void save(Activity activity, File file) throws Throwable {
        if (file == null) {
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = null;
        try {
            try {
                try {
                    outputStream = new FileOutputStream(file);
                    getScreenBitmap(activity).compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Throwable throwable) {
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    throw throwable;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Captures the decor view of the activity, excluding the status bar. */
    private static Bitmap getScreenBitmap(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap drawingCache = decorView.getDrawingCache();
        Rect visibleFrame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);
        int top = visibleFrame.top;
        Bitmap bitmap = Bitmap.createBitmap(drawingCache, 0, top,
                activity.getWindowManager().getDefaultDisplay().getWidth(),
                activity.getWindowManager().getDefaultDisplay().getHeight() - top);
        decorView.destroyDrawingCache();
        return bitmap;
    }
}