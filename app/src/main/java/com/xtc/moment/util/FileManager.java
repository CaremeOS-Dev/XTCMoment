package com.xtc.moment.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.ui.widget.head.DefaultHeadManager;
import com.xtc.utils.storage.FileUtils;

import java.io.File;

/** Owns every directory the moment app writes to. */
public class FileManager {

    public static String JPG_FORMAT = ".jpg";
    public static String MP4_FORMAT = ".mp4";
    public static final String MP4_FORMAT_UPPER_CASE = ".MP4";
    public static String PNG_FORMAT = ".png";
    public static String WEBP_FORMAT = ".webp";
    public static String NAME_MOMENT_BACKGROUND = "moment_background";
    public static String NAME_MOMENT_LIKE = "moment_like";

    private static final String TAG = "FileManager";
    private static final String NAME_ALBUM = "Camera";
    private static final String NAME_CAMERA = "camera";
    private static final String NAME_IMAGE_THUMNAIL = "ImageThumbnail";
    private static final String NAME_LIVE_PHOTO_CACHE = "livePhoto";
    private static final String NAME_THIRD_VIDEO = "ThirdVideo";
    private static final String NAME_THIRD_VIDEO_THUMNAIL = "ThirdVideoThumbnail";
    private static final String NAME_VIDEO = "Video";
    private static final String NAME_VIDEO_THUMNAIL = "VideoThumbnail";

    private static String SHARE_FOLDER_PATH = null;
    private static String albumPath;
    private static String cameraRootPath;
    private static String cameraThirdRootPath;
    private static String imageThumbnailPath;
    private static String launcherPath;
    private static String livePhotoCachePath;
    private static String momentBackgroundRootPath;
    private static String momentLikeRootPath;
    private static String momentPath;
    private static String myIconPath;
    private static String thirdVideoPath;
    private static String thirdVideoThumnailPath;
    private static String videoPath;
    private static String videoThumnailPath;

    private FileManager() {
    }

    public static void initFolder() {
        FileUtils.makeDirs(momentLikeRootPath);
        FileUtils.makeDirs(momentBackgroundRootPath);
    }

    /** Resolves every managed directory for [context]. */
    public static void init(Context context) {
        cameraRootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator;
        videoPath = cameraRootPath + NAME_VIDEO + File.separator;
        albumPath = cameraRootPath + NAME_ALBUM + File.separator;
        momentPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator;
        videoThumnailPath = momentPath + NAME_VIDEO_THUMNAIL + File.separator;
        imageThumbnailPath = momentPath + NAME_IMAGE_THUMNAIL + File.separator;
        livePhotoCachePath = momentPath + NAME_LIVE_PHOTO_CACHE + File.separator;
        cameraThirdRootPath = "/mnt/sdcard/xtc/ibwatch/camera/" + NAME_CAMERA + File.separator;
        thirdVideoPath = cameraThirdRootPath + NAME_THIRD_VIDEO + File.separator;
        thirdVideoThumnailPath = cameraThirdRootPath + NAME_THIRD_VIDEO_THUMNAIL + File.separator;
        momentLikeRootPath = momentPath + NAME_MOMENT_LIKE + File.separator;
        momentBackgroundRootPath = momentPath + NAME_MOMENT_BACKGROUND + File.separator;
        SHARE_FOLDER_PATH = momentPath + "share" + File.separator;
        LogUtil.d(TAG, "init");
    }

    public static String getCameraDir() {
        return cameraRootPath;
    }

    public static String getVideoDir() {
        return videoPath;
    }

    public static String getAlbumPath() {
        return albumPath;
    }

    public static String getVideoDir(boolean send) {
        return videoPath + (send ? "send" : "receive") + File.separator;
    }

    public static String getVideoThumnailDir() {
        return videoThumnailPath;
    }

    public static String getImageThumbnailPath() {
        return imageThumbnailPath;
    }

    public static String getThirdVideoThumnailPath() {
        return thirdVideoThumnailPath;
    }

    public static String getThirdVideoPath() {
        return thirdVideoPath;
    }

    public static String getLivePhotoCachePath() {
        return livePhotoCachePath;
    }

    /** @return the path of the current user icon. */
    public static String getSharedPath(Context context) {
        if (TextUtils.isEmpty(myIconPath)) {
            myIconPath = getMyIconPath(context);
        }
        return myIconPath;
    }

    public static String getMomentBackgroundRootPath() {
        return momentBackgroundRootPath;
    }

    public static String getMomentLikeRootPath() {
        return momentLikeRootPath;
    }

    public static String getMomentPath() {
        return momentPath;
    }

    public static String getShareFolderPath() {
        return SHARE_FOLDER_PATH;
    }

    public static String getLauncherPath() {
        return launcherPath;
    }

    /** @return the path of the user icon. */
    public static String getMyIconPath(Context context) {
        if (TextUtils.isEmpty(myIconPath)) {
            setMyIconPath(context);
        }
        return myIconPath;
    }

    /** Resolves the current account icon, falling back to the default avatar. */
    public static synchronized void setMyIconPath(Context context) {
        myIconPath = WatchAccountBase.getLocalIconPath(context);
        if (TextUtils.isEmpty(myIconPath)) {
            myIconPath = DefaultHeadManager.getDefaultHead(context);
        }
    }
}