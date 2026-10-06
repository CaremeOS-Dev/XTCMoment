package com.xtc.moment.share.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.share.callback.ICheckBundleCallback;
import com.xtc.moment.share.other.ShareUtils;

/**
 * 分享 Presenter 基类，统一负责参数校验与按类型分发分享。
 */
public abstract class AbstractSharePresenter<V extends MvpView> extends MvpBasePresenter<V> {

    protected static final String TAG = ShareUtils.LOG + AbstractSharePresenter.class.getSimpleName();

    ShareBundleManager bundleManager;

    AbstractSharePresenter(Context context) {
        this.bundleManager = new ShareBundleManager(context, createBundleCallback());
    }

    protected abstract ICheckBundleCallback createBundleCallback();

    protected abstract void shareTextMessage();

    protected abstract void shareImageMessage();

    protected abstract void shareAppMessage();

    protected abstract void shareVideoMessage();

    protected abstract void shareWebMessage();

    protected abstract void shareLivePhotoMessage();

    protected abstract void shareMultiImageMessage();

    /** 按分享消息类型分发到具体的分享实现。 */
    public void startSend() {
        DigitalManager.getInstance().clearDigitalEntity();
        DigitalManager.getInstance().getDigitalEntity().startPushTime = SystemClock.elapsedRealtime();
        int type = this.bundleManager.xtcShareMessage.getType();
        if (type == 1) {
            shareTextMessage();
            return;
        }
        if (type == 2) {
            shareImageMessage();
            return;
        }
        if (type == 3) {
            shareAppMessage();
            return;
        }
        if (type == 4) {
            shareVideoMessage();
            return;
        }
        if (type == 6) {
            shareWebMessage();
            return;
        }
        if (type == 7) {
            shareLivePhotoMessage();
        } else if (type == 9) {
            shareMultiImageMessage();
        } else {
            this.bundleManager.sendNotSupportResponse();
        }
    }

    public void checkBundle(Bundle bundle) {
        this.bundleManager.checkBundle(bundle);
    }

    public void onlyParseClassName(Bundle bundle) {
        this.bundleManager.onlyParseClassName(bundle);
    }

    public void sendSuccessResponse() {
        this.bundleManager.sendSuccessResponse();
    }

    public void sendNotSupportResponse() {
        this.bundleManager.sendNotSupportResponse();
    }

    public void sendCancelResponse() {
        this.bundleManager.sendCancelResponse();
    }

    public void sendFailResponse() {
        this.bundleManager.sendFailResponse();
    }

    public void sendOtherResponse(Throwable throwable) {
        this.bundleManager.sendOtherResponse(throwable);
    }
}