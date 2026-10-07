package com.xtc.web.core.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.module.Constants;
import com.xtc.utils.encode.Base64Util;
import com.xtc.utils.system.CameraUtils;
import com.xtc.web.core.CoreConstants;
import com.xtc.moment.R;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.CameraStickerData;
import com.xtc.web.core.data.req.ReqBtnText;
import com.xtc.web.core.data.resp.RespPhoto;
import com.xtc.web.core.jump.JumpManager;
import com.xtc.web.core.provider.JumpContentProvider;
import com.xtc.web.core.utils.JSONUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/** 拍照/选图管理器：拉起系统相机或相册，并把结果路径回传给 H5。 */
public class TakePhotoManager {

    private static final String TAG = CoreConstants.TAG + TakePhotoManager.class.getSimpleName();
    private static final String JUMP_RESULT_PATH = "/jumpActivityResult";
    private static final String EXTRA_STICKER_IP_ACTIVITIES = "camera_sticker_ip_activities";
    private static final String EXTRA_FROM_PHOTO = "xtc_from_photo";
    private static final String EXTRA_STICKER_ID = "camera_sticker_id";
    private static ContentObserver contentObserver;
    private static ContentResolver contentResolver;
    private static TakePhotoManager instance;

    private Context context;

    public TakePhotoManager(Context context) {
        this.context = context;
    }

    public static synchronized TakePhotoManager getInstance(Context context) {
        if (instance == null) {
            instance = new TakePhotoManager(context);
        }
        return instance;
    }

    public synchronized void takePhotoReturnPath(ReqBtnText reqBtnText,
            CompletionHandler<RespPhoto> completionHandler) {
        String leftText = this.context.getString(R.string.button_cancel);
        String rightText = this.context.getString(R.string.button_save);
        CameraStickerData cameraStickerData = null;
        if (reqBtnText != null) {
            cameraStickerData = reqBtnText.getCameraStickerData();
            if (!TextUtils.isEmpty(reqBtnText.getLeftText())) {
                leftText = reqBtnText.getLeftText();
            }
            if (!TextUtils.isEmpty(reqBtnText.getRightText())) {
                rightText = reqBtnText.getRightText();
            }
        }
        takePhoto(completionHandler, leftText, rightText, cameraStickerData);
    }

    public synchronized void takePhotoWithBtnTextReturnPath(ReqBtnText reqBtnText,
            CompletionHandler<RespPhoto> completionHandler) {
        String leftText = this.context.getString(R.string.button_cancel);
        String rightText = this.context.getString(R.string.button_save);
        CameraStickerData cameraStickerData = null;
        if (reqBtnText != null) {
            cameraStickerData = reqBtnText.getCameraStickerData();
            if (!TextUtils.isEmpty(reqBtnText.getLeftText())) {
                leftText = reqBtnText.getLeftText();
            }
            if (!TextUtils.isEmpty(reqBtnText.getRightText())) {
                rightText = reqBtnText.getRightText();
            }
        }
        takePhoto(completionHandler, leftText, rightText, cameraStickerData);
    }

