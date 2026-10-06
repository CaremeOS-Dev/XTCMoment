package com.xtc.moment.serve;

import android.os.Environment;
import android.support.media.ExifInterface;

import com.xtc.log.LogUtil;

import java.io.File;
import java.io.IOException;

/**
 * 实况照片（Live Photo）文件识别工具。
 */
public class LivePhotoServe {

    private static final String EXIF_LIVE_PHOTO_FLAG = "livePhoto";
    private static final String MP4_FORMAT = ".mp4";
    private static String TAG = LivePhotoServe.class.getSimpleName();
    private static String NAME_LIVE_PHOTO = "LivePhoto";
    public static String LV_VIDEO_PATH = "video";

    private static String cameraRootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator;
    private static String livePhotovideoPath = cameraRootPath + NAME_LIVE_PHOTO + File.separator + LV_VIDEO_PATH + File.separator;

    public static boolean isLivePhotoFile(String path) {
        try {
            if (EXIF_LIVE_PHOTO_FLAG.equals(new ExifInterface(path).getAttribute(ExifInterface.TAG_USER_COMMENT))) {
                return isExistVideoMapping(new File(path).getName());
            }
            return false;
        } catch (IOException e) {
            LogUtil.e(TAG, "isLivePhotoFile fail :", e);
            return false;
        }
    }

    public static boolean isExistVideoMapping(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length != 2) {
            return false;
        }
        String videoMapping = livePhotovideoPath + parts[0] + MP4_FORMAT;
        File file = new File(videoMapping);
        LogUtil.i(TAG, "isExistVideoMapping   fileName:" + fileName + "   videoMapping:" + videoMapping);
        return file.exists();
    }

    public static String getVideoFilePath(String photoPath) {
        String[] parts = new File(photoPath).getName().split("\\.");
        if (parts.length != 2) {
            return null;
        }
        File file = new File(livePhotovideoPath + parts[0] + MP4_FORMAT);
        if (file.exists()) {
            return file.getPath();
        }
        return null;
    }
}