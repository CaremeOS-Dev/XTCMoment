package com.xtc.moment.share.callback;

import android.content.Intent;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.shareapi.share.shareobject.XTCShareMessage;

/**
 * 分享到好友圈页面视图接口。
 */
public interface IShareToMomentView extends MvpView {
    void showPreviewDialog(XTCShareMessage shareMessage);

    void shareSuccess();

    void finishSelf();

    void sendResponse(Intent intent);
}