package com.xtc.shareapi.share.utils;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

/**
 * 视频信息读取工具。
 */
public class VideoUtil {

    /** 读取视频时长（毫秒），失败返回 0。 */
    public static long getVideoLength(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (TextUtils.isEmpty(duration)) {
                return 0L;
            }
            return convertLong(duration, 0L);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    /** 字符串转 long，失败时返回默认值。 */
    public static long convertLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}