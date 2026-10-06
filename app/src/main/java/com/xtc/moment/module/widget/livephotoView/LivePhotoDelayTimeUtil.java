package com.xtc.moment.module.widget.livephotoView;

import com.qiniu.android.dns.Record;

/**
 * 实况照片播放遮罩延时工具。
 */
public class LivePhotoDelayTimeUtil {

    private static int DELAY_PLAY_MASK_TIME = 50;
    private static int DELAY_PRE_PLAY_MASK_TIME = 400;

    private static boolean isFirstPrePlay = true;
    private static String watchInnerModel;

    public static int getDelayPrePlayMaskTime() {
        if (isFirstPrePlay) {
            isFirstPrePlay = false;
            return DELAY_PRE_PLAY_MASK_TIME + 300;
        }
        return DELAY_PRE_PLAY_MASK_TIME;
    }

    public static int getDelayPlayMaskTime() {
        return DELAY_PLAY_MASK_TIME;
    }

    public static void lengthenPlayMaskTime() {
        DELAY_PLAY_MASK_TIME = Record.TTL_MIN_SECONDS;
    }

    public static void resetPlayMaskTime() {
        DELAY_PLAY_MASK_TIME = 50;
    }

    public static void resetFirstPrePlayFlag() {
        isFirstPrePlay = true;
    }
}