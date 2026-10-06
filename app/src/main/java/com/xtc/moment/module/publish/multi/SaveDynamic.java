package com.xtc.moment.module.publish.multi;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.publish.multi.bean.FunVideoParms;
import com.xtc.moment.module.publish.multi.bean.PhotoBean;
import com.xtc.moment.module.publish.text.PublishTextBean;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.SharedManager;

import java.util.ArrayList;

/**
 * Persists the half finished publish draft so the flow survives a process restart.
 */
public class SaveDynamic {

    private static final String TAG = "SaveDynamic";

    private static final String SET_SAVE_TEXT_DYNAMIC = "save_text_drnamic";
    private static final String SET_SAVE_VIDEO_DYNAMIC = "save_video_drnamic";
    private static final String SET_SAVE_POI = "save_poi";
    private static final String SET_SAVE_FRIEND_VISIBLE = "save_friend_visible";
    private static final String SET_SAVE_PHOTO_DYNAMIC = "save_photo_drnamic";
    private static final String SET_SAVE_ISMOMEN_PHOTOVIEW = "is_moment_photo_view";
    private static final String SET_SAVE_PUBLISH_TEXT_BEAN = "save_publish_text_bean";
    private static final String IS_FROM_ALBUM = "isFromAlbum";
    private static final String IS_FROM_FUN_VIDEO = "isFromFunVideo";
    private static final String SAVE_FUN_VIDEO_PARMS = "saveFunVideoParms";
    private static final String HAS_LBS_PUBLISHED = "has_lbs_published";

    public static boolean saveTextDynamic(Context context, String text) {
        return SharedManager.getInstance(context).putString(SET_SAVE_TEXT_DYNAMIC, text);
    }

    public static String getSaveTextDynamic(Context context, String defaultValue) {
        return SharedManager.getInstance(context).getString(SET_SAVE_TEXT_DYNAMIC, defaultValue);
    }

    public static boolean removeContent(Context context) {
        return SharedManager.getInstance(context).remove(SET_SAVE_TEXT_DYNAMIC);
    }

    public static boolean isContent(Context context) {
        return !TextUtils.isEmpty(getSaveTextDynamic(context, ""));
    }

    public static boolean saveVideoDynamic(Context context, String video) {
        return SharedManager.getInstance(context).putString(SET_SAVE_VIDEO_DYNAMIC, video);
    }

    public static String getSaveVideoDynamic(Context context, String defaultValue) {
        return SharedManager.getInstance(context).getString(SET_SAVE_VIDEO_DYNAMIC, defaultValue);
    }

    public static boolean removeVideo(Context context) {
        return SharedManager.getInstance(context).remove(SET_SAVE_VIDEO_DYNAMIC);
    }

    public static boolean savePhotoDynamic(Context context, String photo) {
        return SharedManager.getInstance(context).putString(SET_SAVE_PHOTO_DYNAMIC, photo);
    }

    public static String getSavePhotoDynamic(Context context, String defaultValue) {
        return SharedManager.getInstance(context).getString(SET_SAVE_PHOTO_DYNAMIC, defaultValue);
    }

    public static boolean removePhoto(Context context) {
        return SharedManager.getInstance(context).remove(SET_SAVE_PHOTO_DYNAMIC);
    }

    public static boolean savePoi(Context context, PoiBean poiBean) {
        return SharedManager.getInstance(context).putString(SET_SAVE_POI, JSONUtil.toJSON(poiBean));
    }

    public static PoiBean getSavePoi(Context context) {
        return (PoiBean) JSONUtil.fromJSON(SharedManager.getInstance(context).getString(SET_SAVE_POI, ""),
                PoiBean.class);
    }

    public static void removePoi(Context context) {
        SharedManager.getInstance(context).remove(SET_SAVE_POI);
    }

    public static boolean saveFriendVisible(Context context, FriendsVisibleBean visibleBean) {
        return SharedManager.getInstance(context).putString(SET_SAVE_FRIEND_VISIBLE, JSONUtil.toJSON(visibleBean));
    }

    public static FriendsVisibleBean getSaveFriendVisible(Context context) {
        return (FriendsVisibleBean) JSONUtil.fromJSON(
                SharedManager.getInstance(context).getString(SET_SAVE_FRIEND_VISIBLE, ""), FriendsVisibleBean.class);
    }

    public static void removeFriendBean(Context context) {
        SharedManager.getInstance(context).remove(SET_SAVE_FRIEND_VISIBLE);
    }

    public static boolean savePublishTextBean(Context context, PublishTextBean publishTextBean) {
        return SharedManager.getInstance(context).putString(SET_SAVE_PUBLISH_TEXT_BEAN,
                JSONUtil.toJSON(publishTextBean));
    }

    public static PublishTextBean getSavePublishTextBean(Context context) {
        return (PublishTextBean) JSONUtil.fromJSON(
                SharedManager.getInstance(context).getString(SET_SAVE_PUBLISH_TEXT_BEAN, ""), PublishTextBean.class);
    }

    public static void removePublishTextBean(Context context) {
        SharedManager.getInstance(context).remove(SET_SAVE_PUBLISH_TEXT_BEAN);
    }

