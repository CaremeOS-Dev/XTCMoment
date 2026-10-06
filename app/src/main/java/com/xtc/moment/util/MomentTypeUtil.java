package com.xtc.moment.util;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.utils.encode.JSONUtil;

/**
 * 动态类型判定与展示类型映射工具。
 */
public class MomentTypeUtil {

    private static final String TAG = "MomentTypeUtil";

    public static final int ITEM_TYPE_NORMAL = 1;
    public static final int ITEM_TYPE_EMPTY = 2;
    public static final int ITEM_TYPE_PHOTO = 4;
    public static final int ITEM_TYPE_VOICE = 5;
    public static final int ITEM_TYPE_VIDEO = 6;
    public static final int ITEM_TYPE_SHARE_TEXT = 7;
    public static final int ITEM_TYPE_SHARE_IMAGE = 8;
    public static final int ITEM_TYPE_SHARE_APP = 9;
    public static final int ITEM_TYPE_HEADER = 10;
    public static final int ITEM_TYPE_FOOTER = 11;
    public static final int ITEM_TYPE_OFFICIAL_PHOTO = 20;
    public static final int ITEM_TYPE_OFFICIAL_PHOTO_TEXT = 21;
    public static final int ITEM_TYPE_OFFICIAL_PHOTO_TEXT_H5 = 22;
    public static final int ITEM_TYPE_LIVE_PHOTO = 23;
    public static final int ITEM_TYPE_SHARE_LIVE_PHOTO = 24;
    public static final int ITEM_TYPE_OFFICIAL_VIDEO = 25;
    public static final int ITEM_TYPE_SHARE_VIDEO = 26;
    public static final int ITEM_TYPE_SHARE_H5 = 27;
    public static final int ITEM_TYPE_PHOTOS = 28;
    public static final int ITEM_TYPE_VIDEO_TEXT = 29;
    public static final int ITEM_TYPE_MULTI_SHARE_IMAGE_TEXT = 30;

    public static boolean ableChangeVisibleRangeType(int type) {
        return (type == 11 || type == 11 || type == 12 || type == 13 || type == 14 || type == 25) ? false : true;
    }

    public static boolean isCommonPhoto(int type) {
        return type == 5 || type == 23;
    }

    public static boolean isFunVideo(int type) {
        return type == 2;
    }

    public static boolean isOfficialType(int type) {
        switch (type) {
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPhotoList(int type) {
        return 26 == type;
    }

    public static boolean isPointVideo(int type) {
        return type == 1;
    }

    public static boolean isSharePhoto(int type) {
        return type == 8 || type == 9 || type == 25;
    }

    public static boolean isShareVideo(int type) {
        return 24 == type;
    }

    public static boolean isVideo(int type) {
        return 6 == type;
    }

    public static boolean isVideoContent(int type) {
        return 27 == type;
    }

    public static boolean isXTCOfficialType(int type) {
        return type == 1;
    }

    public static boolean isOfficialType(DbMoment moment) {
        if (moment == null || moment.getWatchId() == null) {
            return false;
        }
        return moment.getWatchId().length() < 40 || isOfficialType(moment.getType().intValue());
    }

    public static boolean checkMomentIsFunVideo(DbMoment moment) {
        if (!isShareVideo(moment.getType().intValue())) {
            return false;
        }
        ShareVideoMoment shareVideoMoment = (ShareVideoMoment) JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class);
        return shareVideoMoment.getFunVideoParam() != null
                && isFunVideoOrPointVideo(shareVideoMoment.getFunVideoParam().getModelType());
    }

    public static boolean isFunVideoOrPointVideo(int modelType) {
        return isFunVideo(modelType) || isPointVideo(modelType);
    }

    public static int getMomentType(DbMoment moment) {
        int type = moment.getType().intValue();
        switch (type) {
            case 4:
            case 5:
                return ITEM_TYPE_PHOTO;
            case 6:
                return ITEM_TYPE_VIDEO;
            case 7:
                return ITEM_TYPE_SHARE_TEXT;
            case 8:
                return ITEM_TYPE_SHARE_IMAGE;
            case 9:
                return ITEM_TYPE_SHARE_APP;
            default:
                switch (type) {
                    case 11:
                        return ITEM_TYPE_OFFICIAL_PHOTO;
                    case 12:
                        return ITEM_TYPE_OFFICIAL_PHOTO_TEXT;
                    case 13:
                        return ITEM_TYPE_OFFICIAL_VIDEO;
                    case 14:
                        return ITEM_TYPE_OFFICIAL_PHOTO_TEXT_H5;
                    default:
                        switch (type) {
                            case 22:
                                return ITEM_TYPE_LIVE_PHOTO;
                            case 23:
                                return ITEM_TYPE_SHARE_LIVE_PHOTO;
                            case 24:
                                return ITEM_TYPE_SHARE_VIDEO;
                            case 25:
                                return ITEM_TYPE_SHARE_H5;
                            case 26:
                                return ITEM_TYPE_PHOTOS;
                            case 27:
                                return ITEM_TYPE_VIDEO_TEXT;
                            case 28:
                                return ITEM_TYPE_MULTI_SHARE_IMAGE_TEXT;
                            default:
                                return ITEM_TYPE_NORMAL;
                        }
                }
        }
    }
}