package com.xtc.moment.module.widget.livephotoView;

/**
 * Delay constants used by the live photo mask animation.
 *
 * <p>The first pre-play is intentionally slower so the mask is not flashed while the clip warms up.
 */
public class LivePhotoDelayTimeUtil {

    /** Milliseconds the mask stays visible after a normal play starts. */
    private static final int DEFAULT_DELAY_PLAY_MASK_TIME = 50;
    /** Extra milliseconds added to the first pre-play mask. */
    private static final int DEFAULT_DELAY_PRE_PLAY_MASK_TIME = 400;
    /** Extra delay applied only to the very first pre-play of the process. */
    private static final int FIRST_PRE_PLAY_EXTRA_DELAY = 300;
    /** Longer mask time used when the clip is known to load slowly (qiniu Record.TTL_MIN_SECONDS). */
    private static final int LENGTHENED_PLAY_MASK_TIME = 600;

    private static int delayPlayMaskTime = DEFAULT_DELAY_PLAY_MASK_TIME;
    private static boolean isFirstPrePlay = true;

    public static int getDelayPrePlayMaskTime() {
        if (isFirstPrePlay) {
            isFirstPrePlay = false;
            return DEFAULT_DELAY_PRE_PLAY_MASK_TIME + FIRST_PRE_PLAY_EXTRA_DELAY;
        }
        return DEFAULT_DELAY_PRE_PLAY_MASK_TIME;
    }

    public static int getDelayPlayMaskTime() {
        return delayPlayMaskTime;
    }

    public static void lengthenPlayMaskTime() {
        delayPlayMaskTime = LENGTHENED_PLAY_MASK_TIME;
    }

    public static void resetPlayMaskTime() {
        delayPlayMaskTime = DEFAULT_DELAY_PLAY_MASK_TIME;
    }

    public static void resetFirstPrePlayFlag() {
        isFirstPrePlay = true;
    }
}