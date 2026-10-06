package com.xtc.shareapi.share.bean;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;

/**
 * 分享弹窗图片的裁剪与尺寸参数，按类型决定默认宽高。
 */
public class DialogBitmapArgs implements IBundleSerialize {

    private int cutTop;
    private int cutStart;
    private int cropWidth;
    private int cropHeight;
    private int width;
    private int height;
    private int type;

    public DialogBitmapArgs() {
    }

    public DialogBitmapArgs(int cutTop, int cutStart, int cropWidth, int cropHeight, int width, int height, int type) {
        this.cutTop = cutTop;
        this.cutStart = cutStart;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.type = type;
        this.width = width;
        this.height = height;
        setWidthAndHeight(type, width, height);
    }

    public DialogBitmapArgs(int cutTop, int cutStart, int cropWidth, int cropHeight, int type) {
        this(cutTop, cutStart, cropWidth, cropHeight, 0, 0, type);
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_HEIGHT, height);
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_WIDTH, width);
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CUT_TOP, cutTop);
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CROP_HEIGHT, cropHeight);
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CUT_START, cutStart);
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CROP_WIDTH, cropWidth);
        bundle.putInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_TYPE, type);
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        DialogBitmapArgs args = new DialogBitmapArgs();
        args.setHeight(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_HEIGHT));
        args.setWidth(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_WIDTH));
        args.setCutTop(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CUT_TOP));
        args.setCropHeight(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CROP_HEIGHT));
        args.setCutStart(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CUT_START));
        args.setCropWidth(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_CROP_WIDTH));
        args.setType(bundle.getInt(OpenApiConstant.DialogBitmapArgsConstant.DEBRIS_TYPE));
        return args;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        if (type <= 0) {
            Log.e("DialogBitmapArgs", "checkArgs fail, the type is false");
            response.setCode(6);
            response.setErrorDesc("share_argument_error,the type is false");
            return response;
        }
        response.setCode(1);
        return response;
    }

    /** 根据类型设置默认宽高。 */
    public void setWidthAndHeight(int type, int customWidth, int customHeight) {
        Log.d("DialogBitmapArgs", "type:" + type);
        if (type == 1) {
            setWidth(190);
            setHeight(OpenApiConstant.DialogBitmapArgsConstant.DIALOG_HEIGHT_1);
            return;
        }
        if (type == 2) {
            setWidth(304);
            setHeight(190);
            return;
        }
        if (type == 3) {
            setWidth(240);
            setHeight(220);
        } else if (type == 4) {
            Log.d("DialogBitmapArgs", "OpenApiConstant.DialogBitmapArgsConstant.TYPE_DOUBLE_PK:");
            setWidth(OpenApiConstant.DialogBitmapArgsConstant.DIALOG_WIDTH_4);
            setHeight(220);
        } else if (type == 5) {
            setWidth(customWidth);
            setHeight(customHeight);
        }
    }

    public int getCutTop() {
        return cutTop;
    }

    public void setCutTop(int cutTop) {
        this.cutTop = cutTop;
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

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCutStart() {
        return cutStart;
    }

    public void setCutStart(int cutStart) {
        this.cutStart = cutStart;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    @Override
    public String toString() {
        return "DialogBitmapArgs{type=" + type + ", cutTop=" + cutTop + ", cutStart=" + cutStart + ", width=" + width
                + ", height=" + height + ", cropHeight=" + cropHeight + ", cropWidth=" + cropWidth + '}';
    }
}