package com.xtc.shareapi.share.jumpmoment;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IJumpToMomentObject;

/**
 * 由相机入口跳转好友圈发动态的入参。
 */
public class JumpToMomentFromCamara implements IJumpToMomentObject {

    private static final String TAG = "JumpToMomentFromCamara";

    private Bundle camaraBundle;
    private JumpToMomentBase jumpToMomentBase;

    @Override
    public int type() {
        return FROM_CAMARA;
    }

    public void setCamaraBundle(Bundle camaraBundle) {
        this.camaraBundle = camaraBundle;
    }

    public Bundle getCamaraBundle() {
        return camaraBundle;
    }

    public void setJumpToMomentBase(JumpToMomentBase jumpToMomentBase) {
        this.jumpToMomentBase = jumpToMomentBase;
    }

    public JumpToMomentBase getJumpToMomentBase() {
        return jumpToMomentBase;
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        Bundle bundle = this.camaraBundle;
        if (bundle == null) {
            Log.e(TAG, "check fail , camaraBundle is null");
            response.setCode(6);
            response.setErrorDesc("check fail , camaraBundle is nul");
            return response;
        }
        if (bundle.getString("output", null) == null) {
            Log.e(TAG, "check fail , camaraBundle photo data is null");
            response.setCode(6);
            response.setErrorDesc("check fail ,  camaraBundle photo data is nul");
            return response;
        }
        JumpToMomentBase jumpToMomentBase = this.jumpToMomentBase;
        if (jumpToMomentBase != null) {
            return jumpToMomentBase.checkArgs();
        }
        response.setCode(1);
        return response;
    }
}