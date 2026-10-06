package com.xtc.moment.share.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.behavior.DigitalBigDateSender;
import com.xtc.moment.behavior.DigitalConstant;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.StringConstant;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.SendVideoParam;
import com.xtc.moment.module.bean.ShareAppMoment;
import com.xtc.moment.module.bean.ShareAppPublish;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.bean.ShareImagePublish;
import com.xtc.moment.module.bean.ShareLivePhotoMoment;
import com.xtc.moment.module.bean.ShareTextPublish;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.module.bean.ShareWebPublish;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.illegal.config.IllegalConfigHandler;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.widget.HintIllegalContentDialog;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.service.PublishService;
import com.xtc.moment.share.callback.ICheckBundleCallback;
import com.xtc.moment.share.callback.IShareToMomentView;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.share.other.ShareUtils;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ImageUtil;
import com.xtc.moment.util.ScreenUtils;
import com.xtc.shareapi.share.bean.SerializableMap;
import com.xtc.shareapi.share.shareobject.XTCAppExtendObject;
import com.xtc.shareapi.share.shareobject.XTCImageObject;
import com.xtc.shareapi.share.shareobject.XTCLivePhotoObject;
import com.xtc.shareapi.share.shareobject.XTCMultiImageObject;
import com.xtc.shareapi.share.shareobject.XTCTextObject;
import com.xtc.shareapi.share.shareobject.XTCVideoObject;
import com.xtc.shareapi.share.shareobject.XTCWebObject;
import com.xtc.system.wearswitch.function.FunSwitchUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 分享到好友圈的 Presenter：负责校验参数、上传素材并发布动态。
 */
public class ShareToMomentPresenter extends AbstractSharePresenter<IShareToMomentView> {

    private BehaviorEvent behaviorEvent;
    private boolean bindResult;
    private IMomentServe iMomentServe;
    private volatile boolean isNeedSendShareImage;
    private volatile boolean isPreCompressImaging;
    private volatile String loadingTokenVideoName;
    private Context mContext;
    private volatile SendVideoParam mSendVideoParam;
    private volatile String mShareImagePath;
    private VideoTokenVoResponse mVideoTokenVoResponse;
    private PublishService.PublishBinder publishService;
    private ServiceConnection serviceConnection;
    private ShareAppMoment shareAppMoment;
    private ShareImageMoment shareImageMoment;
    private ShareLivePhotoMoment shareLivePhotoMoment;
    private ShareWebMoment shareWebMoment;

    public ShareToMomentPresenter(Context context) {
        super(context);
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.behaviorEvent = new BehaviorEvent(context);
    }