    public static void saveIsFromAlbum(Context context, boolean fromAlbum) {
        SharedManager.getInstance(context).putBoolean(IS_FROM_ALBUM, fromAlbum);
    }

    public static boolean getIsFromAlbum(Context context) {
        return SharedManager.getInstance(context).getBoolean(IS_FROM_ALBUM, false);
    }

    public static void saveIsFromFunVideo(Context context, boolean fromFunVideo) {
        SharedManager.getInstance(context).putBoolean(IS_FROM_FUN_VIDEO, fromFunVideo);
    }

    public static boolean getIsFromFunVideo(Context context) {
        return SharedManager.getInstance(context).getBoolean(IS_FROM_FUN_VIDEO, false);
    }

    public static boolean saveIsMomentPhotoView(Context context, boolean isMomentPhotoView) {
        return SharedManager.getInstance(context).putBoolean(SET_SAVE_ISMOMEN_PHOTOVIEW, isMomentPhotoView);
    }

    public static boolean getIsMomentPhotoView(Context context, boolean defaultValue) {
        return SharedManager.getInstance(context).getBoolean(SET_SAVE_ISMOMEN_PHOTOVIEW, defaultValue);
    }

    /** Serialises the picked image paths into the stored {@link PhotoBean} list. */
    public static String parseImages2Json(Context context, ArrayList<String> photoPaths) {
        if (CollectionUtil.isEmpty(photoPaths)) {
            return null;
        }
        ArrayList<PhotoBean> photoBeans = new ArrayList<PhotoBean>();
        for (int i = 0; i < photoPaths.size(); i++) {
            PhotoBean photoBean = new PhotoBean();
            photoBean.setPhotoPath(photoPaths.get(i));
            photoBeans.add(photoBean);
        }
        return JSONUtil.toJSON(photoBeans);
    }

    public static ArrayList<String> getPhotoPath(Context context) {
        String photoJson = SharedManager.getInstance(context).getString(SET_SAVE_PHOTO_DYNAMIC, "");
        LogUtil.i(TAG, "getPhotoPath photoPathJson" + photoJson);
        ArrayList<String> photoPaths = new ArrayList<String>();
        if (!TextUtils.isEmpty(photoJson)) {
            ArrayList<PhotoBean> photoBeans = new Gson().fromJson(photoJson,
                    new TypeToken<ArrayList<PhotoBean>>() {
                    }.getType());
            for (int i = 0; i < photoBeans.size(); i++) {
                LogUtil.i(TAG, "fromJsonsize" + photoBeans.get(i).getPhotoPath());
                photoPaths.add(photoBeans.get(i).getPhotoPath());
            }
        }
        return photoPaths;
    }

    /** Returns true when any part of a draft publish is still stored. */
    public static boolean hasDynamicData(Context context, String defaultValue) {
        return !TextUtils.isEmpty(getSaveVideoDynamic(context, defaultValue))
                || !TextUtils.isEmpty(getSavePhotoDynamic(context, defaultValue))
                || getSavePoi(context) != null
                || !TextUtils.isEmpty(getSaveTextDynamic(context, defaultValue))
                || getSaveFriendVisible(context) != null;
    }

    public static boolean saveIs(Context context, String value) {
        return SharedManager.getInstance(context).putString("saveIs", value);
    }

    public static String getSave(Context context, String defaultValue) {
        return SharedManager.getInstance(context).getString("saveIs", defaultValue);
    }

    /** Stores the parameters of a video published from a "fun" app bundle. */
    public static boolean saveFunParms(Context context, Bundle bundle) {
        LogUtil.i(TAG, "saveFunParms");
        String videoPath = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PATH);
        String videoParm = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PARAM);
        String packageName = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_APP_PACK_NAME);
        long videoLength = bundle.getLong(Constants.ShareVideoKey.FUN_VIDEO_LENGTH, 0L);
        byte[] videoIcon = bundle.getByteArray(Constants.ShareVideoKey.FUN_VIDEO_APP_ICON);
        String appName = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_APP_NAME);
        if (TextUtils.isEmpty(videoPath)) {
            videoPath = bundle.getString("output", null);
        }
        FunVideoParms funVideoParms = new FunVideoParms();
        funVideoParms.setVideoPath(videoPath);
        funVideoParms.setVideoParm(videoParm);
        funVideoParms.setVideoPackageName(packageName);
        funVideoParms.setVideoLength(videoLength);
        funVideoParms.setVideoIcon(videoIcon);
        funVideoParms.setVideoAppName(appName);
        return SharedManager.getInstance(context).putString(SAVE_FUN_VIDEO_PARMS, JSONUtil.toJSON(funVideoParms));
    }

    public static String getFunParms(Context context) {
        return SharedManager.getInstance(context).getString(SAVE_FUN_VIDEO_PARMS, "");
    }

    public static boolean hasFirstPublished(Context context) {
        return SharedManager.getInstance(context).getBoolean(HAS_LBS_PUBLISHED, false);
    }

    public static boolean saveFirstPublished(Context context) {
        return SharedManager.getInstance(context).putBoolean(HAS_LBS_PUBLISHED, true);
    }
}