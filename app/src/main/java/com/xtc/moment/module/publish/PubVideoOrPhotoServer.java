package com.xtc.moment.module.publish;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.behavior.DigitalBigDateSender;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.service.PublishVideoOrPhotoCallback;
import com.xtc.moment.third.bean.PushVideoBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.utils.encode.JSONUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 视频/图片动态发布服务：统一处理单图、多图、短视频、分享视频以及视频文本等
 * 各类发布类型，发布成功后落库并回调。
 */
public class PubVideoOrPhotoServer {

    private static final AtomicReference<PubVideoOrPhotoServer> INSTANCE = new AtomicReference<>();
    private static final String TAG = "PubVideoOrPhotoServer";

    /** 图片动态类型。 */
    private static final int DIALOG_TYPE_PHOTO = 5;
    /** 短视频动态类型。 */
    private static final int DIALOG_TYPE_SHORT_VIDEO = 6;
    /** 分享视频类型。 */
    private static final int DIALOG_TYPE_SHARE_VIDEO = 24;
    /** 多图动态类型。 */
    private static final int DIALOG_TYPE_MULTI_PHOTO = 26;
    /** 视频文本动态类型。 */
    private static final int DIALOG_TYPE_VIDEO_TEXT = 27;
    /** 多图分享类型。 */
    private static final int DIALOG_TYPE_MULTI_PHOTO_SHARE = 28;
    /** 无法识别的发布类型。 */
    private static final int DIALOG_TYPE_UNKNOWN = -3;
    /** 发布失败埋点使用的错误码。 */
    private static final String PUBLIC_RESULT_ERROR_CODE = "9005";

    private final Context context = MomentApp.getAppContext();
    private final IMomentServe momentServe = MomentServeImpl.getInstance(this.context);
    private WeakReference<PublishVideoOrPhotoCallback> publishVideoCallbackReference;

    public static PubVideoOrPhotoServer getInstance() {
        PubVideoOrPhotoServer instance;
        do {
            PubVideoOrPhotoServer current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            instance = new PubVideoOrPhotoServer();
        } while (!INSTANCE.compareAndSet(null, instance));
        return instance;
    }

