package com.xtc.shareapi.share.utils;

/**
 * 分享能力支持情况查询。
 */
public class ShareSupportUtil {

    private static final String TAG = "Share_ShareSupportUtil";

    /** 单次分享数量上限。 */
    public static int getMaxSupportShareCount() {
        return 9;
    }

    /** 是否支持多张实况照片。 */
    public static boolean isSupportMultiLivePhotoShare() {
        return false;
    }

    /** 是否支持多个视频。 */
    public static boolean isSupportMultiVideoShare() {
        return false;
    }
}