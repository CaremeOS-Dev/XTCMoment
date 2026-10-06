package com.xtc.moment.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.log.LogUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.Utils;
import com.xtc.ui.widget.privacy.IPrivacyDialogListener;
import com.xtc.ui.widget.util.DialogUtil;

/**
 * 带 CTA 隐私协议弹窗的 Activity 基类。
 */
public abstract class BaseCtaPermissionActivity<V extends MvpView, P extends MvpPresenter<V>> extends BaseActivity<V, P> {

    private static final String TAG = "BaseCtaActivity";

    protected Dialog ctaPermissionDialog;
    protected boolean hintPermission;
    private final Context that = this;

    public abstract void afterDealPermission();

    public abstract void beforeDealPermission();

    public abstract void refusePermission();

    public abstract void requestPermission();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeDealPermission();
        dealPermission();
    }

    protected void dealPermission() {
        this.hintPermission = SharedTool.getIsHintPermission(this);
        if (this.hintPermission) {
            requestPermission();
            LogUtil.d(TAG, "dealPermission: hintPermission is true");
            return;
        }
        this.ctaPermissionDialog = DialogUtil.makePrivacyDialog(this, new IPrivacyDialogListener() {
            @Override
            public void allow() {
                Utils.initWatchConfigManager(BaseCtaPermissionActivity.this.that);
                SharedTool.saveHintPermission(BaseCtaPermissionActivity.this.that, true);
                BaseCtaPermissionActivity.this.requestPermission();
            }

            @Override
            public void refuse() {
                BaseCtaPermissionActivity.this.refusePermission();
            }
        });
        afterDealPermission();
        this.ctaPermissionDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dialog dialog = this.ctaPermissionDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}