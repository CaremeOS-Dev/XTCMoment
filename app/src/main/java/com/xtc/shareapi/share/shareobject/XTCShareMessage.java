package com.xtc.shareapi.share.shareobject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

import java.io.ByteArrayOutputStream;

/**
 * 分享消息封装，持有具体的分享对象（文本/图片/视频等）与标题、描述、缩略图等公共信息。
 */
public class XTCShareMessage implements IShareObject {

    private static final int DESCRIPTION_LENGTH_LIMIT = 1024;
    private static final int MESSAGE_ACTION_LENGTH_LIMIT = 2048;
    private static final int MESSAGE_EXT_LENGTH_LIMIT = 2048;
    private static final int MINI_PROGRAM_THUMB_LENGTH = 131072;
    private static final int TITLE_LENGTH_LIMIT = 512;
    private static final String TAG = OpenApiConstant.TAG + XTCShareMessage.class.getSimpleName();

    private String description;
    private IShareObject shareObject;
    private String action;
    private String ext;
    private byte[] thumbData;
    private int actionType;

    public XTCShareMessage() {
    }

    public XTCShareMessage(IShareObject shareObject) {
        this.shareObject = shareObject;
    }

    @Override
    public int type() {
        return 100;
    }

    @Override
    public void toBundle(Bundle bundle) {
        Builder.toBundle(this);
    }

    @Override
    public XTCShareMessage fromBundle(Bundle bundle) {
        return Builder.fromBundle(bundle);
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        String description = this.description;
        if (description != null && description.length() > DESCRIPTION_LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, description is invalid");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, description is invalid");
            return response;
        }
        if (this.shareObject == null) {
            Log.e(TAG, "checkArgs fail, shareObject is null");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, shareObject is null");
            return response;
        }
        String action = this.action;
        if (action != null && action.length() > MESSAGE_ACTION_LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, messageAction is too long");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, messageAction is too long");
            return response;
        }
        String ext = this.ext;
        if (ext != null && ext.length() > MESSAGE_EXT_LENGTH_LIMIT) {
            Log.e(TAG, "checkArgs fail, messageExt is too long");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail, messageExt is too long");
            return response;
        }
        return this.shareObject.checkArgs();
    }

    /** 当前实际分享内容的类型。 */
    public final int getType() {
        IShareObject shareObject = this.shareObject;
        if (shareObject == null) {
            return 0;
        }
        return shareObject.type();
    }

    /** 设置缩略图，压缩为 JPEG 字节数据。 */
    public final void setThumbImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            this.thumbData = outputStream.toByteArray();
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "bitmapToByteArray exception:" + e.getMessage());
        }
    }

    public static String getTAG() {
        return TAG;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getThumbData() {
        return thumbData;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public IShareObject getShareObject() {
        return shareObject;
    }

    public void setShareObject(IShareObject shareObject) {
        this.shareObject = shareObject;
    }

    @Override
    public String toString() {
        return "XTCShareMessage{description='" + description + "', actionType=" + actionType + ", action='" + action
                + "', ext='" + ext + "', shareObject=" + shareObject + '}';
    }

    /**
     * 分享消息的 Bundle 序列化/反序列化工具。
     */
    public static class Builder {

        public static Bundle toBundle(XTCShareMessage message) {
            Bundle bundle = new Bundle();
            bundle.putString(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_EXT, message.ext);
            bundle.putString(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_DESC, message.description);
            bundle.putByteArray(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_THUMB, message.thumbData);
            bundle.putString(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_ACTION, message.action);
            bundle.putInt(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_ACTION_TYPE, message.actionType);
            if (message.shareObject != null) {
                Log.d(XTCShareMessage.TAG, "shareObject = " + message.getShareObject());
                Log.d(XTCShareMessage.TAG, "shareObject className = " + message.getShareObject().getClass().getName());
                bundle.putString(OpenApiConstant.BuilderConstant.KEY_IDENTIFIER, message.getShareObject().getClass().getName());
                message.shareObject.toBundle(bundle);
            }
            return bundle;
        }

        public static XTCShareMessage fromBundle(Bundle bundle) {
            XTCShareMessage message = new XTCShareMessage();
            message.action = bundle.getString(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_ACTION);
            message.ext = bundle.getString(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_EXT);
            message.description = bundle.getString(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_DESC);
            message.thumbData = bundle.getByteArray(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_THUMB);
            message.actionType = bundle.getInt(OpenApiConstant.XTCShareMessageConstant.BUNDLE_MESSAGE_ACTION_TYPE);
            String shareObjectClassName = bundle.getString(OpenApiConstant.BuilderConstant.KEY_IDENTIFIER);
            Log.d(XTCShareMessage.TAG, "shareObject className = " + shareObjectClassName);
            if (!TextUtils.isEmpty(shareObjectClassName)) {
                try {
                    message.setShareObject((IShareObject) Class.forName(shareObjectClassName).newInstance());
                    message.setShareObject((IShareObject) message.getShareObject().fromBundle(bundle));
                    return message;
                } catch (Exception e) {
                    Log.e(XTCShareMessage.TAG, "error = " + e.getMessage());
                }
            }
            return message;
        }
    }
}