    /**
     * 发布视频或图片动态。
     *
     * @param photoMsg        图片动态数据（与 videoTokenVoResponse 二选一）
     * @param videoTokenVoResponse 视频动态数据（与 photoMsg 二选一）
     * @param callback        发布结果回调
     * @param visibleBean     可见范围
     */
    public void publishMoment(final PhotoMsg photoMsg, final VideoTokenVoResponse videoTokenVoResponse,
                              final PublishVideoOrPhotoCallback callback,
                              final FriendsVisibleBean visibleBean) {
        PoiBean poiBean;
        int dialogType;
        String packageName;
        String content;
        String resourceKey = "";
        if (photoMsg != null && photoMsg.getDialogType() == DIALOG_TYPE_PHOTO) {
            LogUtil.i(TAG, "publishMoment  photoMsg " + photoMsg);
            String sourceKey = photoMsg.getSource().getKey();
            int photoDialogType = photoMsg.getDialogType();
            content = JSONUtil.toJSON(photoMsg);
            poiBean = photoMsg.getPoiBean();
            packageName = "";
            resourceKey = sourceKey;
            dialogType = photoDialogType;
        } else if (videoTokenVoResponse != null
                && videoTokenVoResponse.getDialogType() != DIALOG_TYPE_VIDEO_TEXT) {
            PoiBean videoPoiBean = videoTokenVoResponse.getPoiBean();
            if (videoTokenVoResponse.getDialogType() == DIALOG_TYPE_SHORT_VIDEO) {
                String publishContent = JSONUtil.toJSON(
                        videoTokenVoResponse.toPublishContent(videoTokenVoResponse));
                int videoDialogType = videoTokenVoResponse.getDialogType();
                VideoKeyOrToken videoKeyOrToken = buildVideoKeyOrToken(
                        videoTokenVoResponse.getIcon().getKey(),
                        videoTokenVoResponse.getSource().getKey(),
                        videoDialogType, publishContent, null);
                LogUtil.i(TAG, "publishMoment setTrackMd5Value " + publishContent);
                String tokenJson = JSONUtil.toJSON(videoKeyOrToken);
                LogUtil.i(TAG, "videoMsg" + videoKeyOrToken.getContent());
                packageName = "";
                poiBean = videoPoiBean;
                resourceKey = tokenJson;
                content = publishContent;
                dialogType = videoDialogType;
            } else if (videoTokenVoResponse.getDialogType() == DIALOG_TYPE_SHARE_VIDEO) {
                PoiBean shareVideoPoiBean = videoTokenVoResponse.getPoiBean();
                int shareVideoDialogType = videoTokenVoResponse.getDialogType();
                String shareTokenJson = JSONUtil.toJSON(buildVideoKeyOrToken(
                        videoTokenVoResponse.getIcon().getKey(),
                        videoTokenVoResponse.getSource().getKey(),
                        shareVideoDialogType, videoTokenVoResponse.getContent(),
                        videoTokenVoResponse.getSource().getDownloadUrl()));
                String shareFlags = videoTokenVoResponse.getCustomParamMap()
                        .get(ShareVideoMoment.SHARE_VIDEO_FLAGS);
                videoTokenVoResponse.setCustomParamMap(null);
                videoTokenVoResponse.setUploadToken("");
                videoTokenVoResponse.setIconUploadToken("");
                ShareVideoMoment shareVideoMoment = (ShareVideoMoment) JSONUtil.fromJSON(
                        shareFlags, ShareVideoMoment.class);
                packageName = shareVideoMoment != null ? shareVideoMoment.getPackageName() : "";
                poiBean = shareVideoPoiBean;
                dialogType = shareVideoDialogType;
                resourceKey = shareTokenJson;
                content = shareFlags;
            } else {
                packageName = "";
                poiBean = videoPoiBean;
                dialogType = DIALOG_TYPE_UNKNOWN;
                content = packageName;
            }
        } else if (photoMsg != null) {
            PoiBean multiPhotoPoiBean = photoMsg.getPoiBean();
            if (photoMsg.getDialogType() == DIALOG_TYPE_MULTI_PHOTO) {
                String photoContent = photoMsg.getContent();
                CloudFileResource source = photoMsg.getSource();
                String sourceKey = source.getKey();
                source.setKey(null);
                int multiPhotoDialogType = photoMsg.getDialogType();
                LogUtil.i(TAG, "多图类型" + photoContent);
                MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(
                        photoContent, MultiPhotoContent.class);
                if (multiPhotoContent != null) {
                    multiPhotoContent.setResource(source);
                    content = JSONUtil.toJSON(multiPhotoContent);
                } else {
                    content = photoMsg.getContent();
                }
                poiBean = multiPhotoPoiBean;
                dialogType = multiPhotoDialogType;
                packageName = "";
                resourceKey = sourceKey;
            } else {
                if (photoMsg.getDialogType() == DIALOG_TYPE_MULTI_PHOTO_SHARE) {
                    LogUtil.i(TAG, "多图分享" + photoMsg.getContent());
                }
                poiBean = multiPhotoPoiBean;
                content = "";
                packageName = "";
                dialogType = DIALOG_TYPE_UNKNOWN;
            }
        } else if (videoTokenVoResponse == null
                || videoTokenVoResponse.getDialogType() != DIALOG_TYPE_VIDEO_TEXT) {
            poiBean = null;
            content = "";
            packageName = "";
            dialogType = DIALOG_TYPE_UNKNOWN;
        } else {
            dialogType = videoTokenVoResponse.getDialogType();
            PoiBean videoTextPoiBean = videoTokenVoResponse.getPoiBean();
            VideoKeyOrToken videoKeyOrToken = buildVideoKeyOrToken(
                    videoTokenVoResponse.getIcon().getKey(),
                    videoTokenVoResponse.getSource().getKey(),
                    dialogType,
                    JSONUtil.toJSON(videoTokenVoResponse.toPublishContent(videoTokenVoResponse)),
                    null);
            String videoContent = buildVideoContent(videoTokenVoResponse.getContent(),
                    JSONUtil.toJSON(videoTokenVoResponse));
            String videoTokenJson = JSONUtil.toJSON(videoKeyOrToken);
            LogUtil.i(TAG, "视频文本类型 " + videoKeyOrToken.getContent());
            packageName = "";
            poiBean = videoTextPoiBean;
            content = videoContent;
            resourceKey = videoTokenJson;
        }
        LogUtil.d(TAG, "publishMoment#photoMsg:" + photoMsg + " #videoMsg: " + videoTokenVoResponse);
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(dialogType);
        final long startPublishTime = SystemClock.elapsedRealtime();
        final String publishResourceKey = resourceKey;
        final int publishDialogType = dialogType;
        final String publishContent = content;
        this.momentServe.publishMoment(dialogType, resourceKey, 0, content, packageName, poiBean, visibleBean)
                .map(new Func1<Moment, DbMoment>() {
                    @Override
                    public DbMoment call(Moment moment) {
                        long transformStartTime = SystemClock.elapsedRealtime();
                        DigitalManager.getInstance().getDigitalEntity().publicTime =
                                String.valueOf(transformStartTime - startPublishTime);
                        LogUtil.d(TAG, "moment:" + moment.getMomentId());
                        DigitalManager.getInstance().getDigitalEntity().momentContent = publishResourceKey;
                        DbMoment dbMoment = BeanConverterUtil.convertToDbMoment(moment);
                        if (DIALOG_TYPE_SHORT_VIDEO == publishDialogType) {
                            dbMoment.setContent(JSONUtil.toJSON(videoTokenVoResponse));
                            LogUtil.i(TAG, "setContent" + dbMoment.getContent());
                            PushVideoBean pushVideoBean = new PushVideoBean();
                            pushVideoBean.setType(String.valueOf(2));
                            pushVideoBean.setWatchId(moment.getWatchId());
                            MomentBehavior.sendVideoAndSuccessSend(context, pushVideoBean);
                        } else if (publishDialogType == DIALOG_TYPE_MULTI_PHOTO) {
                            LogUtil.i(TAG, "publishMoment1  photoMsg ");
                            MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(
                                    publishContent, MultiPhotoContent.class);
                            multiPhotoContent.setLocalPaths(photoMsg.getLocalPath());
                            dbMoment.setContent(JSONUtil.toJSON(multiPhotoContent));
                        } else if (DIALOG_TYPE_SHARE_VIDEO == publishDialogType) {
                            PushVideoBean pushVideoBean = new PushVideoBean();
                            pushVideoBean.setType(String.valueOf(3));
                            pushVideoBean.setWatchId(moment.getWatchId());
                            MomentBehavior.sendVideoAndSuccessSend(context, pushVideoBean);
                        } else if (publishDialogType == DIALOG_TYPE_VIDEO_TEXT) {
                            dbMoment.setPublishContent(JSONUtil.toJSON(videoTokenVoResponse));
                            LogUtil.i(TAG, "video text " + JSONUtil.toJSON(videoTokenVoResponse));
                        } else if (DIALOG_TYPE_PHOTO == publishDialogType) {
                            dbMoment.setContent(JSONUtil.toJSON(photoMsg));
                        }
                        if (dbMoment.getEmotionId() != 0 && TextUtils.isEmpty(dbMoment.getMomentBgPath())) {
                            DbMomentPrerogativeBackground background =
                                    MomentPrerogativeServeImpl.getInstance(context)
                                            .getPrerogativeBackgroundByEmotionId(dbMoment.getEmotionId());
                            if (background != null && !TextUtils.isEmpty(background.getLocalEmotionPath())) {
                                dbMoment.setMomentBgPath(background.getLocalEmotionPath());
                            }
                            LogUtil.i(TAG, "updateLocalMoment" + background.getLocalEmotionPath());
                        }
                        if (callback != null) {
                            callback.onPublishSuccess(dbMoment);
                        }
                        if (videoTokenVoResponse != null) {
                            PubVideoOrPhotoServer.this.onPublishVideoSuccess(dbMoment);
                        }
                        PubVideoOrPhotoServer.this.momentServe.insertMomentByMomentId(dbMoment);
                        LogUtil.i(TAG, "iMomentServe " + dbMoment.getPublishContent());
                        DigitalManager.getInstance().getDigitalEntity().transformTime =
                                String.valueOf(SystemClock.elapsedRealtime() - transformStartTime);
                        return dbMoment;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<DbMoment>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "Publish ErrorMessage =" + throwable.getMessage());
                        DigitalBigDateSender.onPublicResult(context,
                                DigitalManager.getInstance().getDigitalEntity(), false,
                                PUBLIC_RESULT_ERROR_CODE, throwable);
                        if (callback != null) {
                            callback.onFail(throwable);
                        }
                        if (videoTokenVoResponse != null) {
                            PubVideoOrPhotoServer.this.onPublishVideoFail(throwable);
                        }
                    }

                    @Override
                    public void onNext(DbMoment dbMoment) {
                        if (visibleBean != null) {
                            dbMoment.setPermissionType(visibleBean.getType());
                            MomentBehavior.upLoadVisibleRange(context, 1, visibleBean.getType());
                        }
                        LogUtil.d(TAG, "onNext");
                        DigitalBigDateSender.onPublicResult(context,
                                DigitalManager.getInstance().getDigitalEntity(), true, "", null);
                        EventBus.getDefault().post(dbMoment);
                    }
                });
    }

    public void onPublishVideoSuccess(DbMoment dbMoment) {
        MomentApp.setIsSendingVideo(false);
        PublishVideoOrPhotoCallback callback = getPublishVideoCallback();
        if (callback == null) {
            return;
        }
        callback.onPublishSuccess(dbMoment);
    }

    public void onPublishVideoFail(Throwable throwable) {
        MomentApp.setIsSendingVideo(false);
        PublishVideoOrPhotoCallback callback = getPublishVideoCallback();
        if (callback == null) {
            return;
        }
        callback.onFail(throwable);
    }

    public void onPublishVideoProgress(int progress) {
        PublishVideoOrPhotoCallback callback = getPublishVideoCallback();
        if (callback == null) {
            return;
        }
        callback.onProgress(progress);
    }

    public void setPublishVideoOrPhotoCallback(PublishVideoOrPhotoCallback callback) {
        if (callback == null) {
            return;
        }
        LogUtil.d(TAG, "setPublishVideoOrPhotoCallback");
        this.publishVideoCallbackReference = new WeakReference<>(callback);
    }

    public void removePublishVideoOrPhotoCallback(PublishVideoOrPhotoCallback callback) {
        WeakReference<PublishVideoOrPhotoCallback> reference = this.publishVideoCallbackReference;
        if (callback == null || reference == null) {
            return;
        }
        if (reference.get() != callback) {
            LogUtil.d(TAG, "removePublishVideoOrPhotoCallback: 已经被新回调覆盖");
        } else {
            LogUtil.d(TAG, "removePublishVideoOrPhotoCallback");
            this.publishVideoCallbackReference = null;
        }
    }

    private PublishVideoOrPhotoCallback getPublishVideoCallback() {
        WeakReference<PublishVideoOrPhotoCallback> reference = this.publishVideoCallbackReference;
        if (reference == null) {
            return null;
        }
        return reference.get();
    }

    private String buildVideoContent(String content, String videoJson) {
        MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(
                content, MultiPhotoContent.class);
        multiPhotoContent.setVideoMsgContent(videoJson);
        return JSONUtil.toJSON(multiPhotoContent);
    }

    private VideoKeyOrToken buildVideoKeyOrToken(String picKey, String videoKey, int dialogType,
                                                 String content, String loadPath) {
        VideoKeyOrToken videoKeyOrToken = new VideoKeyOrToken();
        videoKeyOrToken.setPicKey(picKey);
        videoKeyOrToken.setVideoKey(videoKey);
        videoKeyOrToken.setDialogType(dialogType);
        videoKeyOrToken.setContent(content);
        videoKeyOrToken.setLoadPath(loadPath);
        return videoKeyOrToken;
    }
}