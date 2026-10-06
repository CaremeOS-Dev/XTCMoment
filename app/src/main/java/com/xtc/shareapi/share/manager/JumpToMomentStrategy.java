package com.xtc.shareapi.share.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.xtc.shareapi.R;
import com.xtc.shareapi.share.bean.JumpToMomentRequest;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IJumpToMomentObject;
import com.xtc.shareapi.share.jumpmoment.JumpToMomentBase;
import com.xtc.shareapi.share.jumpmoment.JumpToMomentFromAddress;
import com.xtc.shareapi.share.jumpmoment.JumpToMomentFromAlbum;
import com.xtc.shareapi.share.jumpmoment.JumpToMomentFromCamara;
import com.xtc.shareapi.share.utils.ShareUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.system.WatchModelUtil;

/**
 * 跳转好友圈发动态的策略实现，负责开关校验与 Intent 组装。
 */
public class JumpToMomentStrategy {

    private static final String TAG = "JumpToMomentStrategy";

    private final Context context;

    public JumpToMomentStrategy(Context context) {
        this.context = context;
    }

    public void share(JumpToMomentRequest request) {
        if (!isMomentChecked()) {
            final Context shareContext = context;
            ShareHandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(shareContext, shareContext.getString(R.string.please_install_moment), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (ShareUtil.getHostSdkVersion(context, OpenApiConstant.App.MOMENT_PACKAGE_NAME)
                < OpenApiConstant.SdkVersionCode.MOMENT_VERAION_CODE_SUPPORT_JUMP) {
            Log.d(TAG, "check moment meta fail,moment is not new version");
            Toast.makeText(context, context.getString(R.string.please_update_moment), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isModuleSwitchOpen()) {
            Log.d(TAG, "current moduleSwitch is not open !");
            return;
        }
        if (!isFunSwitchOpen()) {
            Log.d(TAG, "current funSwitch is not open !");
            Toast.makeText(context, context.getString(R.string.moment_forbid), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = getShareIntent(request);
        if (shareIntent == null) {
            Log.d(TAG, "get share intent error!");
            return;
        }
        context.startActivity(shareIntent);
    }

    private boolean isMomentChecked() {
        return ShareUtil.isInstallScene(context, OpenApiConstant.App.MOMENT_PACKAGE_NAME);
    }

    private boolean isModuleSwitchOpen() {
        return WatchAccountBase.queryModuleSwitchByBoolean(context,
                OpenApiConstant.ModuleSwitch.MODULE_SWITCH_MOMENT, !WatchModelUtil.isOverseas());
    }

    private boolean isFunSwitchOpen() {
        return WatchAccountBase.queryFunSwitchByPackageName(context, OpenApiConstant.App.MOMENT_PACKAGE_NAME, true)
                .getSwitchStatus().intValue() == OpenApiConstant.FunSwitchStatue.FUN_SWITCH_OPEN;
    }

    private Intent getShareIntent(JumpToMomentRequest request) {
        Intent intent = new Intent();
        IJumpToMomentObject jumpToMomentObject = request.getJumpToMomentObject();
        if (jumpToMomentObject instanceof JumpToMomentFromCamara) {
            dealIntentFromCamara(intent, (JumpToMomentFromCamara) jumpToMomentObject);
        } else if (jumpToMomentObject instanceof JumpToMomentFromAlbum) {
            dealIntentFromAlbum(intent, (JumpToMomentFromAlbum) jumpToMomentObject);
        } else if (jumpToMomentObject instanceof JumpToMomentBase) {
            dealIntentFromBase(intent, (JumpToMomentBase) jumpToMomentObject);
        } else if (jumpToMomentObject instanceof JumpToMomentFromAddress) {
            dealIntentFromAddress(intent, (JumpToMomentFromAddress) jumpToMomentObject);
        }
        intent.putExtra(OpenApiConstant.MomentIntentConstant.IS_FROM_OUTSIDE_JUMP, true);
        intent.setAction(OpenApiConstant.App.MOMENT_MATA_LBS_ACTIVITY);
        return intent;
    }

    private void dealIntentFromCamara(Intent intent, JumpToMomentFromCamara fromCamara) {
        intent.putExtra(OpenApiConstant.MomentIntentConstant.PiCURE_PHOTO_PATH, fromCamara.getCamaraBundle());
        intent.putExtra(OpenApiConstant.MomentIntentConstant.SHARE_FROM_EXTRA, fromCamara.type());
        if (fromCamara.getJumpToMomentBase() != null) {
            dealIntentFromBase(intent, fromCamara.getJumpToMomentBase());
        }
    }

    private void dealIntentFromAlbum(Intent intent, JumpToMomentFromAlbum fromAlbum) {
        Bundle albumBundle = fromAlbum.getAlbumBundle();
        int photoType = albumBundle.getInt(OpenApiConstant.BundleExtra.PHOTO_TYPE);
        intent.putExtra(OpenApiConstant.MomentIntentConstant.SHARE_FROM_EXTRA, fromAlbum.type());
        if (photoType == OpenApiConstant.BundleExtra.VIDEO) {
            intent.putExtra(OpenApiConstant.MomentIntentConstant.VIDEO_PATH, albumBundle);
        } else if (photoType == OpenApiConstant.BundleExtra.PHOTO) {
            intent.putExtra(OpenApiConstant.MomentIntentConstant.PHOTO_PATH, albumBundle);
        }
        intent.putExtra(OpenApiConstant.MomentIntentConstant.IS_FROM_ALBUM, fromAlbum.isFromAlbum());
        if (fromAlbum.getJumpToMomentBase() != null) {
            dealIntentFromBase(intent, fromAlbum.getJumpToMomentBase());
        }
    }

    private void dealIntentFromBase(Intent intent, JumpToMomentBase fromBase) {
        intent.putExtra(OpenApiConstant.MomentIntentConstant.SHARE_FROM_EXTRA, fromBase.type());
        intent.putExtra(OpenApiConstant.MomentIntentConstant.LBS_ADDRESS_POI_BEAN, JSON.toJSONString(fromBase.getPoiBean()));
        intent.putExtra(OpenApiConstant.MomentIntentConstant.LBS_TEXT, fromBase.getText());
    }

    private void dealIntentFromAddress(Intent intent, JumpToMomentFromAddress fromAddress) {
        intent.putExtra(OpenApiConstant.MomentIntentConstant.SHARE_FROM_EXTRA, fromAddress.type());
        intent.putExtra(OpenApiConstant.MomentIntentConstant.LBS_FROM_LAUNCHER_ADDRESS_ID, fromAddress.getAddressId());
        if (fromAddress.getBase() != null) {
            dealIntentFromBase(intent, fromAddress.getBase());
        }
    }
}