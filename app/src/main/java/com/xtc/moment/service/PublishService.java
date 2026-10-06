package com.xtc.moment.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.contactapi.contact.utils.GsonUtil;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.behavior.DigitalBigDateSender;
import com.xtc.moment.behavior.DigitalConstant;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.behavior.QiNiuThrowable;
import com.xtc.moment.manager.PhotoCompressManager;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.PhotoMD5Value;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.PhotoTokenVo;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.module.bean.UploadPhotoBean;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.module.publish.PubVideoOrPhotoServer;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.serve.IMomentPhotoServe;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.serve.impl.VideoUploadStrategy;
import com.xtc.moment.serve.interfaces.IFileUploadStrategy;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ImageUtil;
import com.xtc.moment.util.MD5Utils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.qiniu.ICloudManager;
import com.xtc.qiniu.ICloudService;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils_screenshot_carry_data.ScreenshotMd5Bean;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class PublishService extends Service {

    public static final String ACTION_BIND = "com.xtc.moment.ACTION_PUBLISH_SERVICE_BIND";

    private static final String TAG = "PublishService";
    private static final String TMP = "_tmp";

    private Callback callback;
    private IMomentPhotoServe mMomentPhotoServe;
    private PublishBinder mPublishBinder;
    private long mUploadStartTime;

    public interface Callback {
        void onFail(Throwable throwable);

        void onUploadLivePhotoSuccess(LivePhotoMsg livePhotoMsg);

        void onUploadPhotoSuccess(PhotoMsg photoMsg, VideoTokenVoResponse videoTokenVoResponse);
    }

    public PublishBinder getMsgBinder() {
        return mPublishBinder;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "绑定service");
        return mPublishBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "解绑service");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate!!!");
        mPublishBinder = new PublishBinder();
        mMomentPhotoServe = new MomentPhotoServeImpl(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand!!!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy!!!");
        super.onDestroy();
    }

    public void sendPhotosMsg(ArrayList<PhotoMsg> photoMsgs, boolean isResend) {
        if (photoMsgs == null) {
            LogUtil.i(TAG, "photoMsgs ===null");
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.net_work_exception), Toast.LENGTH_SHORT).show();
        }
        upLoadPhotos(photoMsgs, isResend);
    }

    private void upLoadPhotos(final ArrayList<PhotoMsg> photoMsgs, boolean isResend) {
        LogUtil.d(TAG, "sendPhotoMsg = " + photoMsgs);
        if (photoMsgs == null) {
            return;
        }
        if (!isResend) {
            getUploadTokens(uploadPhotoBean(photoMsgs), photoMsgs);
            return;
        }
        resetStartTime();
        ArrayList<String> compressedPaths = new ArrayList<>();
        final long startTime = SystemClock.elapsedRealtime();
        Observable<PhotoMsg>[] compressObservables = PhotoCompressManager.createCompressObservables(photoMsgs, compressedPaths);
        if (compressObservables == null || compressObservables.length <= 0) {
            LogUtil.i(TAG, "compress photos error, size is empty");
            DigitalBigDateSender.onUploadError(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                    DigitalConstant.ErrorCode.UPLOAD_QINIU_ERROR, DigitalConstant.FailReason.COMPRESS_ERROR);
            if (callback != null) {
                callback.onFail(new Exception());
            }
            return;
        }
        Observable.combineLatest(java.util.Arrays.<Observable<? extends PhotoMsg>>asList(compressObservables), new FuncN<Object>() {
            @Override
            public Object call(Object... args) {
                LogUtil.i(TAG, "compress all photos success");
                DigitalManager.getInstance().getDigitalEntity().compressTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
                return null;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object value) {
                getUploadTokens(uploadPhotoBean(photoMsgs), photoMsgs);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "compress photos error", throwable);
                DigitalBigDateSender.onPublicResult(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                        false, DigitalConstant.ErrorCode.BEFORE_TOKEN_ERROR, throwable);
                if (callback != null) {
                    callback.onFail(throwable);
                }
            }
        });
    }

    public UploadPhotoBean uploadPhotoBean(ArrayList<PhotoMsg> photoMsgs) {
        ArrayList<PhotoTokenParam> params = new ArrayList<>();
        for (int i = 0; i < photoMsgs.size(); i++) {
            PhotoTokenParam param = new PhotoTokenParam();
            param.setFormat(PhotoTokenParam.WEBP_FORMAT);
            param.setMd5(ScreenshotUtils.buildCarryData(
                    MD5Utils.getPhotoMd5(photoMsgs.get(i).getLocalPath(), MomentApp.getWatchId()),
                    photoMsgs.get(i).getLocalPath()));
            param.setDialogType(photoMsgs.get(i).getDialogType());
            param.setWatchId(MomentApp.getWatchId());
            if (TextUtils.isEmpty(photoMsgs.get(i).getTrackMd5Value())) {
                photoMsgs.get(i).setTrackMd5Value(GsonUtil.toJson(new ScreenshotMd5Bean(MomentApp.getWatchId(), param.getMd5())));
            }
            params.add(param);
        }
        UploadPhotoBean bean = new UploadPhotoBean();
        bean.setmPhotoTokenParams(params);
        return bean;
    }

    public void getUploadTokens(UploadPhotoBean uploadPhotoBean, final ArrayList<PhotoMsg> photoMsgs) {
        List<PhotoTokenParam> params = uploadPhotoBean.getmPhotoTokenParams();
        if (params == null) {
            LogUtil.i(TAG, "photoTokenParams = null");
            if (callback != null) {
                callback.onFail(new Exception());
            }
            return;
        }
        final long startTime = SystemClock.elapsedRealtime();
        mMomentPhotoServe.getUploadTokens(params).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Action1<List<PhotoTokenVo>>() {
            @Override
            public void call(List<PhotoTokenVo> tokenVos) {
                DigitalManager.getInstance().getDigitalEntity().getTokenTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
                printUploadPhotoTime("getUploadToken");
                MultiPartUploadPhotos(tokenVos, photoMsgs);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "uploadPhoto error " + throwable);
                DigitalBigDateSender.onPublicResult(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                        false, DigitalConstant.ErrorCode.GET_TOKEN_ERROR, throwable);
                if (callback != null) {
                    callback.onFail(throwable);
                }
            }
        });
    }

    private void MultiPartUploadPhotos(List<PhotoTokenVo> tokenVos, final ArrayList<PhotoMsg> photoMsgs) {
        if (CollectionUtil.isEmpty(tokenVos) || CollectionUtil.isEmpty(photoMsgs) || tokenVos.size() != photoMsgs.size()) {
            LogUtil.i(TAG, "MultiPartUploadPhotos, tokenVos unequal to photoMsgs");
            if (callback != null) {
                callback.onFail(new Exception());
            }
            return;
        }
        for (int i = 0; i < tokenVos.size(); i++) {
            PhotoMsg photoMsg = photoMsgs.get(i);
            CloudFileResource source = tokenVos.get(i).getSource();
            if (source != null) {
                photoMsg.setSource(source);
            }
        }
        ArrayList<String> localPaths = new ArrayList<>();
        final long startTime = SystemClock.elapsedRealtime();
        Observable<PhotoTokenVo>[] uploadObservables = createUploadObservables(photoMsgs, tokenVos, localPaths);
        if (uploadObservables == null || uploadObservables.length <= 0) {
            DigitalBigDateSender.onUploadError(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                    DigitalConstant.ErrorCode.UPLOAD_QINIU_ERROR, DigitalConstant.FailReason.UPLOAD_ERROR);
            if (callback != null) {
                callback.onFail(new Exception());
            }
            return;
        }
        Observable.combineLatest(java.util.Arrays.<Observable<? extends PhotoTokenVo>>asList(uploadObservables), new FuncN<PhotoTokenVo>() {
            @Override
            public PhotoTokenVo call(Object... args) {
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Action1<PhotoTokenVo>() {
            @Override
            public void call(PhotoTokenVo value) {
                LogUtil.i(TAG, "MultiPart upload all photos success");
                onUploadPhotosSuccess(photoMsgs, startTime);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "startUploadPhoto#onError: ", throwable);
                if (throwable instanceof TimeoutException) {
                    LogUtil.e(TAG, "startUploadPhotos, TimeoutException ", throwable);
                }
                if (throwable instanceof QiNiuThrowable) {
                    DigitalBigDateSender.onUploadError(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                            DigitalConstant.ErrorCode.UPLOAD_QINIU_ERROR, ((QiNiuThrowable) throwable).getErrorCode());
                }
                if (callback != null) {
                    callback.onFail(throwable);
                }
            }
        });
    }

    private Observable<PhotoTokenVo>[] createUploadObservables(ArrayList<PhotoMsg> photoMsgs, List<PhotoTokenVo> tokenVos, ArrayList<String> localPaths) {
        if (CollectionUtil.isEmpty(photoMsgs)) {
            LogUtil.i(TAG, "createMsgObservables error");
            return null;
        }
        Observable<PhotoTokenVo>[] observables = new Observable[photoMsgs.size()];
        int size = photoMsgs.size();
        for (int i = 0; i < size; i++) {
            PhotoTokenVo tokenVo = tokenVos.get(i);
            final PhotoMsg photoMsg = photoMsgs.get(i);
            localPaths.add(photoMsg.getLocalPath());
            observables[i] = mMomentPhotoServe.uploadPhoto(photoMsg.getLocalPath(), tokenVo)
                    .timeout(60L, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<PhotoTokenVo, PhotoTokenVo>() {
                        @Override
                        public PhotoTokenVo call(PhotoTokenVo result) {
                            if (result == null) {
                                return null;
                            }
                            photoMsg.setType(result.getType());
                            photoMsg.setWangSuUrl(result.getWangSuUrl());
                            photoMsg.setZone(result.getZone());
                            photoMsg.setCustomParamMap(result.getCustomParamMap());
                            return result;
                        }
                    });
        }
        return observables;
    }

    public void sendPhotoMsg(PhotoMsg photoMsg, boolean isResend) {
        LogUtil.d(TAG, "sendPhotoMsg isResend = " + isResend);
        if (photoMsg == null) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showShort(this, R.string.net_work_exception);
        }
        if (!isResend) {
            uploadPhoto(photoMsg, isResend);
        } else if (!photoMsg.isPhotoHasUpload()) {
            uploadPhoto(photoMsg, isResend);
        }
    }
    public void sendVideoMsg(String thumbnailPath, boolean fromAlbum, String videoName, boolean isShareVideo,
                             ShareVideoMoment shareVideoMoment, String videoText, PoiBean poiBean,
                             VideoTokenVoResponse videoTokenVoResponse, FriendsVisibleBean friendsVisibleBean) {
        LogUtil.d(TAG, "sendVideoMsg fromAlbum = " + fromAlbum);
        if (TextUtils.isEmpty(thumbnailPath)) {
            DigitalBigDateSender.onPublicResult(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                    false, DigitalConstant.ErrorCode.SAVE_THUMBNAIL_ERROR, null);
            PubVideoOrPhotoServer.getInstance().onPublishVideoFail(new Throwable("videoTokenParam == null"));
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showShort(this, R.string.net_work_exception);
        }
        uploadVideo(thumbnailPath, fromAlbum, videoName, isShareVideo, shareVideoMoment, videoText, poiBean,
                videoTokenVoResponse, friendsVisibleBean);
    }

    private void uploadVideo(final String thumbnailPath, final boolean fromAlbum, final String videoName,
                             final boolean isShareVideo, final ShareVideoMoment shareVideoMoment, final String videoText,
                             final PoiBean poiBean, VideoTokenVoResponse videoTokenVoResponse,
                             final FriendsVisibleBean friendsVisibleBean) {
        if (TextUtils.isEmpty(thumbnailPath) || TextUtils.isEmpty(videoName)) {
            LogUtil.i(TAG, " uploadVideo error, path or name is empty");
            PubVideoOrPhotoServer.getInstance().onPublishVideoFail(new Exception());
            return;
        }
        final long startTime = SystemClock.elapsedRealtime();
        if (videoTokenVoResponse != null
                && Objects.equals(videoTokenVoResponse.getVideoName(), videoName.replace(FileManager.MP4_FORMAT, ""))
                && !TextUtils.isEmpty(videoTokenVoResponse.getUploadToken())) {
            LogUtil.d(TAG, "uploadVideo already getToken : videoName = [" + videoName + "]");
            dealVideoUploadToQiNiu(videoTokenVoResponse, startTime, poiBean, shareVideoMoment, isShareVideo,
                    videoText, thumbnailPath, fromAlbum, videoName, friendsVisibleBean);
            return;
        }
        getUploadVideoToken(thumbnailPath, videoName, fromAlbum).subscribe(new Subscriber<VideoTokenVoResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "uploadVideo#onError", throwable);
                DigitalBigDateSender.onPublicResult(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                        false, DigitalConstant.ErrorCode.GET_TOKEN_ERROR, throwable);
                PubVideoOrPhotoServer.getInstance().onPublishVideoFail(throwable);
            }

            @Override
            public void onNext(VideoTokenVoResponse response) {
                dealVideoUploadToQiNiu(response, startTime, poiBean, shareVideoMoment, isShareVideo, videoText,
                        thumbnailPath, fromAlbum, videoName, friendsVisibleBean);
            }
        });
    }

    public Observable<VideoTokenVoResponse> getUploadVideoToken(String thumbnailPath, String videoName, boolean fromAlbum) {
        VideoTokenParam param = new VideoTokenParam();
        param.setFormat(VideoTokenParam.MP4_FORMAT);
        param.setIconFormat(VideoTokenParam.THUMNAIL_FORMAT);
        param.setMd5(MD5Utils.getPhotoMd5(thumbnailPath, MomentApp.getWatchId()));
        param.setType(VideoTokenParam.VIDEO_TYPE);
        param.setSendType(VideoTokenParam.VIDEO_SEND_TYPE);
        LogUtil.d(TAG, "uploadVideo: videoName = " + videoName);
        String suffix = null;
        if (!fromAlbum) {
            int index = videoName.lastIndexOf("_");
            if (index != -1) {
                suffix = videoName.substring(index);
            }
        } else {
            int dotIndex = videoName.lastIndexOf(".");
            if (dotIndex != -1) {
                suffix = videoName.substring(0, dotIndex);
                if (!TextUtils.isEmpty(suffix)) {
                    int index = suffix.lastIndexOf("_");
                    if (index != -1) {
                        suffix = suffix.substring(index);
                    }
                }
            }
        }
        param.setVideoType(dealVideoType(suffix));
        return mMomentPhotoServe.getUploadVideoToken(param).subscribeOn(Schedulers.io());
    }

    private void dealVideoUploadToQiNiu(VideoTokenVoResponse tokenVo, long startTime, PoiBean poiBean,
                                        ShareVideoMoment shareVideoMoment, boolean isShareVideo, String videoText,
                                        String thumbnailPath, boolean fromAlbum, String videoName,
                                        FriendsVisibleBean friendsVisibleBean) {
        if (tokenVo == null) {
            PubVideoOrPhotoServer.getInstance().onPublishVideoFail(new NullPointerException("Token result is null"));
            return;
        }
        long uploadStartTime = SystemClock.elapsedRealtime();
        DigitalManager.getInstance().getDigitalEntity().getTokenTime = String.valueOf(uploadStartTime - startTime);
        tokenVo.setPoiBean(poiBean);
        LogUtil.d(TAG, "onNext: upload sha " + tokenVo.getSource());
        LogUtil.i(TAG, "isShareVideo " + isShareVideo);
        if (isShareVideo) {
            tokenVo.setDialogType(24);
            HashMap<String, String> customParamMap = new HashMap<>();
            shareVideoMoment.setSource(tokenVo.getSource());
            shareVideoMoment.setIcon(tokenVo.getIcon());
            shareVideoMoment.setTextMsg(videoText);
            customParamMap.put(ShareVideoMoment.SHARE_VIDEO_FLAGS, JSONUtil.toJSON(shareVideoMoment));
            tokenVo.setCustomParamMap(customParamMap);
        } else {
            LogUtil.i(TAG, "videoText =null ?" + videoText);
            if (!TextUtils.isEmpty(videoText)) {
                tokenVo.setDialogType(27);
            } else {
                tokenVo.setDialogType(6);
            }
        }
        LogUtil.i(TAG, "result 类型 " + tokenVo.getDialogType());
        uploadToQINiu(tokenVo, videoName, true, fromAlbum, isShareVideo, thumbnailPath, videoText, uploadStartTime, friendsVisibleBean);
        uploadToQINiu(tokenVo, videoName, false, fromAlbum, isShareVideo, thumbnailPath, videoText, uploadStartTime, friendsVisibleBean);
    }

    private int dealVideoType(String suffix) {
        LogUtil.d(TAG, "dealVideoType: Suffix = " + suffix);
        return (Constants.Suffix.SUFFIX_POINT.equals(suffix) || Constants.Suffix.SUFFIX_EDIT.equals(suffix)) ? 1 : 0;
    }

    private void uploadToQINiu(VideoTokenVoResponse tokenVo, String videoName, boolean isVideo, boolean fromAlbum,
                               boolean isShareVideo, String thumbnailPath, String videoText, long startTime,
                               FriendsVisibleBean friendsVisibleBean) {
        String localPath;
        String uploadToken;
        String key;
        if (isVideo) {
            localPath = (fromAlbum ? FileManager.getVideoDir() : FileManager.getThirdVideoPath()) + videoName + FileManager.MP4_FORMAT;
            uploadToken = tokenVo.getUploadToken();
            key = tokenVo.getSource().getKey();
            tokenVo.setLocalVideoPath(localPath);
            tokenVo.setContent(videoText);
            LogUtil.i(TAG, "小视频" + videoText);
        } else {
            localPath = (fromAlbum ? FileManager.getVideoThumnailDir() : FileManager.getThirdVideoThumnailPath()) + videoName + FileManager.WEBP_FORMAT;
            uploadToken = tokenVo.getIconUploadToken();
            key = tokenVo.getIcon().getKey();
            tokenVo.setLocalThumbnailPath(localPath);
            tokenVo.setContent(videoText);
            LogUtil.i(TAG, "相册分享添加文字" + videoText);
        }
        String uploadPath = isShareVideo ? (isVideo ? videoName : thumbnailPath) : localPath;
        LogUtil.d(TAG, "uploadToQINiu: " + tokenVo + "\nuploadToQINiu: " + uploadPath);
        ICloudService.uploadFile(ContextUtils.getContext(), uploadPath, key, uploadToken,
                new QiNiuUploadListener(isVideo, startTime, tokenVo, videoText, friendsVisibleBean));
    }

    private class QiNiuUploadListener implements ICloudManager.OnUpLoadListener {

        private final boolean isVideo;
        private final long startUploadTime;
        private final VideoTokenVoResponse videoMsg;
        private final String videoText;
        private final FriendsVisibleBean friendsVisibleBean;

        QiNiuUploadListener(boolean isVideo, long startUploadTime, VideoTokenVoResponse videoMsg, String videoText,
                            FriendsVisibleBean friendsVisibleBean) {
            this.isVideo = isVideo;
            this.startUploadTime = startUploadTime;
            this.videoMsg = videoMsg;
            this.videoText = videoText;
            this.friendsVisibleBean = friendsVisibleBean;
        }

        @Override
        public void onProgress(String key, double percent) {
            if (isVideo) {
                PubVideoOrPhotoServer.getInstance().onPublishVideoProgress((int) (percent * 100.0d));
            }
        }

        @Override
        public void onSuccess(String key) {
            if (!isVideo) {
                return;
            }
            DigitalManager.getInstance().getDigitalEntity().uploadTime = String.valueOf(SystemClock.elapsedRealtime() - startUploadTime);
            videoMsg.setIconUploadToken("");
            videoMsg.setUploadToken("");
            final MultiPhotoContent multiPhotoContent = new MultiPhotoContent();
            multiPhotoContent.setContent(videoText);
            final VideoTokenVoResponse tokenVo = videoMsg;
            final FriendsVisibleBean visibleBean = friendsVisibleBean;
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    publishVideoMoment(tokenVo, multiPhotoContent, visibleBean);
                }
            });
        }

        @Override
        public void onFailure(String key, int code, String message) {
            if (!isVideo) {
                return;
            }
            DigitalBigDateSender.onUploadError(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                    DigitalConstant.ErrorCode.UPLOAD_QINIU_ERROR, String.valueOf(code));
            PubVideoOrPhotoServer.getInstance().onPublishVideoFail(new Throwable(message));
        }
    }

    private void publishVideoMoment(VideoTokenVoResponse tokenVo, MultiPhotoContent multiPhotoContent, FriendsVisibleBean friendsVisibleBean) {
        tokenVo.setContent(JSONUtil.toJSON(multiPhotoContent));
        if (tokenVo != null && tokenVo.getPoiBean() != null) {
            BehaviorEvent.publishLbs(MomentApp.getAppContext());
        }
        PubVideoOrPhotoServer.getInstance().publishMoment(null, tokenVo, null, friendsVisibleBean);
        LogUtil.i(TAG, "videoMsg.getDialogType() 类型 ：" + tokenVo.getDialogType());
    }
    private void uploadLivePhoto(final LivePhotoMsg livePhotoMsg) {
        final long startTime = SystemClock.elapsedRealtime();
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final VideoMsg videoMsg = livePhotoMsg.getVideoMsg();
        VideoTokenParam param = new VideoTokenParam();
        param.setFormat(VideoTokenParam.MP4_FORMAT);
        param.setIconFormat(VideoTokenParam.THUMNAIL_FORMAT);
        param.setMd5(MD5Utils.getPhotoMd5(videoMsg.getLocalThumbnailPath(), MomentApp.getWatchId()));
        param.setType(VideoTokenParam.VIDEO_TYPE);
        param.setSendType(VideoTokenParam.VIDEO_SEND_TYPE);
        param.setWatchId(MomentApp.getWatchId());
        LogUtil.d(TAG, "VideoTokenParam:" + param);
        mMomentPhotoServe.getUploadVideoToken(param).subscribeOn(Schedulers.io()).subscribe(new HttpSubscriber<VideoTokenVoResponse>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onHttpError(Throwable throwable) {
                super.onHttpError(throwable);
                if (callback != null) {
                    callback.onFail(throwable);
                }
            }

            @Override
            public void onNext(VideoTokenVoResponse tokenVo) {
                super.onNext(tokenVo);
                DigitalManager.getInstance().getDigitalEntity().getTokenTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
                LogUtil.i(TAG, "videoTokenResponse:" + tokenVo);
                videoMsg.setIcon(tokenVo.getIcon());
                sendLivePhotoMsgAfterVideoAndThumnailUpload(livePhotoMsg, countDownLatch);
                LogUtil.i(TAG, "videoMsg LocalVideoPath = " + videoMsg.getLocalVideoPath());
                uploadFileToCloud(videoMsg.getLocalVideoPath(), tokenVo, null, livePhotoMsg, true, countDownLatch);
                uploadFileToCloud(videoMsg.getLocalThumbnailPath(), tokenVo, null, livePhotoMsg, false, countDownLatch);
            }
        });
    }

    private void uploadFileToCloud(String localPath, VideoTokenVoResponse tokenVo, VideoMsg videoMsg,
                                   LivePhotoMsg livePhotoMsg, boolean isVideo, final CountDownLatch countDownLatch) {
        VideoUploadStrategy strategy = new VideoUploadStrategy();
        if (videoMsg != null) {
            strategy.uploadFileToCloud(localPath, tokenVo, videoMsg, isVideo, new IFileUploadStrategy.FileUploadListener() {
                @Override
                public void onSuccess() {
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                        LogUtil.i(TAG, "countDownLatch.countDown() execute.");
                    }
                }

                @Override
                public void onFail(String key, int code, String message) {
                    LogUtil.e("MsgService", "uploadFileByQiniuSdk onFailure :" + message + " statusCode:" + code + " key:" + key);
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                        LogUtil.i(TAG, "countDownLatch.countDown() execute.");
                    }
                }
            });
        } else if (livePhotoMsg != null) {
            strategy.uploadFileToCloud(localPath, tokenVo, livePhotoMsg.getVideoMsg(), isVideo, new IFileUploadStrategy.FileUploadListener() {
                @Override
                public void onSuccess() {
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                        LogUtil.i(TAG, "countDownLatch.countDown() execute.");
                    }
                }

                @Override
                public void onFail(String key, int code, String message) {
                    LogUtil.e("MsgService", "uploadFileByQiniuSdk onFailure :" + message + " statusCode:" + code + " key:" + key);
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                        LogUtil.i(TAG, "countDownLatch.countDown() execute.");
                        DigitalBigDateSender.onUploadError(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                                DigitalConstant.ErrorCode.UPLOAD_QINIU_ERROR, String.valueOf(code));
                    }
                }
            });
        }
    }

    private void sendLivePhotoMsgAfterVideoAndThumnailUpload(final LivePhotoMsg livePhotoMsg, final CountDownLatch countDownLatch) {
        Observable.just((Object) null).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "senImVideoMsgAfterVideoAndThumnailUpload onError:" + throwable);
            }

            @Override
            public void onNext(Object value) {
                try {
                    LogUtil.i(TAG, "countDownLatch.await() execute.");
                    countDownLatch.await(20L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, "InterruptedException e:" + e);
                }
                VideoMsg videoMsg = livePhotoMsg.getVideoMsg();
                CloudFileResource icon = videoMsg.getIcon();
                if (videoMsg.getThumnailHasUpload() && videoMsg.getVideoHasUpload() && icon != null) {
                    LogUtil.i(TAG, "图片和视频上传完成");
                    SmallPicSouce smallPic = new SmallPicSouce(icon.getKey(), icon.getDownloadUrl(), icon.getUrlDeadline());
                    livePhotoMsg.setSource(icon);
                    livePhotoMsg.setSmallPic(smallPic);
                    livePhotoMsg.setWangSuUrl(videoMsg.getWangSuUrl());
                    livePhotoMsg.setPhotoHasDownload(videoMsg.getThumnailHasDownload());
                    livePhotoMsg.setType(videoMsg.getType());
                    livePhotoMsg.setPhotoHasUpload(videoMsg.getVideoHasUpload());
                    livePhotoMsg.setSmallPic(smallPic);
                    callback.onUploadLivePhotoSuccess(livePhotoMsg);
                    LogUtil.i(TAG, " livePhotoMsg  :" + livePhotoMsg);
                    return;
                }
                LogUtil.i(TAG, "upload video or thumnail failed. videoMsg:" + videoMsg);
                callback.onFail(new Throwable("upload live video failed"));
            }
        });
    }

    private void uploadPhoto(final PhotoMsg photoMsg, boolean isResend) {
        if (photoMsg == null) {
            LogUtil.i(TAG, "uploadPhoto error, photoMsg is empty");
            if (callback != null) {
                callback.onFail(new Exception());
            }
            return;
        }
        resetStartTime();
        Observable.just(photoMsg.getLocalPath()).map(new Func1<String, String>() {
            @Override
            public String call(String path) {
                long startTime = SystemClock.elapsedRealtime();
                String compressedPath = null;
                try {
                    compressedPath = ImageUtil.compressByScale(path, PhotoTokenParam.WEBP_FORMAT,
                            Constants.Camera.PhotoSize.WIDTH, Constants.Camera.PhotoSize.HEIGHT);
                } catch (Throwable throwable) {
                    LogUtil.e(TAG, "compress photo error", throwable);
                }
                DigitalManager.getInstance().getDigitalEntity().compressTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
                printUploadPhotoTime("compressTime");
                return compressedPath;
            }
        }).map(new Func1<String, UploadPhotoBean>() {
            @Override
            public UploadPhotoBean call(String compressedPath) {
                String localPath = TextUtils.isEmpty(compressedPath) ? photoMsg.getLocalPath() : compressedPath;
                PhotoTokenParam param = new PhotoTokenParam();
                if (localPath.endsWith(PhotoTokenParam.GIF_FORMAT)) {
                    param.setFormat(PhotoTokenParam.GIF_FORMAT);
                    param.setSmallPicFormat(PhotoTokenParam.GIF_FORMAT);
                } else {
                    param.setFormat(PhotoTokenParam.WEBP_FORMAT);
                    param.setSmallPicFormat(PhotoTokenParam.WEBP_FORMAT);
                }
                param.setMd5(ScreenshotUtils.buildCarryData(
                        MD5Utils.getPhotoMd5(TextUtils.isEmpty(compressedPath) ? photoMsg.getLocalPath() : compressedPath, MomentApp.getWatchId()),
                        photoMsg.getLocalPath()));
                param.setDialogType(photoMsg.getDialogType());
                param.setWatchId(MomentApp.getWatchId());
                param.setContent(photoMsg.getContent());
                if (TextUtils.isEmpty(photoMsg.getTrackMd5Value())) {
                    photoMsg.setTrackMd5Value(GsonUtil.toJson(new ScreenshotMd5Bean(MomentApp.getWatchId(), param.getMd5())));
                }
                UploadPhotoBean bean = new UploadPhotoBean();
                bean.setPhotoTokenParam(param);
                bean.setTmpPath(compressedPath);
                return bean;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<UploadPhotoBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "uploadPhoto get PhotoTokenParam onError:", throwable);
                if (callback != null) {
                    callback.onFail(throwable);
                }
                DigitalBigDateSender.onPublicResult(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                        false, DigitalConstant.ErrorCode.BEFORE_TOKEN_ERROR, throwable);
            }

            @Override
            public void onNext(final UploadPhotoBean uploadPhotoBean) {
                LogUtil.i(TAG, "uploadPhoto#onNext: " + uploadPhotoBean);
                if (uploadPhotoBean == null || uploadPhotoBean.getPhotoTokenParam() == null) {
                    onError(new Throwable("uploadPhotoBean error. " + uploadPhotoBean));
                    return;
                }
                final long startTime = SystemClock.elapsedRealtime();
                mMomentPhotoServe.getUploadToken(uploadPhotoBean.getPhotoTokenParam()).subscribeOn(Schedulers.io()).subscribe(new HttpSubscriber<PhotoTokenVo>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                        super.onHttpError(throwable);
                        LogUtil.e(TAG, "uploadPhoto error " + throwable.getMessage());
                        if (callback != null) {
                            callback.onFail(throwable);
                        }
                        DigitalBigDateSender.onPublicResult(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                                false, DigitalConstant.ErrorCode.GET_TOKEN_ERROR, throwable);
                    }

                    @Override
                    public void onNext(PhotoTokenVo tokenVo) {
                        DigitalManager.getInstance().getDigitalEntity().getTokenTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
                        super.onNext(tokenVo);
                        tokenVo.setText(photoMsg.getContent());
                        photoMsg.setSource(tokenVo.getSource());
                        LogUtil.d(TAG, "onNext: photoMsg.getLocalPath()=" + photoMsg.getLocalPath());
                        if (uploadPhotoBean.getTmpPath() != null) {
                            startUploadPhoto(photoMsg, uploadPhotoBean.getTmpPath(), true, tokenVo);
                        } else {
                            startUploadPhoto(photoMsg, photoMsg.getLocalPath(), false, tokenVo);
                        }
                    }
                });
            }
        });
    }
    private void startUploadPhoto(final PhotoMsg photoMsg, final String localPath, final boolean isCompressPhoto, PhotoTokenVo tokenVo) {
        LogUtil.d(TAG, "startUploadPhoto: photoMsg = [" + photoMsg + "], photoLocalPath = [" + localPath
                + "], isCompressPhoto = [" + isCompressPhoto + "], photoTokenVo = [" + tokenVo + "]");
        if (tokenVo == null) {
            return;
        }
        final long startTime = SystemClock.elapsedRealtime();
        mMomentPhotoServe.uploadPhoto(localPath, tokenVo).map(new Func1<PhotoTokenVo, PhotoTokenVo>() {
            @Override
            public PhotoTokenVo call(PhotoTokenVo result) {
                DigitalManager.getInstance().getDigitalEntity().uploadTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
                if (isCompressPhoto) {
                    photoMsg.setLocalPath(localPath);
                }
                return result;
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<PhotoTokenVo>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof QiNiuThrowable) {
                    DigitalBigDateSender.onUploadError(getApplicationContext(), DigitalManager.getInstance().getDigitalEntity(),
                            DigitalConstant.ErrorCode.UPLOAD_QINIU_ERROR, ((QiNiuThrowable) throwable).getErrorCode());
                }
                LogUtil.e(TAG, "startUploadPhoto#onError: ", throwable);
                if (isCompressPhoto) {
                    FileUtils.deleteFile(localPath);
                }
                if (callback != null) {
                    callback.onFail(throwable);
                }
            }

            @Override
            public void onNext(PhotoTokenVo result) {
                LogUtil.i(TAG, "photo upload complete!photoTokenVo：" + result.toString());
                photoMsg.setType(result.getType());
                photoMsg.setSource(result.getSource());
                photoMsg.setSmallPic(result.getSmallPic());
                photoMsg.setWangSuUrl(result.getWangSuUrl());
                photoMsg.setZone(result.getZone());
                photoMsg.setCustomParamMap(result.getCustomParamMap());
                photoMsg.setPhotoHasUpload(true);
                photoMsg.setPhotoHasDownload(true);
                printUploadPhotoTime("UploadPhoto");
                if (callback != null) {
                    callback.onUploadPhotoSuccess(photoMsg, null);
                }
            }
        });
    }

    private void onUploadPhotosSuccess(ArrayList<PhotoMsg> photoMsgs, long startTime) {
        DigitalManager.getInstance().getDigitalEntity().uploadTime = String.valueOf(SystemClock.elapsedRealtime() - startTime);
        StringBuilder keys = new StringBuilder();
        PhotoMsg lastPhotoMsg = new PhotoMsg();
        int size = photoMsgs.size();
        for (int i = 0; i < size; i++) {
            PhotoMsg photoMsg = photoMsgs.get(i);
            if (photoMsg != null && photoMsg.getSource() != null) {
                keys.append(photoMsg.getSource().getKey());
                int lastIndex = size - 1;
                if (i != lastIndex) {
                    keys.append(",");
                } else {
                    lastPhotoMsg = photoMsgs.get(lastIndex);
                }
            }
        }
        setLastPhotoMsg(lastPhotoMsg, photoMsgs);
        lastPhotoMsg.getSource().setKey(keys.toString());
        lastPhotoMsg.setPhotoHasUpload(true);
        lastPhotoMsg.setPhotoHasDownload(true);
        lastPhotoMsg.setContent(getMultiPhotosContent(photoMsgs, lastPhotoMsg.getContent()));
        LogUtil.i(TAG, "onUploadPhotosSuccess");
        if (callback != null) {
            callback.onUploadPhotoSuccess(lastPhotoMsg, null);
        }
    }

    private void setLastPhotoMsg(PhotoMsg lastPhotoMsg, ArrayList<PhotoMsg> photoMsgs) {
        if (lastPhotoMsg == null || CollectionUtil.isEmpty(photoMsgs)) {
            return;
        }
        StringBuilder localPaths = new StringBuilder();
        StringBuilder downloadUrls = new StringBuilder();
        for (PhotoMsg photoMsg : photoMsgs) {
            localPaths.append(photoMsg.getLocalPath());
            localPaths.append(",");
            downloadUrls.append(photoMsg.getSource().getDownloadUrl());
            downloadUrls.append(",");
        }
        String localPathString = localPaths.toString();
        String downloadUrlString = downloadUrls.toString();
        lastPhotoMsg.setLocalPath(localPathString.substring(0, localPathString.length() - 1));
        lastPhotoMsg.getSource().setDownloadUrl(downloadUrlString.substring(0, downloadUrlString.length() - 1));
    }

    private String getMultiPhotosContent(ArrayList<PhotoMsg> photoMsgs, String content) {
        if (CollectionUtil.isEmpty(photoMsgs)) {
            return null;
        }
        MultiPhotoContent multiPhotoContent = new MultiPhotoContent();
        multiPhotoContent.setContent(content);
        ArrayList<PhotoMD5Value> md5Values = new ArrayList<>();
        Iterator<PhotoMsg> iterator = photoMsgs.iterator();
        while (iterator.hasNext()) {
            md5Values.add(JSONUtil.fromJSON(iterator.next().getTrackMd5Value(), PhotoMD5Value.class));
        }
        multiPhotoContent.setTrackMd5Values(md5Values);
        return JSONUtil.toJSON(multiPhotoContent);
    }

    private void resetStartTime() {
        mUploadStartTime = System.currentTimeMillis();
        LogUtil.i(TAG, "resetStartTime: mUploadStartTime: " + mUploadStartTime);
    }

    private void printUploadPhotoTime(String tag) {
        LogUtil.i(TAG, "printUploadPhotoTime: " + tag + ": " + (System.currentTimeMillis() - mUploadStartTime) + " ms");
        resetStartTime();
    }

    public class PublishBinder extends Binder {

        public PublishBinder() {
        }

        public PublishService getService() {
            return PublishService.this;
        }

        public void sendPhotoMsg(PhotoMsg photoMsg) {
            PublishService.this.sendPhotoMsg(photoMsg, false);
        }

        public void senPhotosMsg(ArrayList<PhotoMsg> photoMsgs, boolean isResend) {
            PublishService.this.sendPhotosMsg(photoMsgs, false);
        }

        public void sendLivePhotoMsg(LivePhotoMsg livePhotoMsg) {
            if (livePhotoMsg != null && livePhotoMsg.getVideoMsg() != null) {
                PublishService.this.uploadLivePhoto(livePhotoMsg);
            } else {
                LogUtil.e(TAG, "livePhotoMsg is null!!!");
            }
        }

        public void sendVideoMsg(String thumbnailPath, boolean fromAlbum, String videoName, ShareVideoMoment shareVideoMoment,
                                 String videoText, PoiBean poiBean, VideoTokenVoResponse videoTokenVoResponse,
                                 FriendsVisibleBean friendsVisibleBean) {
            PublishService.this.sendVideoMsg(thumbnailPath, fromAlbum, videoName, shareVideoMoment != null,
                    shareVideoMoment, videoText, poiBean, videoTokenVoResponse, friendsVisibleBean);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void onFailCallback(Throwable throwable) {
        if (callback != null) {
            callback.onFail(throwable);
        }
    }
}
