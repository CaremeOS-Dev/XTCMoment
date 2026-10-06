package com.xtc.shareapi.share.jumpmoment;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IJumpToMomentObject;

/**
 * 由相册入口跳转好友圈发动态的入参。
 */
public class JumpToMomentFromAlbum implements IJumpToMomentObject {

    private static final String TAG = "JumpToMomentFromAlbum";

    private boolean isFromAlbum;
    private Bundle albumBundle;
    private JumpToMomentBase jumpToMomentBase;

    @Override
    public int type() {
        return FROM_ALBUM;
    }

    public boolean isFromAlbum() {
        return isFromAlbum;
    }

    public void setFromAlbum(boolean fromAlbum) {
        this.isFromAlbum = fromAlbum;
    }

    public Bundle getAlbumBundle() {
        return albumBundle;
    }

    public void setAlbumBundle(Bundle albumBundle) {
        this.albumBundle = albumBundle;
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
        if (!isFromAlbum) {
            Log.e(TAG, "check fail , isFromAlbum is need to is set true");
            response.setCode(6);
            response.setErrorDesc("check fail , isFromAlbum is need to is set true");
            return response;
        }
        Bundle bundle = this.albumBundle;
        if (bundle == null) {
            Log.e(TAG, "check fail , camaraBundle null");
            response.setCode(6);
            response.setErrorDesc("check fail , camaraBundle null");
            return response;
        }
        if (bundle.getInt("com.xtc.camera.EXTRA_PHOTO_TYPE") == 2) {
            Log.e(TAG, "check fail , is not support  live photo");
            response.setCode(6);
            response.setErrorDesc("check fail , is not support  live photo");
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