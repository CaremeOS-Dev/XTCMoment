package com.xtc.moment.module.publish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.aitext.manager.AITextConfig;
import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.behavior.DigitalBigDateSender;
import com.xtc.moment.behavior.DigitalConstant;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.manager.PhotoCompressManager;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.SendPhotosParam;
import com.xtc.moment.module.bean.SendVideoParam;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.net.bean.BanStateBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.TemplateResponseBean;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.IMomentTemplateServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.MomentTemplateServeImpl;
import com.xtc.moment.service.PublishService;
import com.xtc.moment.service.PublishVideoOrPhotoCallback;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.BroadcastReceiverUtil;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ScreenUtils;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * 动态发布 presenter：负责图片/视频发布前的压缩与缩略图准备、发布请求下发以及发布结果回调。
 */
public class PublishPresenter extends MvpBasePresenter<IPublishView> {

    private static final String TAG = "PublishPresenter";

    /** 发布被限制时的服务端错误码。 */
    private static final String ERROR_CODE_PUBLISH_LIMITED = "000060";
    /** 内容不合法时的服务端错误码。 */
    private static final String ERROR_CODE_PUBLISH_INVALID = "000061";
    /** 视频内容不合法时的服务端错误码。 */
    private static final String ERROR_CODE_VIDEO_INVALID = "000008";
    /** 发布失败埋点使用的错误码。 */
    private static final String PUBLIC_RESULT_ERROR_CODE = "9005";
    /** 视频动态类型。 */
    private static final int MOMENT_TYPE_VIDEO = 6;
    /** 位置动态类型。 */
    private static final int MOMENT_TYPE_LOCATION = 2;
    /** 实况照片本地动态子类型。 */
    private static final int LIVE_PHOTO_DB_TYPE = 22;

    private AITextConfig aiTextConfig;
    private final String appName;
    private volatile boolean compressingImage;
    private final Context context;
    private boolean hasUpdateBanState;
    private final IMomentServe momentServe;
    private final IMomentTemplateServe momentTemplateServe;
    private volatile String loadingTokenVideoName;
    private volatile SendPhotosParam sendPhotosParam;
    private volatile SendVideoParam sendVideoParam;
    private VideoTokenVoResponse videoTokenVoResponse;
    private final IMomentsDataSource momentsDataSource;
    private final MomentsLocalDataSource momentsLocalDataSource;
    private final MomentsRemoteDataSource momentsRemoteDataSource;