    /** 拉起相机拍照。 */
    private void takePhoto(final CompletionHandler<RespPhoto> completionHandler, String leftText,
            String rightText, CameraStickerData cameraStickerData) {
        if (!CameraUtils.hasCamera(this.context)) {
            LogUtil.i(TAG, "the watch has not camera");
            RespPhoto response = new RespPhoto();
            response.setCode(RespPhoto.Code.NOT_PERMISSION);
            completionHandler.complete(response);
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, leftText);
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, rightText);
        Intent jumpIntent = new Intent(this.context, JumpManager.class);
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, JumpManager.class.getSimpleName());
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, CoreConstants.TakePhotoConstant.REQUEST_CAMERA);
        String requestKey = UUID.randomUUID().toString();
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST_KEY, requestKey);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, jumpIntent.toURI());
        if (cameraStickerData != null) {
            intent.putExtra(EXTRA_STICKER_IP_ACTIVITIES, cameraStickerData.getUseSticker());
            intent.putExtra(EXTRA_FROM_PHOTO, cameraStickerData.getSlideOut());
            intent.putExtra(EXTRA_STICKER_ID, cameraStickerData.getStickerId());
        }
        contentResolver = this.context.getApplicationContext().getContentResolver();
        final Uri jumpResultUri = Uri.parse(Constants.ProviderConstants.PREFIX_CONTENT_PROVIDER
                + JumpContentProvider.getFileProviderName(this.context) + JUMP_RESULT_PATH);
        if (contentObserver != null) {
            contentResolver.unregisterContentObserver(contentObserver);
            contentObserver = null;
        }
        contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                LogUtil.i(TAG, "onActivityResult");
                RespPhoto response = new RespPhoto();
                Cursor cursor = contentResolver.query(jumpResultUri, null, null, null, null);
                if (cursor == null) {
                    LogUtil.w(TAG, "cursorJump is null");
                    return;
                }
                if (cursor.moveToNext()) {
                    HashMap map = JSONUtil.fromJSON(cursor.getString(0), HashMap.class);
                    if (map == null) {
                        LogUtil.w(TAG, "remote data is null");
                        LogUtil.i(TAG, "onFailed");
                        cursor.close();
                        return;
                    }
                    if (map.containsKey("output")) {
                        String photoPath = (String) map.get("output");
                        LogUtil.d(TAG, " getFromAlbum onActivityResult: photoPath = " + photoPath);
                        if (photoPath != null) {
                            response.setCode(RespPhoto.Code.SUCCESS);
                            response.setData(CoreConstants.FileConstant.FILE_PREFIX + photoPath);
                        } else {
                            response.setCode(RespPhoto.Code.ENCODE_FAIL);
                        }
                    } else {
                        response.setCode(RespPhoto.Code.RETURN_FAIL);
                    }
                    completionHandler.complete(response);
                }
                cursor.close();
            }
        };
        contentResolver.registerContentObserver(jumpResultUri, true, contentObserver);
        JumpManager.start(this.context, intent, requestKey, CoreConstants.TakePhotoConstant.REQUEST_CAMERA);
    }

    /** 从相册选择图片。 */
    public synchronized void chooseImageReturnPath(final CompletionHandler<RespPhoto> completionHandler) {
        if (!CameraUtils.hasCamera(this.context)) {
            LogUtil.i(TAG, "the watch has not camera");
            RespPhoto response = new RespPhoto();
            response.setCode(RespPhoto.Code.NOT_PERMISSION);
            completionHandler.complete(response);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
        intent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, this.context.getString(R.string.button_cancel));
        intent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, this.context.getString(R.string.button_save));
        Intent jumpIntent = new Intent(this.context, JumpManager.class);
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_APP, JumpManager.class.getSimpleName());
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, CoreConstants.TakePhotoConstant.REQUEST_PHOTO);
        String requestKey = UUID.randomUUID().toString();
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST_KEY, requestKey);
        intent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, jumpIntent.toURI());
        contentResolver = this.context.getApplicationContext().getContentResolver();
        final Uri jumpResultUri = Uri.parse(Constants.ProviderConstants.PREFIX_CONTENT_PROVIDER
                + JumpContentProvider.getFileProviderName(this.context) + JUMP_RESULT_PATH);
        if (contentObserver != null) {
            contentResolver.unregisterContentObserver(contentObserver);
            contentObserver = null;
        }
        contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                LogUtil.i(TAG, "onActivityResult");
                RespPhoto response = new RespPhoto();
                Cursor cursor = contentResolver.query(jumpResultUri, null, null, null, null);
                if (cursor == null) {
                    LogUtil.w(TAG, "cursorJump is null");
                    return;
                }
                if (cursor.moveToNext()) {
                    HashMap map = JSONUtil.fromJSON(cursor.getString(0), HashMap.class);
                    if (map == null) {
                        LogUtil.w(TAG, "remote data is null");
                        LogUtil.i(TAG, "onFailed");
                        cursor.close();
                        return;
                    }
                    if (map.containsKey("output")) {
                        String photoPath = (String) map.get("output");
                        LogUtil.d(TAG, " getFromAlbum onActivityResult: photoPath = " + photoPath);
                        if (photoPath != null) {
                            response.setCode(RespPhoto.Code.SUCCESS);
                            response.setData(CoreConstants.FileConstant.FILE_PREFIX + photoPath);
                        } else {
                            response.setCode(RespPhoto.Code.ENCODE_FAIL);
                        }
                    } else {
                        response.setCode(RespPhoto.Code.RETURN_FAIL);
                    }
                    completionHandler.complete(response);
                }
                cursor.close();
            }
        };
        contentResolver.registerContentObserver(jumpResultUri, true, contentObserver);
        JumpManager.start(this.context, intent, requestKey, CoreConstants.TakePhotoConstant.REQUEST_PHOTO);
    }

    /** 把图片文件转成 base64。 */
    public static String imageToBase64(String path) {
        byte[] data = null;
        try {
            FileInputStream inputStream = new FileInputStream(path);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (Exception e) {
            LogUtil.w(TAG, "imageToBase64 error" + e);
            e.printStackTrace();
        }
        String base64 = Base64Util.encode(data);
        LogUtil.d(TAG, "encode to base64 " + base64);
        return base64;
    }

    /** 注销拍照结果监听。 */
    public static void unRegisterTakePhotoCallback() {
        ContentObserver observer = contentObserver;
        if (observer != null) {
            contentResolver.unregisterContentObserver(observer);
            contentResolver = null;
            contentObserver = null;
            LogUtil.d(TAG, "release contentProvider");
        }
    }
}