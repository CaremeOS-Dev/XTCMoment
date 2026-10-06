package com.xtc.shareapi.share.shareobject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.shareapi.share.bean.MessageBitmapArgs;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

/**
 * 图片分享对象，支持本地路径或位图数据，并可携带弹窗/消息卡片尺寸参数。
 */
public class XTCImageObject implements Parcelable, IShareObject {

    private static final int CONTENT_LENGTH_LIMIT = 307200;
    private static final int PATH_LENGTH_LIMIT = 512;
    private static final String TAG = OpenApiConstant.TAG + XTCImageObject.class.getSimpleName();

    private byte[] imageData;
    private String imagePath;
    private String textMessage;
    private String description;
    private DialogBitmapArgs dialogBitmapArgs;
    private MessageBitmapArgs messageBitmapArgs;

    public XTCImageObject() {
    }

    public XTCImageObject(String imagePath) {
        this.imagePath = imagePath;
    }

    public XTCImageObject(Bitmap bitmap) {
        compressBitmap(bitmap);
    }

    /** 将位图压缩为 JPEG 字节数据。 */
    private void compressBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            this.imageData = outputStream.toByteArray();
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "WXImageObject <init>, exception:" + e.getMessage());
        }
    }

    @Override
    public int type() {
        return TYPE_IMAGE;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putByteArray(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_DATA, imageData);
        bundle.putString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_PATH, imagePath);
        bundle.putString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_TEXT_MESSAGE, textMessage);
        if (dialogBitmapArgs != null) {
            bundle.putString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_DIALOG_BITMAP_ARGS,
                    getDialogBitmapArgs().getClass().getName());
            dialogBitmapArgs.toBundle(bundle);
        }
        if (messageBitmapArgs != null) {
            Log.d("MessageBitmapArgs", "ssss" + getMessageBitmapArgs());
            bundle.putString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_MESSAGE_BITMAP_ARGS,
                    getMessageBitmapArgs().getClass().getName());
            messageBitmapArgs.toBundle(bundle);
        }
    }

    @Override
    public XTCImageObject fromBundle(Bundle bundle) {
        XTCImageObject imageObject = new XTCImageObject();
        imageObject.imageData = bundle.getByteArray(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_DATA);
        imageObject.imagePath = bundle.getString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_PATH);
        imageObject.textMessage = bundle.getString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_TEXT_MESSAGE);
        String dialogArgsClassName = bundle.getString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_DIALOG_BITMAP_ARGS);
        if (!TextUtils.isEmpty(dialogArgsClassName)) {
            try {
                imageObject.setDialogBitmapArgs((DialogBitmapArgs) Class.forName(dialogArgsClassName).newInstance());
                imageObject.setDialogBitmapArgs((DialogBitmapArgs) imageObject.getDialogBitmapArgs().fromBundle(bundle));
            } catch (Exception e) {
                Log.e(TAG, "error = " + e.getMessage());
                return imageObject;
            }
        }
        String messageArgsClassName = bundle.getString(OpenApiConstant.XTCImageConstant.BUNDLE_IMAGE_MESSAGE_BITMAP_ARGS);
        if (!TextUtils.isEmpty(messageArgsClassName)) {
            try {
                imageObject.setMessageBitmapArgs((MessageBitmapArgs) Class.forName(messageArgsClassName).newInstance());
                imageObject.setMessageBitmapArgs((MessageBitmapArgs) imageObject.getMessageBitmapArgs().fromBundle(bundle));
                return imageObject;
            } catch (Exception e) {
                Log.e(TAG, "error = " + e.getMessage());
            }
        }
        return imageObject;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        byte[] imageData = this.imageData;
        String imagePath = this.imagePath;
        if ((imageData != null && imageData.length != 0) || (imagePath != null && imagePath.length != 0)) {
            byte[] data = this.imageData;
            if (data != null && data.length > CONTENT_LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, content is too large");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,content is too large");
                return response;
            }
            String path = this.imagePath;
            if (path != null && path.length() > PATH_LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, path is invalid");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,path is invalid");
                return response;
            }
            if (path != null && getFileSize(path) > PATH_LENGTH_LIMIT) {
                Log.e(TAG, "checkArgs fail, image content is too large");
                response.setCode(6);
                response.setErrorDesc("share_argument_error,image content is too large");
                return response;
            }
            response.setCode(1);
            return response;
        }
        Log.e(TAG, "checkArgs fail, all arguments are null");
        response.setCode(6);
        response.setErrorDesc("share_argument_errorall arguments are null");
        return response;
    }

    /** 校验图片文件大小（原实现以路径长度作为判断依据）。 */
    private int getFileSize(String path) {
        Log.d(TAG, "image path = " + path);
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return path.length();
        }
        Log.e(TAG, "image not exist or not file!");
        return Integer.MAX_VALUE;
    }

    public void setBitmap(Bitmap bitmap) {
        compressBitmap(bitmap);
    }

    public byte[] getImageData() {
        return imageData;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public DialogBitmapArgs getDialogBitmapArgs() {
        return dialogBitmapArgs;
    }

    public void setDialogBitmapArgs(DialogBitmapArgs dialogBitmapArgs) {
        this.dialogBitmapArgs = dialogBitmapArgs;
    }

    public MessageBitmapArgs getMessageBitmapArgs() {
        return messageBitmapArgs;
    }

    public void setMessageBitmapArgs(MessageBitmapArgs messageBitmapArgs) {
        this.messageBitmapArgs = messageBitmapArgs;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(imagePath);
        parcel.writeString(textMessage);
        parcel.writeString(description);
    }

    public void readFromParcel(Parcel parcel) {
        this.imagePath = parcel.readString();
        this.textMessage = parcel.readString();
        this.description = parcel.readString();
    }

    protected XTCImageObject(Parcel parcel) {
        this.imagePath = parcel.readString();
        this.textMessage = parcel.readString();
        this.description = parcel.readString();
    }

    @Override
    public String toString() {
        return "XTCImageObject{imageData=" + Arrays.toString(imageData) + ", imagePath='" + imagePath
                + "', dialogBitmapArgs=" + dialogBitmapArgs + ", messageBitmapArgs=" + messageBitmapArgs
                + ", textMessage='" + textMessage + "', description='" + description + "'}";
    }

    public static final Parcelable.Creator<XTCImageObject> CREATOR = new Parcelable.Creator<XTCImageObject>() {
        @Override
        public XTCImageObject createFromParcel(Parcel parcel) {
            return new XTCImageObject(parcel);
        }

        @Override
        public XTCImageObject[] newArray(int size) {
            return new XTCImageObject[size];
        }
    };
}