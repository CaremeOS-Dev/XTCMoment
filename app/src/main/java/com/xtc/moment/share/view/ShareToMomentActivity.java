package com.xtc.moment.share.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.event.ShareEventEntity;
import com.xtc.moment.share.callback.IShareCallback;
import com.xtc.moment.share.callback.IShareToMomentView;
import com.xtc.moment.share.presenter.ShareToMomentPresenter;
import com.xtc.moment.share.view.playvideo.PlayShareVideoPresenter;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.Utils;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.manager.ShareSupportManager;
import com.xtc.shareapi.share.shareobject.XTCImageObject;
import com.xtc.shareapi.share.shareobject.XTCLivePhotoObject;
import com.xtc.shareapi.share.shareobject.XTCMultiImageObject;
import com.xtc.shareapi.share.shareobject.XTCShareMessage;
import com.xtc.shareapi.share.shareobject.XTCTextObject;
import com.xtc.shareapi.share.shareobject.XTCVideoObject;
import com.xtc.ui.widget.privacy.IPrivacyDialogListener;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 接收外部分享并发布到好友圈的 Activity。
 */
public class ShareToMomentActivity extends AbstractShareActivity<IShareToMomentView, ShareToMomentPresenter>
        implements IShareToMomentView {

    private static final String TAG = "Share_Msg_AbstractShareActivity";

    private Dialog ctaPermissionDialog;
    private boolean hintPermission;
    private PlayShareVideoPresenter mPlayShareVideoPresenter;
    private ProgressBar progressBar;
    private long sendTime;
    private long startSend;

    @Override
    public ShareToMomentPresenter createPresenter() {
        return new ShareToMomentPresenter(this);
    }

    @Override
    public void initView() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_moment);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        dealPermission();
    }

    private void requestPermission() {
        requestRunTimePermission(PermissionStringUtils.SEND_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                PermissionStringUtils.checkPermissionForReTryBaseUrl(ShareToMomentActivity.this);
                LogUtil.d(TAG, "onGranted: requestPermission");
                ShareToMomentActivity.this.initData();
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: requestPermission");
                presenter.onlyParseClassName(getIntent().getExtras());
                presenter.sendCancelResponse();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.unbindService(this);
        Dialog dialog = this.ctaPermissionDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtil.d(TAG, "onNewIntent: ");
        dealPermission();
    }

    private void dealPermission() {
        this.hintPermission = SharedTool.getIsHintPermission(this);
        if (this.hintPermission) {
            requestPermission();
            LogUtil.d(TAG, "dealPermission: hintPermission is true");
        } else {
            this.ctaPermissionDialog = DialogUtil.makePrivacyDialog(this, new IPrivacyDialogListener() {
                @Override
                public void allow() {
                    Utils.initWatchConfigManager(ShareToMomentActivity.this);
                    SharedTool.saveHintPermission(ShareToMomentActivity.this, true);
                    ShareToMomentActivity.this.requestPermission();
                }

                @Override
                public void refuse() {
                    LogUtil.d(TAG, "refuse");
                    presenter.onlyParseClassName(getIntent().getExtras());
                    presenter.sendCancelResponse();
                }
            });
            this.ctaPermissionDialog.show();
        }
    }

    @Override
    public void initData() {
        super.initData();
        this.presenter.bindService(this);
        initShareSupport();
    }

    private void initShareSupport() {
        Observable.just(false)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean input) {
                        return Boolean.valueOf(ShareSupportManager.getInstance(ShareToMomentActivity.this).init());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "initShareSupport is error", throwable);
                    }

                    @Override
                    public void onNext(Boolean initialized) {
                        LogUtil.i(TAG, "initShareSupport onNext: init = " + initialized);
                        presenter.checkBundle(getIntent().getExtras());
                    }
                });
    }

    @Override
    protected IShareCallback createShareCallback() {
        return new IShareCallback() {
            @Override
            public void sendShare() {
                startSend = System.currentTimeMillis();
                presenter.dealSend();
            }

            @Override
            public void cancelShare() {
                presenter.sendCancelResponse();
            }

            @Override
            public void sendSuccessResult() {
                sendTime = System.currentTimeMillis() - startSend;
                ShareEventEntity shareEventEntity = new ShareEventEntity();
                shareEventEntity.time = String.valueOf(sendTime);
                if (getIntent().getExtras() != null) {
                    shareEventEntity.appname = getIntent().getExtras()
                            .getString(OpenApiConstant.IntentConstant.INTENT_APP_NAME);
                } else {
                    shareEventEntity.appname = "";
                }
                shareEventEntity.isSuccess = "success";
                BehaviorUtil.customEvent(ShareToMomentActivity.this, ShareEventEntity.funtionName,
                        shareEventEntity.getHashMap());
                presenter.sendSuccessResponse();
            }

            @Override
            public void sendOtherResponse(Throwable throwable) {
                presenter.sendOtherResponse(throwable);
            }
        };
    }
    @Override
    public void showPreviewDialog(XTCShareMessage shareMessage) {
        LogUtil.d(TAG, "showPreviewDialog() called with: Type = [" + shareMessage.getType() + "]");
        this.progressBar.setVisibility(View.GONE);
        if (shareMessage.getType() == 1) {
            this.shareDialogManager.showTextShareDialog(getString(R.string.send_to_moment),
                    ((XTCTextObject) shareMessage.getShareObject()).getText());
            return;
        }
        if (shareMessage.getType() == 2) {
            XTCImageObject imageObject = (XTCImageObject) shareMessage.getShareObject();
            this.presenter.preCompressImage(imageObject);
            this.shareDialogManager.showImageShareDialog(getString(R.string.send_to_moment),
                    imageObject.getImageData(), imageObject.getImagePath(), imageObject.getDialogBitmapArgs(),
                    imageObject.getMessageBitmapArgs());
            return;
        }
        if (shareMessage.getType() == 9) {
            ArrayList<XTCImageObject> imagePathList = ((XTCMultiImageObject) shareMessage.getShareObject())
                    .getImagePathList();
            if (CollectionUtil.isEmpty(imagePathList)) {
                this.presenter.sendOtherResponse(new Throwable("xtcImageObjectList is empty"));
                return;
            }
            ArrayList<String> paths = new ArrayList<>();
            for (int i = 0; i < imagePathList.size(); i++) {
                XTCImageObject imageObject = imagePathList.get(i);
                if (imageObject != null && !TextUtils.isEmpty(imageObject.getImagePath())) {
                    paths.add(imageObject.getImagePath());
                }
            }
            this.shareDialogManager.showMultiImageShareDialog(getString(R.string.send_to_moment), paths);
            return;
        }
        if (shareMessage.getType() == 4) {
            XTCVideoObject videoObject = (XTCVideoObject) shareMessage.getShareObject();
            String thumbnailPath = videoObject.getThumbnailPath();
            String videoPath = videoObject.getVideoPath();
            final String videoName = ChatVideoUtil.getVideoName(videoPath);
            if (TextUtils.isEmpty(thumbnailPath)) {
                String cachedThumbnail = FileManager.getVideoThumnailDir() + videoName + FileManager.WEBP_FORMAT;
                if (FileUtils.isFileExists(cachedThumbnail)) {
                    thumbnailPath = cachedThumbnail;
                }
            }
            if (TextUtils.isEmpty(thumbnailPath)) {
                String thirdThumbnail = FileManager.getThirdVideoThumnailPath() + videoName + FileManager.WEBP_FORMAT;
                if (FileUtils.isFileExists(thirdThumbnail)) {
                    thumbnailPath = thirdThumbnail;
                }
            }
            LogUtil.d(TAG, "thumbnail path = " + thumbnailPath);
            if (TextUtils.isEmpty(videoPath)) {
                videoPath = videoObject.getVideoDownloadUrl();
            } else {
                final String preSaveVideoPath = videoPath;
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        presenter.preSaveThumbnailAndToken(preSaveVideoPath, videoName);
                    }
                });
            }
            this.mPlayShareVideoPresenter = this.shareDialogManager.showVideoShareDialog(
                    getString(R.string.send_to_moment), thumbnailPath, videoObject.getDuration(), videoPath);
            return;
        }
        if (shareMessage.getType() == 7) {
            this.shareDialogManager.showImageShareDialog(getString(R.string.send_to_moment), null,
                    ((XTCLivePhotoObject) shareMessage.getShareObject()).localPhotoPath, null, null);
            return;
        }
        if (shareMessage.getType() == 6) {
            byte[] thumbData = shareMessage.getThumbData();
            if (thumbData == null || thumbData.length == 0) {
                this.presenter.sendFailResponse();
                return;
            }
            this.shareDialogManager.showWebShareDialog(shareMessage.getDescription(), thumbData);
            return;
        }
        if (shareMessage.getType() == 3) {
            byte[] thumbData = shareMessage.getThumbData();
            if (thumbData == null || thumbData.length == 0) {
                this.presenter.sendFailResponse();
                return;
            }
            this.shareDialogManager.showAppShareDial(shareMessage.getDescription(), shareMessage.getThumbData());
            return;
        }
        this.presenter.sendNotSupportResponse();
    }

    @Override
    public void sendResponse(Intent intent) {
        if (intent != null && SystemUtil.isActivityOnTop(this, ShareToMomentActivity.class)) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                LogUtil.e(TAG, "start target activity error ", e);
            }
        }
        if (this.shareDialogManager != null) {
            this.shareDialogManager.cancelDialog();
        }
        finishSelf();
    }

    @Override
    public void shareSuccess() {
        this.shareDialogManager.shareSuccess();
    }

    @Override
    public void finishSelf() {
        if (isFinishing()) {
            return;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause() called");
        PlayShareVideoPresenter playShareVideoPresenter = this.mPlayShareVideoPresenter;
        if (playShareVideoPresenter != null) {
            playShareVideoPresenter.dealScreenOff();
            this.mPlayShareVideoPresenter.pausePlayVideo();
        }
    }
}
