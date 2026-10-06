package com.xtc.moment.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.Window;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.storage.SharedManager;
import com.xtc.utils.ui.ImageUtils;

import java.io.File;

/**
 * 视频缩略图与视频窗口辅助工具。
 */
public class ChatVideoUtil {

    private static final String TAG = "ChatVideoUtil";

    public static int getWidth(Context context) {
        return (int) context.getResources().getDimension(R.dimen.video_thumnail_width);
    }

    public static int getHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.video_thumnail_height);
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        LogUtil.i(TAG, "getVideoThumbnail: " + videoPath);
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationMs = 0L;
            if (!TextUtils.isEmpty(duration)) {
                durationMs = convertLong(duration, 0L);
            }
            return extractFrame(1L, durationMs, retriever);
        } catch (Throwable t) {
            LogUtil.e(TAG, "getVideoThumbnail error: ", t);
            return null;
        }
    }

    public static Bitmap extractFrame(long startSecond, long endSecond, MediaMetadataRetriever retriever) {
        Bitmap result = null;
        for (long second = startSecond; second < endSecond; second += 1000) {
            Bitmap frame = retriever.getFrameAtTime(second * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (frame != null) {
                return frame;
            }
            result = frame;
        }
        return result;
    }

    private static boolean saveThumnail(Bitmap bitmap, String savePath, Bitmap.CompressFormat format) {
        boolean saved = ImageUtils.save(bitmap, savePath, format);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return saved;
    }

    public static String saveThumnail(String videoPath, String savePath, int maxWidth, int maxHeight) {
        if (FileUtils.exists(savePath)) {
            return savePath;
        }
        Bitmap thumbnail = getVideoThumbnail(videoPath);
        if (thumbnail == null) {
            LogUtil.e(TAG, "getVideoThumbnail failed! path:" + videoPath);
            return null;
        }
        int width = thumbnail.getWidth();
        int height = thumbnail.getHeight();
        if (width <= 880 || height <= 990) {
            LogUtil.i(TAG, "fit size, no need to compress");
        } else {
            float scale = ImageUtil.calculateInSampleSize(width, height, maxWidth, maxHeight);
            LogUtil.d(TAG, "thumbnail compress scale = " + scale + ", resWidth = " + width + ", resHeight = " + height);
            thumbnail = ImageUtils.scale(thumbnail, (int) (width / scale), (int) (height / scale));
        }
        if (thumbnail == null) {
            LogUtil.e(TAG, "getThumnailBitmap failed! path:" + videoPath);
            return null;
        }
        if (!saveThumnail(thumbnail, savePath, Bitmap.CompressFormat.WEBP)) {
            LogUtil.e(TAG, "save ThumnailBitmap failed! savePath:" + savePath);
        }
        return savePath;
    }

    public static long convertLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static void keepScreenOn(Window window) {
        if (window == null) {
            return;
        }
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void cancelKeepScreenOn(Window window) {
        if (window == null) {
            return;
        }
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Deprecated
    public static boolean isEntryDisplay(Context context, String key) {
        return isEntryDisplay(context, key, false);
    }

    @Deprecated
    public static boolean isEntryDisplay(Context context, String key, boolean defaultValue) {
        return SharedManager.getInstance(context).getBoolean(key, defaultValue);
    }

    public static String getVideoName(String videoPath) {
        String name = new File(videoPath).getName();
        int index = name.indexOf(FileManager.MP4_FORMAT);
        if (index < 0) {
            index = name.length() - 1;
        }
        return name.substring(0, index);
    }
}