    /** 绑定发布服务。 */
    public void bindService(Context context) {
        Intent intent = new Intent(context, PublishService.class);
        intent.setAction(PublishService.ACTION_BIND);
        this.serviceConnection = createServiceConnection();
        this.bindResult = context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        ServiceConnection connection = this.serviceConnection;
        if (connection == null || !this.bindResult) {
            return;
        }
        this.bindResult = false;
        context.unbindService(connection);
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                publishService = (PublishService.PublishBinder) binder;
                publishService.getService().setCallback(createPhotoUploadCallback());
                LogUtil.d(AbstractSharePresenter.TAG, "publish service connected!");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                LogUtil.d(AbstractSharePresenter.TAG, "publish service disconnected!");
            }
        };
    }

    private PublishService.Callback createPhotoUploadCallback() {
        return new PublishService.Callback() {
            @Override
            public void onUploadPhotoSuccess(PhotoMsg photoMsg, VideoTokenVoResponse videoTokenVoResponse) {
                LogUtil.i(AbstractSharePresenter.TAG, "onUploadPhotoSuccess photoMsg " + photoMsg);
                if ((photoMsg instanceof ShareImageMoment) && photoMsg.getDialogType() != 28) {
                    shareImageMoment = (ShareImageMoment) photoMsg;
                    LogUtil.d(AbstractSharePresenter.TAG, "upload share image success = " + shareImageMoment);
                    publishShareImageMoment(convertToShareImagePublish(shareImageMoment), shareImageMoment.getSource());
                } else if (photoMsg instanceof ShareAppMoment) {
                    shareAppMoment = (ShareAppMoment) photoMsg;
                    LogUtil.d(AbstractSharePresenter.TAG, "upload share app success = " + shareAppMoment);
                    publishShareAppMoment(convertToShareAppPublish(shareAppMoment), shareAppMoment.getSource());
                } else if (photoMsg instanceof ShareWebMoment) {
                    shareWebMoment = (ShareWebMoment) photoMsg;
                    LogUtil.d(AbstractSharePresenter.TAG, "upload share app success = " + shareWebMoment);
                    publishShareWebMoment(convertToShareWebPublish(shareWebMoment), shareWebMoment.getSource());
                }
                if (photoMsg != null && photoMsg.getDialogType() == 28) {
                    shareImageMoment = (ShareImageMoment) photoMsg;
                    String content = JSONUtil.toJSON(convertToShareImagePublish(shareImageMoment));
                    LogUtil.i(AbstractSharePresenter.TAG,
                            "photoMsg " + photoMsg.getDialogType() + "shareImageMoment content;" + content);
                    CloudFileResource source = photoMsg.getSource();
                    LogUtil.i(AbstractSharePresenter.TAG, "多图分享 " + source.getKey());
                    publishMoment(28, source.getKey(), null, photoMsg.getPoiBean());
                }
                if (videoTokenVoResponse == null || videoTokenVoResponse.getDialogType() != 24) {
                    return;
                }
                String videoFlags = videoTokenVoResponse.getCustomParamMap().get(ShareVideoMoment.SHARE_VIDEO_FLAGS);
                ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(videoFlags, ShareVideoMoment.class);
                LogUtil.d(AbstractSharePresenter.TAG, "upload share video success = " + shareVideoMoment.getSource());
                VideoKeyOrToken videoKeyOrToken = new VideoKeyOrToken();
                videoKeyOrToken.setPicKey(videoTokenVoResponse.getIcon().getKey());
                videoKeyOrToken.setVideoKey(videoTokenVoResponse.getSource().getKey());
                videoTokenVoResponse.setCustomParamMap(null);
                videoTokenVoResponse.setUploadToken("");
                videoTokenVoResponse.setIconUploadToken("");
                publishMoment(24, JSONUtil.toJSON(videoKeyOrToken), videoFlags, videoTokenVoResponse.getPoiBean());
            }

            @Override
            public void onUploadLivePhotoSuccess(LivePhotoMsg livePhotoMsg) {
                if (livePhotoMsg instanceof ShareLivePhotoMoment) {
                    shareLivePhotoMoment = (ShareLivePhotoMoment) livePhotoMsg;
                    LogUtil.d(AbstractSharePresenter.TAG, "upload share livePhoto success = " + shareLivePhotoMoment);
                    String content = JSONUtil.toJSON(shareLivePhotoMoment);
                    publishShareLivePhotoMoment(content, shareLivePhotoMoment.getSource(), livePhotoMsg.getPoiBean());
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                LogUtil.e(AbstractSharePresenter.TAG, "upload image fail: " + throwable);
                sendFailResponse();
            }
        };
    }
    private ShareWebPublish convertToShareWebPublish(ShareWebMoment shareWebMoment) {
        ShareWebPublish publish = new ShareWebPublish();
        publish.setAppIcon(shareWebMoment.getAppIcon());
        publish.setAppName(shareWebMoment.getAppName());
        publish.setDesc(shareWebMoment.getDesc());
        publish.setTransaction(shareWebMoment.getTransaction());
        publish.setPackageName(shareWebMoment.getPackageName());
        publish.setWebLink(shareWebMoment.getWebLink());
        publish.setVideoSource(shareWebMoment.getVideoSource());
        publish.setVideoThumbSource(shareWebMoment.getVideoThumbSource());
        return publish;
    }

    private ShareAppPublish convertToShareAppPublish(ShareAppMoment shareAppMoment) {
        ShareAppPublish publish = new ShareAppPublish();
        if (shareAppMoment != null) {
            publish.setAppIcon(shareAppMoment.getAppIcon());
            publish.setAction(shareAppMoment.getAction());
            publish.setAppName(shareAppMoment.getAppName());
            publish.setDesc(shareAppMoment.getDesc());
            publish.setExtInfo(shareAppMoment.getExtInfo());
            publish.setTargetClass(shareAppMoment.getTargetClass());
            publish.setTargetPackage(shareAppMoment.getTargetPackage());
            publish.setTransaction(shareAppMoment.getTransaction());
            publish.setPackageName(shareAppMoment.getPackageName());
        }
        return publish;
    }

    private ShareImagePublish convertToShareImagePublish(ShareImageMoment shareImageMoment) {
        ShareImagePublish publish = new ShareImagePublish();
        publish.setAppIcon(shareImageMoment.getAppIcon());
        publish.setTransaction(shareImageMoment.getTransaction());
        publish.setDesc(shareImageMoment.getDesc());
        publish.setAppName(shareImageMoment.getAppName());
        publish.setPackageName(shareImageMoment.getPackageName());
        publish.setTextMsg(shareImageMoment.getTextMsg());
        publish.setMessageBitmapArgs(shareImageMoment.getMessageBitmapArgs());
        publish.setDialogBitmapArgs(shareImageMoment.getDialogBitmapArgs());
        publish.setTrackMd5Value(shareImageMoment.getTrackMd5Value());
        return publish;
    }

    @Override
    protected ICheckBundleCallback createBundleCallback() {
        return new ICheckBundleCallback() {
            @Override
            public void checkSuccess() {
                LogUtil.d(AbstractSharePresenter.TAG, "check bundle success!");
                Observable.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return Boolean.valueOf(FunSwitchUtil.queryFunSwitchByBoolean(mContext, mContext.getPackageName(), true));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                bundleManager.sendOtherResponse(throwable);
                            }

                            @Override
                            public void onNext(Boolean result) {
                                showFunResult(result);
                            }
                        });
            }

            @Override
            public void sendResponse(Intent intent) {
                if (getView() != null) {
                    ((IShareToMomentView) getView()).sendResponse(intent);
                }
            }
        };
    }

    private void showFunResult(Boolean result) {
        if (getView() == null) {
            return;
        }
        if (result.booleanValue()) {
            ((IShareToMomentView) getView()).showPreviewDialog(this.bundleManager.xtcShareMessage);
        } else {
            this.bundleManager.sendSceneForbidResponse();
        }
    }

    @Override
    protected void shareTextMessage() {
        ShareTextPublish shareTextPublish = new ShareTextPublish();
        XTCTextObject textObject = (XTCTextObject) this.bundleManager.xtcShareMessage.getShareObject();
        shareTextPublish.setAppIcon(this.bundleManager.icon);
        shareTextPublish.setAppName(this.bundleManager.name);
        shareTextPublish.setTransaction(this.bundleManager.transaction);
        shareTextPublish.setPackageName(this.bundleManager.packageName);
        if (TextUtils.isEmpty(textObject.getText())) {
            shareTextPublish.setContent(this.bundleManager.xtcShareMessage.getDescription());
        } else {
            shareTextPublish.setContent(textObject.getText());
        }
        publishMoment(7, JSONUtil.toJSON(shareTextPublish));
    }

    @Override
    protected void shareAppMessage() {
        Observable.just(this.bundleManager.xtcShareMessage.getThumbData())
                .map(new Func1<byte[], String>() {
                    @Override
                    public String call(byte[] thumbData) {
                        long startTime = SystemClock.elapsedRealtime();
                        String savedPath = ShareUtils.saveBitmapToSdcard(thumbData);
                        DigitalManager.getInstance().getDigitalEntity().shareTransTime =
                                String.valueOf(SystemClock.elapsedRealtime() - startTime);
                        return savedPath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String savedPath) {
                        uploadShareAppSource(savedPath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        bundleManager.sendOtherResponse(throwable);
                    }
                });
    }

    @Override
    protected void shareImageMessage() {
        if (TextUtils.isEmpty(this.mShareImagePath)) {
            LogUtil.d(TAG, "shareImageMessage: wait preCompressImage");
            this.isNeedSendShareImage = true;
        } else {
            uploadShareImageSource(this.mShareImagePath);
        }
    }

    /** 预压缩待分享图片。 */
    public void preCompressImage(XTCImageObject imageObject) {
        if (this.isPreCompressImaging) {
            LogUtil.d(TAG, "preCompressImage： isPreCompressImaging");
            return;
        }
        this.isPreCompressImaging = true;
        this.mShareImagePath = "";
        LogUtil.d(TAG, "preCompressImage");
        Observable.just(imageObject)
                .map(new Func1<XTCImageObject, String>() {
                    @Override
                    public String call(XTCImageObject targetImage) {
                        String imagePath = getShareImagePath(targetImage);
                        LogUtil.d(AbstractSharePresenter.TAG, "bundleManager.xtcShareMessage.getDescription()="
                                + bundleManager.xtcShareMessage.getDescription());
                        if (TextUtils.isEmpty(targetImage.getImagePath())
                                && !TextUtils.isEmpty(bundleManager.xtcShareMessage.getDescription())) {
                            ScreenshotUtils.buildCarryData(imagePath, bundleManager.xtcShareMessage.getDescription());
                        }
                        try {
                            ImageUtil.compressByScale(imagePath, PhotoTokenParam.WEBP_FORMAT,
                                    Constants.Camera.PhotoSize.WIDTH, Constants.Camera.PhotoSize.HEIGHT);
                        } catch (Throwable throwable) {
                            LogUtil.e(AbstractSharePresenter.TAG, "compress share image error", throwable);
                        }
                        return imagePath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String imagePath) {
                        mShareImagePath = imagePath;
                        isPreCompressImaging = false;
                        LogUtil.i(AbstractSharePresenter.TAG, "preCompressImage success");
                        if (isNeedSendShareImage) {
                            isNeedSendShareImage = false;
                            uploadShareImageSource(imagePath);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(AbstractSharePresenter.TAG, "preCompressImage error: ", throwable);
                        isPreCompressImaging = false;
                        if (isNeedSendShareImage) {
                            isNeedSendShareImage = false;
                            DigitalBigDateSender.onPublicResult(mContext, DigitalManager.getInstance().getDigitalEntity(),
                                    false, DigitalConstant.ErrorCode.BEFORE_TOKEN_ERROR, throwable);
                            bundleManager.sendOtherResponse(throwable);
                        }
                    }
                });
    }

    @Override
    protected void shareMultiImageMessage() {
        XTCMultiImageObject multiImageObject = (XTCMultiImageObject) this.bundleManager.xtcShareMessage.getShareObject();
        if (multiImageObject == null) {
            sendOtherResponse(new Throwable("xtcMultiImageObject is null!!!"));
            return;
        }
        ArrayList<XTCImageObject> imagePathList = multiImageObject.getImagePathList();
        if (CollectionUtil.isEmpty(imagePathList)) {
            sendOtherResponse(new Throwable("xtcImageObjectList is empty!!!"));
            return;
        }
        ArrayList<PhotoMsg> photos = new ArrayList<>();
        for (int i = 0; i < imagePathList.size(); i++) {
            ShareImageMoment shareImageMoment = new ShareImageMoment();
            shareImageMoment.setDialogType(28);
            shareImageMoment.setLocalPath(imagePathList.get(i).getImagePath());
            shareImageMoment.setPhotoHasDownload(true);
            shareImageMoment.setDesc(this.bundleManager.xtcShareMessage.getDescription());
            shareImageMoment.setAppIcon(this.bundleManager.icon);
            shareImageMoment.setAppName(this.bundleManager.name);
            shareImageMoment.setTransaction(this.bundleManager.transaction);
            shareImageMoment.setPackageName(this.bundleManager.packageName);
            photos.add(shareImageMoment);
        }
        this.publishService.senPhotosMsg(photos, true);
    }

    @Override
    protected void shareLivePhotoMessage() {
        XTCLivePhotoObject livePhotoObject = (XTCLivePhotoObject) this.bundleManager.xtcShareMessage.getShareObject();
        String localPhotoPath = livePhotoObject.localPhotoPath;
        String localVideoPath = livePhotoObject.localVideoPath;
        if (TextUtils.isEmpty(localPhotoPath) || TextUtils.isEmpty(localVideoPath)) {
            sendOtherResponse(new Throwable("save to SdCard fail"));
            return;
        }
        ShareLivePhotoMoment shareLivePhotoMoment = new ShareLivePhotoMoment();
        shareLivePhotoMoment.setAppIcon(this.bundleManager.icon);
        shareLivePhotoMoment.setAppName(this.bundleManager.name);
        shareLivePhotoMoment.setTransaction(this.bundleManager.transaction);
        shareLivePhotoMoment.setPackageName(this.bundleManager.packageName);
        shareLivePhotoMoment.setDesc(this.bundleManager.xtcShareMessage.getDescription());
        shareLivePhotoMoment.setDialogType(5);
        shareLivePhotoMoment.setLocalPath(localPhotoPath);
        shareLivePhotoMoment.setPhotoHasDownload(true);
        shareLivePhotoMoment.setVideoMsg(BeanConverterUtil.toVideoMsg(localPhotoPath, localVideoPath));
        this.publishService.sendLivePhotoMsg(shareLivePhotoMoment);
    }

    @Override
    protected void shareWebMessage() {
        Observable.just(this.bundleManager.xtcShareMessage.getThumbData())
                .map(new Func1<byte[], String>() {
                    @Override
                    public String call(byte[] thumbData) {
                        long startTime = SystemClock.elapsedRealtime();
                        String savedPath = ShareUtils.saveBitmapToSdcard(thumbData);
                        DigitalManager.getInstance().getDigitalEntity().shareTransTime =
                                String.valueOf(SystemClock.elapsedRealtime() - startTime);
                        return savedPath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String savedPath) {
                        uploadShareWebSource(savedPath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DigitalBigDateSender.onPublicResult(mContext, DigitalManager.getInstance().getDigitalEntity(),
                                false, DigitalConstant.ErrorCode.BEFORE_TOKEN_ERROR, throwable);
                        bundleManager.sendOtherResponse(throwable);
                    }
                });
    }

    @Override
    protected void shareVideoMessage() {
        XTCVideoObject videoObject = (XTCVideoObject) this.bundleManager.xtcShareMessage.getShareObject();
        videoObject.getThumbnailPath();
        String videoPath = videoObject.getVideoPath();
        LogUtil.d(TAG, "shareVideoMessage: " + videoObject);
        String thumbnailPath = FileManager.getVideoThumnailDir() + ChatVideoUtil.getVideoName(videoPath)
                + FileManager.WEBP_FORMAT;
        if (TextUtils.isEmpty(thumbnailPath) || TextUtils.isEmpty(videoPath)) {
            sendOtherResponse(new Throwable("save to SdCard fail"));
            return;
        }
        if (TextUtils.isEmpty(thumbnailPath)) {
            this.bundleManager.sendOtherResponse(new Throwable("save icon to sdcard error!"));
            return;
        }
        ShareVideoMoment shareVideoMoment = new ShareVideoMoment();
        shareVideoMoment.setLocalThumbnailPath(thumbnailPath);
        shareVideoMoment.setLocalVideoPath(videoPath);
        shareVideoMoment.setDesc(this.bundleManager.xtcShareMessage.getDescription());
        shareVideoMoment.setAppIcon(this.bundleManager.icon);
        shareVideoMoment.setAppName(this.bundleManager.name);
        shareVideoMoment.setTransaction(this.bundleManager.transaction);
        shareVideoMoment.setPackageName(this.bundleManager.packageName);
        shareVideoMoment.setVideoLength(videoObject.getDuration());
        shareVideoMoment.setFunVideoParam((ShareVideoMoment.FunVideoParam) JSONUtil.fromJSON(
                this.bundleManager.xtcShareMessage.getExt(), ShareVideoMoment.FunVideoParam.class));
        LogUtil.d(TAG, "shareVideoMessage: " + this.bundleManager.xtcShareMessage.getExt());
        sendVideoMsg(this.publishService, thumbnailPath, true, videoPath, shareVideoMoment, null, null);
    }
    private void sendVideoMsg(PublishService.PublishBinder publishBinder, String thumbnailPath, boolean fromAlbum,
            String videoName, ShareVideoMoment shareVideoMoment, String videoText, PoiBean poiBean) {
        if (publishBinder == null) {
            return;
        }
        if (TextUtils.isEmpty(this.loadingTokenVideoName)) {
            this.mSendVideoParam = new SendVideoParam(thumbnailPath, videoName, fromAlbum, videoText, poiBean,
                    shareVideoMoment, this.mVideoTokenVoResponse, null);
            toSendVideoInBackground(publishBinder, this.mSendVideoParam);
        } else {
            LogUtil.d(TAG, "sendVideoMsg wait token ");
            this.mSendVideoParam = new SendVideoParam(thumbnailPath, videoName, fromAlbum, videoText, poiBean,
                    shareVideoMoment);
        }
    }

    private void toSendVideoInBackground(PublishService.PublishBinder publishBinder, SendVideoParam sendVideoParam) {
        LogUtil.d(TAG, "toSendVideoInBackground");
        MomentApp.setIsSendingVideo(true);
        publishBinder.sendVideoMsg(sendVideoParam.getThumbnailPath(), sendVideoParam.isFromAlbum(),
                sendVideoParam.getVideoName(), sendVideoParam.getShareVideoMoment(), sendVideoParam.getVideoText(),
                sendVideoParam.getPoiBean(), sendVideoParam.getVideoTokenVoResponse(),
                sendVideoParam.getFriendsVisibleBean());
        this.bundleManager.setConversionId(-1L);
        shareEvent();
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    ((IShareToMomentView) getView()).shareSuccess();
                }
            }
        }, 1000L);
    }

    /** 预生成视频缩略图并预取上传 token。 */
    public void preSaveThumbnailAndToken(String videoPath, final String videoName) {
        this.loadingTokenVideoName = videoName;
        String thumbnailPath = FileManager.getVideoThumnailDir() + videoName + FileManager.WEBP_FORMAT;
        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailExists = thumbnailFile.isFile() && thumbnailFile.exists();
        LogUtil.d(TAG, "preSaveThumbnail thumbnailPath exit : " + thumbnailExists);
        if (!thumbnailExists) {
            ChatVideoUtil.saveThumnail(videoPath, thumbnailPath, ScreenUtils.screenWidth(this.mContext.getApplicationContext()),
                    ScreenUtils.screenHeight(this.mContext.getApplicationContext()));
        }
        PublishService.PublishBinder binder = this.publishService;
        if (binder == null || binder.getService() == null) {
            LogUtil.d(TAG, "preLoadVideoToken: publishBinder is null");
            this.loadingTokenVideoName = null;
            return;
        }
        VideoTokenVoResponse tokenVoResponse = this.mVideoTokenVoResponse;
        if (tokenVoResponse != null && Objects.equals(tokenVoResponse.getVideoName(), videoName)) {
            LogUtil.d(TAG, "preLoadVideoToken: already have token = [" + videoName + "]");
            this.loadingTokenVideoName = null;
            return;
        }
        this.publishService.getService().getUploadVideoToken(thumbnailPath, videoName, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoTokenVoResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(AbstractSharePresenter.TAG, "preLoadVideoToken#onError", throwable);
                        loadingTokenVideoName = null;
                        if (mSendVideoParam != null) {
                            mSendVideoParam = null;
                            publishService.getService().onFailCallback(throwable);
                        }
                    }

                    @Override
                    public void onNext(VideoTokenVoResponse videoTokenVoResponse) {
                        if (videoTokenVoResponse == null) {
                            LogUtil.d(AbstractSharePresenter.TAG, "preLoadVideoToken onNext result is null");
                            return;
                        }
                        LogUtil.d(AbstractSharePresenter.TAG, "preLoadVideoToken onNext");
                        loadingTokenVideoName = null;
                        mVideoTokenVoResponse = videoTokenVoResponse;
                        mVideoTokenVoResponse.setVideoName(videoName);
                        if (mSendVideoParam != null) {
                            LogUtil.d(AbstractSharePresenter.TAG, "sendVideoMsg continue");
                            mSendVideoParam.setVideoTokenVoResponse(mVideoTokenVoResponse);
                            toSendVideoInBackground(publishService, mSendVideoParam);
                            mSendVideoParam = null;
                        }
                    }
                });
    }

    private String getShareImagePath(XTCImageObject imageObject) {
        if (imageObject.getImagePath() != null) {
            return imageObject.getImagePath();
        }
        if (imageObject.getImageData() != null) {
            return ShareUtils.saveBitmapToSdcard(imageObject.getImageData());
        }
        return null;
    }

    private void uploadShareImageSource(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            this.bundleManager.sendOtherResponse(new Throwable("save icon to sdcard error!"));
            return;
        }
        LogUtil.d(TAG, "uploadShareImageSource: imagePath = [" + imagePath + "]");
        XTCImageObject imageObject = (XTCImageObject) this.bundleManager.xtcShareMessage.getShareObject();
        ShareImageMoment shareImageMoment = new ShareImageMoment();
        shareImageMoment.setDialogType(5);
        shareImageMoment.setLocalPath(imagePath);
        shareImageMoment.setPhotoHasDownload(true);
        shareImageMoment.setDesc(this.bundleManager.xtcShareMessage.getDescription());
        shareImageMoment.setAppIcon(this.bundleManager.icon);
        shareImageMoment.setAppName(this.bundleManager.name);
        shareImageMoment.setTransaction(this.bundleManager.transaction);
        shareImageMoment.setPackageName(this.bundleManager.packageName);
        if (imageObject.getMessageBitmapArgs() != null) {
            shareImageMoment.setMessageBitmapArgs(imageObject.getMessageBitmapArgs());
        }
        if (imageObject.getDialogBitmapArgs() != null) {
            shareImageMoment.setDialogBitmapArgs(imageObject.getDialogBitmapArgs());
        }
        shareImageMoment.setTrackMd5Value(ScreenshotUtils.getScreenshotMd5Json(imagePath));
        this.publishService.sendPhotoMsg(shareImageMoment);
    }

    private void uploadShareAppSource(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            this.bundleManager.sendOtherResponse(new Throwable("save icon to sdcard error!"));
            return;
        }
        ShareAppMoment shareAppMoment = new ShareAppMoment();
        shareAppMoment.setDialogType(5);
        shareAppMoment.setLocalPath(imagePath);
        shareAppMoment.setPhotoHasDownload(true);
        XTCAppExtendObject appExtendObject = (XTCAppExtendObject) this.bundleManager.xtcShareMessage.getShareObject();
        shareAppMoment.setAppIcon(this.bundleManager.icon);
        shareAppMoment.setAppName(this.bundleManager.name);
        shareAppMoment.setTargetPackage(this.bundleManager.packageName);
        shareAppMoment.setDesc(this.bundleManager.xtcShareMessage.getDescription());
        shareAppMoment.setExtInfo(appExtendObject.getExtInfo());
        shareAppMoment.setTargetClass(appExtendObject.getStartActivity());
        shareAppMoment.setTransaction(this.bundleManager.transaction);
        shareAppMoment.setPackageName(this.bundleManager.packageName);
        this.publishService.sendPhotoMsg(shareAppMoment);
    }

    private void uploadShareWebSource(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            this.bundleManager.sendOtherResponse(new Throwable("save icon to sdcard error!"));
            return;
        }
        ShareWebMoment shareWebMoment = new ShareWebMoment();
        shareWebMoment.setDialogType(5);
        shareWebMoment.setLocalPath(imagePath);
        shareWebMoment.setPhotoHasDownload(true);
        shareWebMoment.setAppIcon(this.bundleManager.icon);
        shareWebMoment.setAppName(this.bundleManager.name);
        shareWebMoment.setDesc(this.bundleManager.xtcShareMessage.getDescription());
        shareWebMoment.setTransaction(this.bundleManager.transaction);
        shareWebMoment.setPackageName(this.bundleManager.packageName);
        XTCWebObject webObject = (XTCWebObject) this.bundleManager.xtcShareMessage.getShareObject();
        shareWebMoment.setWebLink(webObject.getUrl());
        dealExtParam(webObject.getExtInfo(), webObject);
        this.publishService.sendPhotoMsg(shareWebMoment);
    }

    private void dealExtParam(String extInfo, XTCWebObject webObject) {
        if (!"1".equals(extInfo)) {
            return;
        }
        SerializableMap extMap = webObject.getExtMap();
        if (extMap == null) {
            return;
        }
        HashMap map = extMap.getMap();
        if (map == null || map.isEmpty()) {
            return;
        }
        CloudFileResource videoSource = JSONUtil.fromJSON((String) map.get(Constants.IntentExtra.VIDEO_SOURCE),
                CloudFileResource.class);
        CloudFileResource videoThumbSource = JSONUtil.fromJSON((String) map.get(Constants.IntentExtra.VIDEO_THUMB_SOURCE),
                CloudFileResource.class);
        this.shareWebMoment.setVideoSource(videoSource);
        this.shareWebMoment.setVideoThumbSource(videoThumbSource);
    }

    private void publishShareImageMoment(ShareImagePublish shareImagePublish, CloudFileResource source) {
        publishMoment(8, source.getKey(), JSONUtil.toJSON(shareImagePublish));
    }

    private void publishShareAppMoment(ShareAppPublish shareAppPublish, CloudFileResource source) {
        publishMoment(9, source.getKey(), JSONUtil.toJSON(shareAppPublish));
    }

    private void publishShareLivePhotoMoment(String content, CloudFileResource source, PoiBean poiBean) {
        publishMoment(8, source.getKey(), content, Arrays.asList(8, 23), 23, poiBean);
    }

    private void publishShareWebMoment(ShareWebPublish shareWebPublish, CloudFileResource source) {
        publishMoment(25, source.getKey(), JSONUtil.toJSON(shareWebPublish));
    }

    private void publishMoment(int type, String content) {
        publishMoment(type, null, content);
    }

    private void publishMoment(int type, String resource, String content) {
        publishMoment(type, resource, content, null);
    }

    private void publishMoment(int type, String resource, String content, PoiBean poiBean) {
        final long startTime = SystemClock.elapsedRealtime();
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(type);
        this.iMomentServe.publishMoment(type, resource, 0, content, this.bundleManager.packageName, poiBean,
                (FriendsVisibleBean) null)
                .subscribe(new Action1<Moment>() {
                    @Override
                    public void call(Moment moment) {
                        long publishEndTime = SystemClock.elapsedRealtime();
                        DigitalManager.getInstance().getDigitalEntity().publicTime =
                                String.valueOf(publishEndTime - startTime);
                        DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
                        DigitalManager.getInstance().getDigitalEntity().shareTransTime =
                                String.valueOf(SystemClock.elapsedRealtime() - publishEndTime);
                        publishSuccess(dbMoment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        HandlerUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                publishFail(throwable);
                            }
                        });
                        DigitalBigDateSender.onPublicResult(mContext, DigitalManager.getInstance().getDigitalEntity(),
                                false, "9005", throwable);
                    }
                });
    }

    private void publishMoment(int type, String resource, String content, List<Integer> visibleTypes,
            final int convertType, PoiBean poiBean) {
        this.iMomentServe.publishMoment(type, resource, 0, content, this.bundleManager.packageName, visibleTypes, poiBean)
                .subscribe(new Action1<Moment>() {
                    @Override
                    public void call(Moment moment) {
                        publishSuccess(BeanConverterUtil.convertToDbMoment(moment, convertType));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        HandlerUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                publishFail(throwable);
                            }
                        });
                    }
                });
    }

    private void publishSuccess(DbMoment moment) {
        LogUtil.d(TAG, "publish success ：" + moment);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (getView() != null) {
                    ((IShareToMomentView) getView()).shareSuccess();
                }
            }
        });
        this.bundleManager.setConversionId(-1L);
        shareEvent();
        EventBus.getDefault().post(moment);
        insertToDatabase(moment);
        DigitalBigDateSender.onPublicResult(this.mContext, DigitalManager.getInstance().getDigitalEntity(), true, "", null);
    }

    private void publishFail(Throwable throwable) {
        LogUtil.e(TAG, "publish fail：" + throwable.toString());
        sendFailResponse();
    }

    private void insertToDatabase(DbMoment moment) {
        if (moment.getType().intValue() == 8) {
            moment.setContent(JSONUtil.toJSON(this.shareImageMoment));
        } else if (moment.getType().intValue() == 9) {
            moment.setContent(JSONUtil.toJSON(this.shareAppMoment));
        } else if (moment.getType().intValue() == 25) {
            moment.setContent(JSONUtil.toJSON(this.shareWebMoment));
        } else if (moment.getType().intValue() == 28) {
            moment.setContent(JSONUtil.toJSON(this.shareImageMoment));
        }
        this.iMomentServe.insertMomentByMomentId(moment);
    }

    private void shareEvent() {
        this.behaviorEvent.shareEvent(this.bundleManager.transaction, false, StringConstant.RABBIT_WATCHID,
                this.bundleManager.packageName, 2, this.bundleManager.xtcShareMessage.getType());
    }

    /** 校验敏感词与开关后开始发送。 */
    public void dealSend() {
        IllegalMessageHandler illegalMessageHandler = IllegalMessageHandler.getInstance(
                this.mContext.getApplicationContext());
        if (!illegalMessageHandler.isInitHandler() || illegalMessageHandler.refreshCurrentState()) {
            Observable.just(false)
                    .map(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean input) {
                            illegalMessageHandler.initIllegalConfig(mContext.getApplicationContext(),
                                    IllegalConfigHandler.obtainConfig(mContext.getApplicationContext()),
                                    new IllegalMessageHandler.InitIllegalListener() {
                                        @Override
                                        public void onIIllegalFinish() {
                                            HandlerUtil.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    judgeCanSend(illegalMessageHandler);
                                                }
                                            });
                                        }
                                    });
                            return true;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }

                        @Override
                        public void onNext(Boolean result) {
                        }
                    });
        } else {
            judgeCanSend(illegalMessageHandler);
        }
    }

    private void judgeCanSend(IllegalMessageHandler illegalMessageHandler) {
        if (!illegalMessageHandler.isDisableSend()) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    startSend();
                }
            });
        } else {
            illegalMessageHandler.showDisableSendMessageHintDialog(this.mContext,
                    new HintIllegalContentDialog.HintClickListener() {
                        @Override
                        public void onAppealClick() {
                        }

                        @Override
                        public void onConfirmClick() {
                            sendFailResponse();
                        }
                    });
        }
    }
}