    /**
     * 监听拍照/相册保存结果的广播，回调到视图层。
     */
    private final BroadcastReceiver takePhotoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "action:" + action + ";extra:" + intent.getStringExtra("app_name")
                    + ";path:" + intent.getStringExtra(Constants.PublishMedia.EXTRA_PHOTO_PATH));
            if (Constants.PublishMedia.ACTION_PHOTO_TAKE_SUCCESS.equals(action)) {
                LogUtil.d(TAG, "receive PHOTO_SAVE_SUCCESS broadcast");
                String takePhotoPath = intent.getStringExtra(Constants.PublishMedia.EXTRA_PHOTO_PATH);
                LogUtil.d(TAG, "receive PHOTO_TAKE_SUCCESS broadcast,photoPath = " + takePhotoPath);
                PhotoMsg takePhotoMsg = BeanConverterUtil.toPhotoMsg(takePhotoPath);
                if (takePhotoMsg != null) {
                    LogUtil.d(TAG, "photoMsg:" + takePhotoMsg);
                    if (PublishPresenter.this.isViewAttached() && PublishPresenter.this.getView() != null) {
                        PublishPresenter.this.getView().showLoading();
                    }
                }
            }
            if (Constants.PublishMedia.ACTION_PHOTO_SAVE_SUCCESS.equals(action)) {
                LogUtil.d(TAG, "receive PHOTO_SAVE_SUCCESS broadcast");
                String savedPhotoPath = intent.getStringExtra(Constants.PublishMedia.EXTRA_PHOTO_PATH);
                LogUtil.d(TAG, "receive PHOTO_TAKE_SUCCESS broadcast,photoPath = " + savedPhotoPath);
                PhotoMsg savedPhotoMsg = BeanConverterUtil.toPhotoMsg(savedPhotoPath);
                if (savedPhotoMsg == null || !PublishPresenter.this.isViewAttached()
                        || PublishPresenter.this.getView() == null) {
                    return;
                }
                PublishPresenter.this.getView().savePhotoSuccess(savedPhotoMsg);
            }
        }
    };
    private final CopyOnWriteArrayList<String> needCompressPhotoList = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, String> compressPhotoMap = new ConcurrentHashMap<>();

    public PublishPresenter(Context context) {
        this.context = context;
        this.momentServe = MomentServeImpl.getInstance(context);
        this.momentTemplateServe = MomentTemplateServeImpl.getInstance(context);
        this.momentsRemoteDataSource = MomentsRemoteDataSource.getInstance(context);
        this.momentsLocalDataSource = MomentsLocalDataSource.getInstance(context);
        this.momentsDataSource = MomentsRepository.getInstance(this.momentsRemoteDataSource, this.momentsLocalDataSource);
        this.appName = context.getPackageName();
    }

    public void getMomentTemplateFromNet() {
        this.momentTemplateServe.getMomentTemplateFromNet(0L, 100L, 0L)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<TemplateResponseBean>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG, "getMomentTemplate onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, throwable);
                    }

                    @Override
                    public void onNext(TemplateResponseBean templateResponseBean) {
                        if (templateResponseBean == null) {
                            return;
                        }
                        long lastRequestTime = SharedTool.getCanRequestReportTime(context);
                        long currentTime = System.currentTimeMillis();
                        long differTime = currentTime - lastRequestTime;
                        LogUtil.i(TAG, "saveTime = " + lastRequestTime + "  currentTime = "
                                + currentTime + "  differTime = " + differTime);
                        if (differTime < Constants.DIFFER_TIME) {
                            SharedTool.removeReportInfoMessage(context);
                            MomentTemplateServeImpl.getInstance(context).deleteAllTemplates();
                        } else {
                            PublishPresenter.this.momentTemplateServe.updateTemplates(
                                    templateResponseBean.getResources());
                        }
                    }
                });
    }

    public void updateBanState() {
        if (this.hasUpdateBanState) {
            return;
        }
        this.momentServe.updateBanState()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BanStateBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "updateBanState onError");
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(BanStateBean banStateBean) {
                        LogUtil.d(TAG, "moment banState = " + banStateBean.getBannedToPost());
                        SharedTool.saveBanToPublish(context, banStateBean.getBannedToPost().booleanValue());
                        PublishPresenter.this.hasUpdateBanState = true;
                    }
                });
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PublishMedia.ACTION_PHOTO_TAKE_SUCCESS);
        intentFilter.addAction(Constants.PublishMedia.ACTION_PHOTO_SAVE_SUCCESS);
        if (this.context != null) {
            BroadcastReceiverUtil.registerReceiver(this.context, this.takePhotoReceiver, intentFilter);
        }
    }

    public void unregister() {
        if (this.context != null) {
            BroadcastReceiverUtil.unregisterReceiver(this.context, this.takePhotoReceiver);
        }
    }

    public void sendPhotoMsg(PublishService.PublishBinder publishBinder, PhotoMsg photoMsg) {
        if (publishBinder == null) {
            LogUtil.e(TAG, "msgBinder is null!!!");
        } else {
            publishBinder.sendPhotoMsg(photoMsg);
        }
    }
    /**
     * 发送多图动态：先压缩尚未处理过的图片，压缩完成后统一下发。
     */
    public void sendPhotosMsh(PublishService.PublishBinder publishBinder, List<String> photoPaths,
                              String content, PoiBean poiBean) {
        if (publishBinder == null) {
            LogUtil.e(TAG, "sendPhotosMsh : msgBinder is null!!!");
            return;
        }
        if (CollectionUtil.isEmpty(photoPaths)) {
            LogUtil.e(TAG, "sendPhotosMsh : photoList is empty");
            return;
        }
        for (int index = 0; index < photoPaths.size(); index++) {
            String photoPath = photoPaths.get(index);
            if (!this.compressPhotoMap.containsKey(photoPath)) {
                this.needCompressPhotoList.add(photoPath);
            }
        }
        this.sendPhotosParam = null;
        if (this.needCompressPhotoList.size() == 0) {
            LogUtil.d(TAG, "sendPhotosMsh now");
            publishBinder.senPhotosMsg(BeanConverterUtil.toPhotosMsg(photoPaths, content, poiBean,
                    this.compressPhotoMap), false);
            return;
        }
        this.sendPhotosParam = new SendPhotosParam(photoPaths, content, poiBean, publishBinder);
        if (this.compressingImage) {
            LogUtil.d(TAG, "sendPhotosMsh : continue compress photos: is compressing");
            return;
        }
        LogUtil.i(TAG, "sendPhotosMsh : continue compress photos");
        ArrayList<String> pendingPaths = new ArrayList<>(this.needCompressPhotoList);
        this.needCompressPhotoList.clear();
        preCompressImage(pendingPaths, publishBinder);
    }

    /**
     * 压缩图片列表，全部成功后回调发送多图动态。
     */
    public void preCompressImage(List<String> photoPaths, final PublishService.PublishBinder publishBinder) {
        if (CollectionUtil.isEmpty(photoPaths)) {
            LogUtil.d(TAG, "preCompressImage: photoPath is null");
            onCompressPhotoFail(publishBinder, new NullPointerException("photoPath is null"));
            return;
        }
        final ArrayList<String> pathsToCompress = new ArrayList<>();
        for (String photoPath : photoPaths) {
            if (TextUtils.isEmpty(photoPath) || this.compressPhotoMap.containsKey(photoPath)) {
                LogUtil.d(TAG, "path has been compressed: " + photoPath);
            } else {
                pathsToCompress.add(photoPath);
            }
        }
        if (pathsToCompress.size() <= 0) {
            LogUtil.d(TAG, "preCompressImage: photoPath is all compress");
            if (this.sendPhotosParam != null) {
                sendPhotosMsh(this.sendPhotosParam.getPublishBinder(), this.sendPhotosParam.getPhotoMsgs(),
                        this.sendPhotosParam.getContent(), this.sendPhotosParam.getPoiBean());
            }
            return;
        }
        if (this.compressingImage) {
            LogUtil.d(TAG, "preCompressImage: is compressing");
            return;
        }
        LogUtil.d(TAG, "preCompressImage: compressPaths = [" + pathsToCompress + "]");
        this.compressingImage = true;
        final long compressStartTime = SystemClock.elapsedRealtime();
        Observable<String>[] compressObservables = PhotoCompressManager.createCompressObservables(pathsToCompress);
        if (compressObservables == null || compressObservables.length <= 0) {
            LogUtil.i(TAG, "preCompressImage: compress photos error, size is empty");
            this.compressingImage = false;
            onCompressPhotoFail(publishBinder, new NullPointerException("compressObservables is null"));
            return;
        }
        Observable.zip(compressObservables, new FuncN<Object[]>() {
            @Override
            public Object[] call(Object... args) {
                return args;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object[]>() {
                    @Override
                    public void call(Object[] resultPathArray) {
                        PublishPresenter.this.compressingImage = false;
                        if (resultPathArray == null || resultPathArray.length != pathsToCompress.size()) {
                            PublishPresenter.this.onCompressPhotoFail(publishBinder,
                                    new NullPointerException("resultPathArray is error"));
                            return;
                        }
                        for (int index = 0; index < resultPathArray.length; index++) {
                            PublishPresenter.this.compressPhotoMap.put(pathsToCompress.get(index),
                                    String.valueOf(resultPathArray[index]));
                        }
                        LogUtil.i(TAG, "preCompressImage: compress all photos success");
                        if (PublishPresenter.this.needCompressPhotoList.size() > 0) {
                            ArrayList<String> remainingPaths =
                                    new ArrayList<>(PublishPresenter.this.needCompressPhotoList);
                            PublishPresenter.this.needCompressPhotoList.clear();
                            PublishPresenter.this.preCompressImage(remainingPaths, null);
                            return;
                        }
                        if (PublishPresenter.this.sendPhotosParam != null) {
                            DigitalManager.getInstance().getDigitalEntity().compressTime =
                                    String.valueOf(SystemClock.elapsedRealtime() - compressStartTime);
                            LogUtil.i(TAG, "continue send photo");
                            PublishPresenter.this.sendPhotosMsh(
                                    PublishPresenter.this.sendPhotosParam.getPublishBinder(),
                                    PublishPresenter.this.sendPhotosParam.getPhotoMsgs(),
                                    PublishPresenter.this.sendPhotosParam.getContent(),
                                    PublishPresenter.this.sendPhotosParam.getPoiBean());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "preCompressImage: compress photos error", throwable);
                        PublishPresenter.this.compressingImage = false;
                        PublishPresenter.this.needCompressPhotoList.clear();
                        PublishPresenter.this.onCompressPhotoFail(
                                PublishPresenter.this.sendPhotosParam != null
                                        ? PublishPresenter.this.sendPhotosParam.getPublishBinder()
                                        : publishBinder,
                                throwable);
                        PublishPresenter.this.sendPhotosParam = null;
                    }
                });
    }

    private void onCompressPhotoFail(PublishService.PublishBinder publishBinder, Throwable throwable) {
        if (publishBinder == null || publishBinder.getService() == null) {
            return;
        }
        DigitalBigDateSender.onUploadError(MomentApp.getAppContext(),
                DigitalManager.getInstance().getDigitalEntity(),
                DigitalConstant.ErrorCode.BEFORE_TOKEN_ERROR,
                DigitalConstant.FailReason.COMPRESS_ERROR);
        publishBinder.getService().onFailCallback(throwable);
    }

    public void publishMoment(String content, int type, int resourceId) {
        publishMoment(content, type, resourceId, null);
    }

    public void publishMoment(String content, int type, int resourceId, PoiBean poiBean) {
        PublishMomentTask publishMomentTask = new PublishMomentTask(this.momentsDataSource, this.context);
        PublishMomentTask.RequestValues requestValues = new PublishMomentTask.RequestValues(
                AccountInfoServerImpl.getInstance(this.context).getWatchAccountInfo().getWatchId(this.context),
                0, null, content, type, null,
                MomentPrerogativeServeImpl.getInstance(this.context).getCurrentUseBackgroundEmotionId());
        if (poiBean != null) {
            requestValues.setLocation(poiBean.getCity() + poiBean.getAddressDesc());
            requestValues.setLocationType(poiBean.getLocationType());
            requestValues.setLatitude(Double.parseDouble(poiBean.getLocation().getLatitude()));
            requestValues.setLongitude(Double.parseDouble(poiBean.getLocation().getLongitude()));
        }
        publishMomentTask.setRequestValues(requestValues);
        publishMomentTask.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (!PublishPresenter.this.isViewAttached() || PublishPresenter.this.getView() == null
                        || !(responseValue instanceof PublishMomentTask.ResponseValue)) {
                    return;
                }
                PublishPresenter.this.getView().publishSuccess(
                        ((PublishMomentTask.ResponseValue) responseValue).getDbMoment());
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (!PublishPresenter.this.isViewAttached() || PublishPresenter.this.getView() == null
                        || !(responseValue instanceof PublishMomentTask.ErrorResponseValue)) {
                    return;
                }
                String errorCode = ((PublishMomentTask.ErrorResponseValue) responseValue).getErrorCode();
                if (ERROR_CODE_PUBLISH_LIMITED.equals(errorCode)) {
                    PublishPresenter.this.getView().publishLimited();
                } else if (ERROR_CODE_PUBLISH_INVALID.equals(errorCode)) {
                    PublishPresenter.this.getView().publishInvalidate();
                } else {
                    PublishPresenter.this.getView().publishFail(errorCode);
                }
            }

            @Override
            protected void onError() {
                if (!PublishPresenter.this.isViewAttached() || PublishPresenter.this.getView() == null) {
                    return;
                }
                PublishPresenter.this.getView().publishFail("");
            }
        });
        TaskDispatcher.dispatchImmediately(publishMomentTask);
    }

    public void sendLivePhotoMsg(PublishService.PublishBinder publishBinder, LivePhotoMsg livePhotoMsg) {
        if (publishBinder == null) {
            LogUtil.e(TAG, "msgBinder is null!!!");
        } else {
            publishBinder.sendLivePhotoMsg(livePhotoMsg);
        }
    }
    /**
     * 图片/视频发布结果回调：成功或失败后切换到主线程更新视图。
     */
    private final class VideoOrPhotoPublishCallback implements PublishVideoOrPhotoCallback {

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onPublishSuccess(final DbMoment dbMoment) {
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishPresenter.this.getView() != null) {
                        PublishPresenter.this.getView().publishSuccess(dbMoment);
                    } else {
                        EventBus.getDefault().post(new EventData(5, dbMoment));
                    }
                }
            });
        }

        @Override
        public void onFail(final Throwable throwable) {
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishPresenter.this.getView() == null) {
                        return;
                    }
                    if (ERROR_CODE_PUBLISH_LIMITED.equals(throwable.getMessage())) {
                        PublishPresenter.this.getView().publishLimited();
                    } else if (ERROR_CODE_VIDEO_INVALID.equals(throwable.getMessage())) {
                        PublishPresenter.this.getView().publishInvalidate();
                    } else {
                        LogUtil.e(TAG, " 发布失败 ");
                        PublishPresenter.this.getView().publishFail(throwable.getMessage());
                    }
                }
            });
        }
    }

    public void publishMoment(PhotoMsg photoMsg, VideoTokenVoResponse videoTokenVoResponse,
                              FriendsVisibleBean visibleBean) {
        PubVideoOrPhotoServer.getInstance().publishMoment(photoMsg, videoTokenVoResponse,
                new VideoOrPhotoPublishCallback(), visibleBean);
    }

    public void publishMoment(LivePhotoMsg livePhotoMsg) {
        LogUtil.d(TAG, "publishMoment#photoMsg:" + livePhotoMsg.toString());
        String livePhotoData = JSONUtil.toJSON(livePhotoMsg);
        this.momentServe.publishMoment(5, livePhotoMsg.getSource().getKey(), 0, livePhotoData,
                        Arrays.asList(5, 22))
                .subscribeOn(Schedulers.io())
                .subscribe(new LivePhotoPublishSubscriber(livePhotoMsg, livePhotoData));
    }

    /**
     * 实况照片发布结果订阅者。
     */
    private final class LivePhotoPublishSubscriber extends Subscriber<Moment> {

        private final LivePhotoMsg photoMsg;
        private final String livePhotoData;

        LivePhotoPublishSubscriber(LivePhotoMsg photoMsg, String livePhotoData) {
            this.photoMsg = photoMsg;
            this.livePhotoData = livePhotoData;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(final Throwable throwable) {
            LogUtil.e(TAG, "Publish ErrorMessage =" + throwable.getMessage());
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishPresenter.this.getView() == null) {
                        return;
                    }
                    if (ERROR_CODE_PUBLISH_LIMITED.equals(throwable.getMessage())) {
                        PublishPresenter.this.getView().publishLimited();
                    } else if (ERROR_CODE_VIDEO_INVALID.equals(throwable.getMessage())) {
                        PublishPresenter.this.getView().publishInvalidate();
                    } else {
                        LogUtil.e(TAG, " 发布失败 ");
                        PublishPresenter.this.getView().publishFail(throwable.getMessage());
                    }
                }
            });
        }

        @Override
        public void onNext(Moment moment) {
            if (moment == null) {
                LogUtil.e(TAG, "moment is null");
                return;
            }
            LogUtil.d(TAG, "moment:" + moment.getMomentId());
            DigitalManager.getInstance().getDigitalEntity().momentContent = this.photoMsg.getSource().getKey();
            final DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment, LIVE_PHOTO_DB_TYPE);
            dbMoment.setContent(this.livePhotoData);
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishPresenter.this.getView() != null) {
                        PublishPresenter.this.getView().publishSuccess(dbMoment);
                    } else {
                        EventBus.getDefault().post(new EventData(5, dbMoment));
                    }
                }
            });
            EventBus.getDefault().post(dbMoment);
            PublishPresenter.this.momentServe.insertMomentByMomentId(dbMoment);
            LogUtil.d(TAG, "dbMoment:" + dbMoment.getMomentId());
        }
    }
    public void sendVideoMsg(boolean fromAlbum, PublishService.PublishBinder publishBinder,
                             String videoPath, String videoText) {
        sendVideoMsg(fromAlbum, publishBinder, videoPath, videoText, null, null);
    }

    public void sendVideoMsg(final boolean fromAlbum, final PublishService.PublishBinder publishBinder,
                             final String videoPath, final String videoText, final PoiBean poiBean,
                             final FriendsVisibleBean visibleBean) {
        final String videoName = ChatVideoUtil.getVideoName(videoPath);
        String thumbnailPath = (fromAlbum ? FileManager.getVideoThumnailDir()
                : FileManager.getThirdVideoThumnailPath()) + videoName + FileManager.WEBP_FORMAT;
        if (publishBinder == null) {
            LogUtil.w(TAG, "sendVideoMsg publishBinder = null");
            return;
        }
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(MOMENT_TYPE_VIDEO);
        LogUtil.d(TAG, "sendVideoMsg: " + thumbnailPath);
        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailExists = thumbnailFile.isFile() && thumbnailFile.exists();
        LogUtil.d(TAG, "sendVideoMsg thumbnailPath exit : " + thumbnailExists);
        if (thumbnailExists || !TextUtils.isEmpty(this.loadingTokenVideoName)) {
            sendVideoMsg(publishBinder, thumbnailPath, fromAlbum, videoName, null, videoText,
                    poiBean, visibleBean);
            return;
        }
        final long saveThumbnailStartTime = SystemClock.elapsedRealtime();
        Observable.just(thumbnailPath)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String savePath) {
                        return ChatVideoUtil.saveThumnail(videoPath, savePath,
                                ScreenUtils.screenWidth(context), ScreenUtils.screenHeight(context));
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.i(TAG, "saveThumnailAndShowComplete onError:" + throwable);
                        DigitalBigDateSender.onPublicResult(context,
                                DigitalManager.getInstance().getDigitalEntity(), false,
                                DigitalConstant.ErrorCode.SAVE_THUMBNAIL_ERROR, throwable);
                    }

                    @Override
                    public void onNext(String savedPath) {
                        DigitalManager.getInstance().getDigitalEntity().saveThumnailTime =
                                String.valueOf(SystemClock.elapsedRealtime() - saveThumbnailStartTime);
                        PublishPresenter.this.sendVideoMsg(publishBinder, thumbnailPath, fromAlbum,
                                videoName, null, videoText, poiBean, visibleBean);
                    }
                });
    }

    private void sendVideoMsg(PublishService.PublishBinder publishBinder, String thumbnailPath,
                              boolean fromAlbum, String videoName, ShareVideoMoment shareVideoMoment,
                              String videoText, PoiBean poiBean, FriendsVisibleBean visibleBean) {
        if (publishBinder == null) {
            return;
        }
        if (TextUtils.isEmpty(videoName)) {
            LogUtil.d(TAG, "sendVideoMsg videoName is empty");
            return;
        }
        if (!TextUtils.isEmpty(this.loadingTokenVideoName)
                && Objects.equals(this.loadingTokenVideoName, videoName.replace(FileManager.MP4_FORMAT, ""))) {
            LogUtil.d(TAG, "sendVideoMsg wait token ");
            if (isViewAttached() && getView() != null) {
                getView().showLoading();
            }
            this.sendVideoParam = new SendVideoParam(thumbnailPath, videoName, fromAlbum, videoText,
                    poiBean, shareVideoMoment);
            return;
        }
        this.sendVideoParam = new SendVideoParam(thumbnailPath, videoName, fromAlbum, videoText, poiBean,
                shareVideoMoment, this.videoTokenVoResponse, visibleBean);
        toSendVideoInBackground(this.sendVideoParam);
    }

    public void sendShareVideoMsg(final boolean fromAlbum, final PublishService.PublishBinder publishBinder,
                                  final String videoPath, String packageName, String funVideoParamJson,
                                  byte[] appIcon, String appName, long videoLength,
                                  final String videoText, final PoiBean poiBean,
                                  final FriendsVisibleBean visibleBean) {
        LogUtil.d(TAG, "sendShareVideoMsg: " + videoPath);
        final String thumbnailPath = FileManager.getVideoThumnailDir()
                + ChatVideoUtil.getVideoName(videoPath) + FileManager.WEBP_FORMAT;
        final ShareVideoMoment shareVideoMoment = new ShareVideoMoment();
        shareVideoMoment.setLocalThumbnailPath(thumbnailPath);
        shareVideoMoment.setLocalVideoPath(videoPath);
        LogUtil.d(TAG, "sendShareVideoMsg: " + thumbnailPath);
        shareVideoMoment.setAppIcon(appIcon);
        shareVideoMoment.setAppName(appName);
        shareVideoMoment.setPackageName(packageName);
        shareVideoMoment.setVideoLength(videoLength);
        shareVideoMoment.setFunVideoParam((ShareVideoMoment.FunVideoParam) JSONUtil.fromJSON(
                funVideoParamJson, ShareVideoMoment.FunVideoParam.class));
        File thumbnailFile = new File(thumbnailPath);
        LogUtil.e(TAG, "sendShareVideoMsg: " + shareVideoMoment + shareVideoMoment.getVideoLength());
        if ((thumbnailFile.isFile() && thumbnailFile.exists())
                || !TextUtils.isEmpty(this.loadingTokenVideoName)) {
            sendVideoMsg(publishBinder, thumbnailPath, fromAlbum, videoPath, shareVideoMoment,
                    videoText, poiBean, visibleBean);
            return;
        }
        Observable.just(thumbnailPath)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String savePath) {
                        return ChatVideoUtil.saveThumnail(videoPath, savePath,
                                ScreenUtils.screenWidth(context), ScreenUtils.screenHeight(context));
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.i(TAG, "saveThumnailAndShowComplete onError:" + throwable);
                    }

                    @Override
                    public void onNext(String savedPath) {
                        PublishPresenter.this.sendVideoMsg(publishBinder, thumbnailPath, fromAlbum,
                                videoPath, shareVideoMoment, videoText, poiBean, visibleBean);
                    }
                });
    }

    /**
     * 提前保存视频缩略图并预取上传 token。
     */
    public void preSaveThumbnailAndToken(String videoPath, boolean isFunVideo, boolean fromAlbum,
                                         final PublishService.PublishBinder publishBinder) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        boolean saveToAlbumDir = fromAlbum;
        if (isFunVideo) {
            LogUtil.d(TAG, "preSaveThumbnail isFunVideo");
            saveToAlbumDir = true;
        }
        final String videoName = ChatVideoUtil.getVideoName(videoPath);
        this.loadingTokenVideoName = videoName;
        String thumbnailPath = (saveToAlbumDir ? FileManager.getVideoThumnailDir()
                : FileManager.getThirdVideoThumnailPath()) + videoName + FileManager.WEBP_FORMAT;
        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailExists = thumbnailFile.isFile() && thumbnailFile.exists();
        LogUtil.d(TAG, "preSaveThumbnail thumbnailPath exit : " + thumbnailExists);
        if (!thumbnailExists) {
            ChatVideoUtil.saveThumnail(videoPath, thumbnailPath, ScreenUtils.screenWidth(this.context),
                    ScreenUtils.screenHeight(this.context));
        }
        if (publishBinder == null || publishBinder.getService() == null) {
            LogUtil.d(TAG, "preLoadVideoToken: publishBinder is null");
            this.loadingTokenVideoName = null;
            return;
        }
        VideoTokenVoResponse cachedToken = this.videoTokenVoResponse;
        if (cachedToken != null && Objects.equals(cachedToken.getVideoName(), videoName)) {
            LogUtil.d(TAG, "preLoadVideoToken: already have token = [" + videoName + "]");
            this.loadingTokenVideoName = null;
            return;
        }
        publishBinder.getService().getUploadVideoToken(thumbnailPath, videoName, isFunVideo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoTokenVoResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "preLoadVideoToken#onError", throwable);
                        PublishPresenter.this.loadingTokenVideoName = null;
                        if (PublishPresenter.this.sendVideoParam != null) {
                            PublishPresenter.this.sendVideoParam = null;
                            publishBinder.getService().onFailCallback(throwable);
                        }
                    }

                    @Override
                    public void onNext(VideoTokenVoResponse result) {
                        if (result == null) {
                            LogUtil.d(TAG, "preLoadVideoToken onNext result is null");
                            return;
                        }
                        LogUtil.d(TAG, "preLoadVideoToken onNext");
                        PublishPresenter.this.loadingTokenVideoName = null;
                        PublishPresenter.this.videoTokenVoResponse = result;
                        PublishPresenter.this.videoTokenVoResponse.setVideoName(videoName);
                        if (PublishPresenter.this.sendVideoParam == null
                                || !Objects.equals(videoName,
                                PublishPresenter.this.sendVideoParam.getVideoName()
                                        .replace(FileManager.MP4_FORMAT, ""))) {
                            return;
                        }
                        LogUtil.d(TAG, "sendVideoMsg continue");
                        if (PublishPresenter.this.isViewAttached()
                                && PublishPresenter.this.getView() != null) {
                            PublishPresenter.this.getView().dismissLoading();
                        }
                        PublishPresenter.this.sendVideoParam.setVideoTokenVoResponse(
                                PublishPresenter.this.videoTokenVoResponse);
                        PublishPresenter.this.toSendVideoInBackground(
                                PublishPresenter.this.sendVideoParam);
                        PublishPresenter.this.sendVideoParam = null;
                    }
                });
    }

    private void toSendVideoInBackground(SendVideoParam sendVideoParam) {
        LogUtil.d(TAG, "toSendVideoInBackground");
        if (!NetworkUtils.isConnected(MomentApp.getAppContext())) {
            ToastUtil.showShort(MomentApp.getAppContext(), R.string.net_work_exception);
            return;
        }
        if (MomentApp.isSendingVideo()) {
            ToastUtil.showShort(MomentApp.getAppContext(), R.string.sending_video_tip);
            return;
        }
        EventBus.getDefault().post(sendVideoParam);
        if (!isViewAttached() || getView() == null) {
            return;
        }
        getView().toMomentActivity();
    }
    public void publishMoment(PoiBean poiBean, FriendsVisibleBean visibleBean) {
        long startPublishTime = SystemClock.elapsedRealtime();
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(MOMENT_TYPE_LOCATION);
        this.momentServe.publishMoment(MOMENT_TYPE_LOCATION, null, 0, poiBean.getAddressDesc(),
                        poiBean, visibleBean)
                .subscribeOn(Schedulers.io())
                .subscribe(new LocationPublishSubscriber(startPublishTime, visibleBean));
    }

    /**
     * 位置动态发布结果订阅者。
     */
    private final class LocationPublishSubscriber extends Subscriber<Moment> {

        private final long startPublishTime;
        private final FriendsVisibleBean visibleBean;

        LocationPublishSubscriber(long startPublishTime, FriendsVisibleBean visibleBean) {
            this.startPublishTime = startPublishTime;
            this.visibleBean = visibleBean;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(final Throwable throwable) {
            LogUtil.e(TAG, "publishMoment ErrorMessage =" + throwable.getMessage());
            DigitalBigDateSender.onPublicResult(context, DigitalManager.getInstance().getDigitalEntity(),
                    false, PUBLIC_RESULT_ERROR_CODE, throwable);
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishPresenter.this.getView() == null) {
                        return;
                    }
                    if (ERROR_CODE_PUBLISH_LIMITED.equals(throwable.getMessage())) {
                        PublishPresenter.this.getView().publishLimited();
                    } else if (ERROR_CODE_PUBLISH_INVALID.equals(throwable.getMessage())) {
                        PublishPresenter.this.getView().publishInvalidate();
                    } else {
                        PublishPresenter.this.getView().publishFail(throwable.getMessage());
                    }
                }
            });
        }

        @Override
        public void onNext(Moment moment) {
            long transformStartTime = SystemClock.elapsedRealtime();
            DigitalManager.getInstance().getDigitalEntity().publicTime =
                    String.valueOf(transformStartTime - this.startPublishTime);
            final DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
            if (this.visibleBean != null) {
                dbMoment.setPermissionType(this.visibleBean.getType());
                MomentBehavior.upLoadVisibleRange(context, 1, this.visibleBean.getType());
            }
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (PublishPresenter.this.getView() != null) {
                        PublishPresenter.this.getView().publishSuccess(dbMoment);
                    }
                }
            });
            PublishPresenter.this.momentServe.insertMomentByMomentId(dbMoment);
            DigitalManager.getInstance().getDigitalEntity().transformTime =
                    String.valueOf(SystemClock.elapsedRealtime() - transformStartTime);
            DigitalBigDateSender.onPublicResult(context,
                    DigitalManager.getInstance().getDigitalEntity(), true, "", null);
            EventBus.getDefault().post(dbMoment);
        }
    }

    public AITextConfig getAiTextConfig() {
        if (this.aiTextConfig == null) {
            this.aiTextConfig = new AITextConfig.AITextConfigBuilder().clientType(2).build();
        }
        return this.aiTextConfig;
    }
}