package com.xtc.shareapi.share.bean;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;

/**
 * 消息卡片图片的尺寸参数，按类型决定宽高。
 */
public class MessageBitmapArgs implements IBundleSerialize {

    private int width;
    private int height;
    private int type;

    public MessageBitmapArgs() {
        setWidthAndHeight(1);
    }

    public MessageBitmapArgs(int type) {
        this.type = type;
        setWidthAndHeight(type);
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putInt(OpenApiConstant.MessageBitmapArgsConstant.DEBRIS_HEIGHT, height);
        bundle.putInt(OpenApiConstant.MessageBitmapArgsConstant.DEBRIS_WIDTH, width);
        bundle.putInt(OpenApiConstant.MessageBitmapArgsConstant.DEBRIS_TYPE, type);
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        MessageBitmapArgs args = new MessageBitmapArgs();
        args.setHeight(bundle.getInt(OpenApiConstant.MessageBitmapArgsConstant.DEBRIS_HEIGHT));
        args.setWidth(bundle.getInt(OpenApiConstant.MessageBitmapArgsConstant.DEBRIS_WIDTH));
        args.setType(bundle.getInt(OpenApiConstant.MessageBitmapArgsConstant.DEBRIS_TYPE));
        return args;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        if (type <= 0) {
            Log.e("MessageBitmapArgs", "checkArgs fail, the type is false");
            response.setCode(6);
            response.setErrorDesc("share_argument_error,the type is false");
            return response;
        }
        response.setCode(1);
        return response;
    }

    /** 根据类型设置默认宽高。 */
    public void setWidthAndHeight(int type) {
        if (type == 1) {
            setWidth(160);
            setHeight(OpenApiConstant.MessageBitmapArgsConstant.MESSAGE_HEIGHT_1);
            return;
        }
        if (type == 2) {
            setWidth(250);
            setHeight(OpenApiConstant.MessageBitmapArgsConstant.MESSAGE_HEIGHT_2);
        } else if (type == 3) {
            setWidth(OpenApiConstant.MessageBitmapArgsConstant.MESSAGE_WIDTH_3);
            setHeight(154);
        } else if (type == 4) {
            setWidth(OpenApiConstant.MessageBitmapArgsConstant.MESSAGE_WIDTH_4);
            setHeight(160);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        setWidthAndHeight(type);
    }

    @Override
    public String toString() {
        return "MessageBitmapArgs{type=" + type + ", width=" + width + ", height=" + height + '}';
    }
}