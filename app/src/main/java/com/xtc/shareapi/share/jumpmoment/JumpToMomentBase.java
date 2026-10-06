package com.xtc.shareapi.share.jumpmoment;

import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.bean.PoiBean;
import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IJumpToMomentObject;

/**
 * 跳转好友圈发动态的通用入参，包含文本与位置兴趣点。
 */
public class JumpToMomentBase implements IJumpToMomentObject {

    private static final String TAG = "Share_JumpToMomentBase";

    private String text;
    private PoiBean poiBean;

    public static String getTAG() {
        return TAG;
    }

    @Override
    public int type() {
        return FROM_OUTSIDE;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PoiBean getPoiBean() {
        return poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        if (!TextUtils.isEmpty(text) && text.length() > 40) {
            Log.e(TAG, "check fail , text length is more than 40");
            response.setCode(6);
            response.setErrorDesc("check fail , text length is more than 40");
            return response;
        }
        PoiBean poiBean = this.poiBean;
        if (poiBean != null) {
            return poiBean.checkArgs();
        }
        if (TextUtils.isEmpty(text) && this.poiBean == null) {
            Log.e(TAG, "check fail , text or poi is at least one ");
            return response;
        }
        response.setCode(1);
        return response;
    }
}