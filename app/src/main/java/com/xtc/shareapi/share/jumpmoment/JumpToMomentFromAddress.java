package com.xtc.shareapi.share.jumpmoment;

import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IJumpToMomentObject;

/**
 * 由地址簿入口跳转好友圈发动态的入参。
 */
public class JumpToMomentFromAddress implements IJumpToMomentObject {

    private static final String TAG = "JumpToMomentFromAddress";

    private String addressId;
    private JumpToMomentBase base;

    @Override
    public int type() {
        return FROM_OUTSIDE;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setBase(JumpToMomentBase base) {
        this.base = base;
    }

    public JumpToMomentBase getBase() {
        return base;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        if (TextUtils.isEmpty(addressId)) {
            Log.e(TAG, "checkArgs fail ,addressId is null or empty");
            response.setCode(6);
            response.setErrorDesc("addressId is null or empty");
            return response;
        }
        JumpToMomentBase base = this.base;
        if (base == null) {
            Log.e(TAG, "checkArgs fail ,poiInfo is null");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail ,poiInfo is null");
            return response;
        }
        return base.checkArgs();
    }
}