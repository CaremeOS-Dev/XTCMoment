package com.xtc.moment.serve.impl;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.log.LogUtil;
import com.xtc.moment.behavior.QiNiuThrowable;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.PhotoTokenVo;
import com.xtc.moment.module.bean.ShareAppMoment;
import com.xtc.moment.module.bean.ShareAppPublish;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.bean.ShareImagePublish;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.module.bean.ShareWebPublish;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.net.MomentPhotoServeHttpProxy;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.serve.IMomentPhotoServe;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.Utils;
import com.xtc.qiniu.ICloudManager;
import com.xtc.qiniu.ICloudService;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 动态图片服务实现：图片上传、下载地址换取与动态内容回填。
 */
public class MomentPhotoServeImpl implements IMomentPhotoServe {

    private static final String TAG = MomentPhotoServeImpl.class.getSimpleName();

    public Context context;
    private MomentPhotoServeHttpProxy momentPhotoServeHttpProxy;
    private IMomentServe momentServe;

    public MomentPhotoServeImpl(Context context) {
        this.context = context;
        this.momentPhotoServeHttpProxy = new MomentPhotoServeHttpProxy(context);
        this.momentServe = MomentServeImpl.getInstance(context);
    }

    @Override
    public Observable<PhotoTokenVo> getUploadToken(PhotoTokenParam param) {
        return this.momentPhotoServeHttpProxy.getUploadToken(param);
    }

    @Override
    public Observable<List<PhotoTokenVo>> getUploadTokens(List<PhotoTokenParam> paramList) {
        return this.momentPhotoServeHttpProxy.getUploadTokens(paramList);
    }

    @Override
    public Observable<PhotoTokenVo> uploadPhoto(final String localPath, final PhotoTokenVo photoTokenVo) {
        final String uploadToken = photoTokenVo.getUploadToken();
        final String key = photoTokenVo.getSource().getKey();
        return Observable.create(new Observable.OnSubscribe<PhotoTokenVo>() {
            @Override
            public void call(final Subscriber<? super PhotoTokenVo> subscriber) {
                if (TextUtils.isEmpty(uploadToken) || TextUtils.isEmpty(key)) {
                    subscriber.onError(new Throwable("token or key is null"));
                    return;
                }
                ICloudService.uploadFile(ContextUtils.getContext(), localPath, key, uploadToken, new ICloudManager.OnUpLoadListener() {
                    @Override
                    public void onProgress(String key2, double percent) {
                    }

                    @Override
                    public void onSuccess(String key2) {
                        subscriber.onNext(photoTokenVo);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(String key2, int code, String message) {
                        subscriber.onError(new QiNiuThrowable("upload fail: " + message, code));
                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<String> getDownloadUrl(FileUrlParam param, final PhotoMsg photoMsg, final DbMoment moment) {
        return this.momentPhotoServeHttpProxy.getDownloadUrl(param).map(new Func1<DownloadUrlVo, String>() {
            @Override
            public String call(DownloadUrlVo downloadUrlVo) {
                List<CloudFileResource> urls = downloadUrlVo.getUrls();
                if (urls == null || urls.size() <= 0) {
                    return null;
                }
                CloudFileResource resource = urls.get(0);
                String downloadUrl = resource.getDownloadUrl();
                if (TextUtils.isEmpty(downloadUrl)) {
                    return null;
                }
                photoMsg.getSource().setDownloadUrl(downloadUrl);
                photoMsg.getSource().setUrlDeadline(resource.getUrlDeadline());
                moment.setContent(JSONUtil.toJSON(photoMsg));
                MomentPhotoServeImpl.this.updatePhotoMsgToDB(photoMsg, false, moment);
                return downloadUrl;
            }
        });
    }

    @Override
    public Observable<String> getDownloadUrl(final FileUrlParam param, final DbMoment moment) {
        return this.momentPhotoServeHttpProxy.getDownloadUrl(param).map(new Func1<DownloadUrlVo, String>() {
            @Override
            public String call(DownloadUrlVo downloadUrlVo) {
                List<CloudFileResource> urls = downloadUrlVo.getUrls();
                if (urls == null || urls.size() <= 0) {
                    return null;
                }
                CloudFileResource resource = urls.get(0);
                LogUtil.d(MomentPhotoServeImpl.TAG, "fileResource:  " + resource);
                String downloadUrl = resource.getDownloadUrl();
                if (TextUtils.isEmpty(downloadUrl)) {
                    return null;
                }
                if (moment.getType().intValue() == 24) {
                    MomentPhotoServeImpl.this.getVideoMsg(resource, param, moment);
                } else if (moment.getType().intValue() != 27) {
                    MomentPhotoServeImpl.this.updatePhotoMsgToDB(MomentPhotoServeImpl.this.getPhotoMsg(resource, param, moment), false, moment);
                } else {
                    VideoMsg videoMsg = new VideoMsg();
                    videoMsg.setIcon(resource);
                    moment.setPublishContent(JSONUtil.toJSON(videoMsg));
                    MomentPhotoServeImpl.this.setVideoContent(moment, videoMsg);
                }
                return downloadUrl;
            }
        });
    }

    private void getVideoMsg(CloudFileResource resource, FileUrlParam param, DbMoment moment) {
        LogUtil.e(TAG, "getVideoMsg: " + resource.getDownloadUrl());
        if (moment.getType().intValue() == 24) {
            ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class);
            shareVideoMoment.setIcon(resource);
            shareVideoMoment.setType(param.getType());
            moment.setContent(JSONUtil.toJSON(shareVideoMoment));
        }
        this.momentServe.updateMoment(moment);
    }

    private PhotoMsg getPhotoMsg(CloudFileResource resource, FileUrlParam param, DbMoment moment) {
        PhotoMsg photoMsg = new PhotoMsg();
        if (moment.getType().intValue() == 9) {
            ShareAppPublish shareAppPublish = JSONUtil.fromJSON(moment.getContent(), ShareAppPublish.class);
            LogUtil.d(TAG, "share app Publish = " + shareAppPublish);
            photoMsg = convertShareAppMoment(shareAppPublish);
            LogUtil.d(TAG, "get app msg = " + photoMsg);
        } else if (moment.getType().intValue() == 8) {
            ShareImagePublish shareImagePublish = JSONUtil.fromJSON(moment.getContent(), ShareImagePublish.class);
            LogUtil.d(TAG, "share image Publish = " + shareImagePublish);
            photoMsg = convertShareImageMoment(shareImagePublish);
            LogUtil.d(TAG, "get photo msg = " + photoMsg);
        } else if (moment.getType().intValue() == 25) {
            ShareWebPublish shareWebPublish = JSONUtil.fromJSON(moment.getContent(), ShareWebPublish.class);
            LogUtil.d(TAG, "share web Publish = " + shareWebPublish);
            photoMsg = convertShareWebMoment(shareWebPublish);
            LogUtil.d(TAG, "get web msg = " + photoMsg);
        }
        String content;
        if (moment.getType().intValue() != 27 && moment.getType().intValue() != 26) {
            content = moment.getContent();
        } else {
            content = JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class).getContent();
        }
        LogUtil.i(TAG, "videoKeyOrToken.getContent()" + content);
        if (!TextUtils.isEmpty(content)) {
            try {
                PhotoMsg parsed = JSONUtil.fromJSON(content, PhotoMsg.class);
                if (parsed != null) {
                    photoMsg.setTrackMd5Value(parsed.getTrackMd5Value());
                    LogUtil.i(TAG, "photoMsg TrackMd5Value " + parsed.getTrackMd5Value() + " photoMsg toString" + photoMsg.toString());
                }
            } catch (Exception e) {
                LogUtil.d(TAG, "content Exception :" + e.toString());
            }
        }
        photoMsg.setSource(resource);
        photoMsg.setType(param.getType());
        if (moment.getType().intValue() != 27) {
            moment.setContent(JSONUtil.toJSON(photoMsg));
        } else {
            moment.setPublishContent(JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class).getContent());
        }
        return photoMsg;
    }

    private ShareImageMoment convertShareImageMoment(ShareImagePublish publish) {
        ShareImageMoment moment = new ShareImageMoment();
        moment.setAppIcon(publish.getAppIcon());
        moment.setTransaction(publish.getTransaction());
        moment.setPackageName(publish.getPackageName());
        moment.setAppName(publish.getAppName());
        moment.setDesc(publish.getDesc());
        moment.setTextMsg(publish.getTextMsg());
        moment.setMessageBitmapArgs(publish.getMessageBitmapArgs());
        moment.setDialogBitmapArgs(publish.getDialogBitmapArgs());
        moment.setTrackMd5Value(publish.getTrackMd5Value());
        return moment;
    }

    private ShareAppMoment convertShareAppMoment(ShareAppPublish publish) {
        ShareAppMoment moment = new ShareAppMoment();
        moment.setAppIcon(publish.getAppIcon());
        moment.setAction(publish.getAction());
        moment.setAppName(publish.getAppName());
        moment.setDesc(publish.getDesc());
        moment.setExtInfo(publish.getExtInfo());
        moment.setTargetClass(publish.getTargetClass());
        moment.setTargetPackage(publish.getTargetPackage());
        moment.setPackageName(publish.getPackageName());
        moment.setTransaction(publish.getTransaction());
        return moment;
    }

    private ShareWebMoment convertShareWebMoment(ShareWebPublish publish) {
        ShareWebMoment moment = new ShareWebMoment();
        moment.setAppIcon(publish.getAppIcon());
        moment.setAppName(publish.getAppName());
        moment.setDesc(publish.getDesc());
        moment.setPackageName(publish.getPackageName());
        moment.setTransaction(publish.getTransaction());
        moment.setWebLink(publish.getWebLink());
        moment.setVideoSource(publish.getVideoSource());
        moment.setVideoThumbSource(publish.getVideoThumbSource());
        return moment;
    }

    @Override
    public void updatePhotoMsgToDB(PhotoMsg photoMsg, boolean flag, DbMoment moment) {
        if (photoMsg == null) {
            LogUtil.d(TAG, "updatePhotoMsgToDB fail:photoMsg == null");
            return;
        }
        if (moment == null) {
            LogUtil.d(TAG, "updatePhotoMsgToDB fail:dbMoment == null");
            return;
        }
        int type = moment.getType().intValue();
        if (26 == type || 27 == type || 5 == type || 8 == type || 9 == type || 6 == type || 25 == type) {
            moment.setPublishContent(JSONUtil.toJSON(photoMsg));
            this.momentServe.updateMoment(moment);
        }
    }

    @Override
    public Observable<VideoTokenVoResponse> getUploadVideoToken(VideoTokenParam param) {
        return this.momentServe.getUploadVideoToken(param);
    }

    @Override
    public Observable<DownloadUrlVo> getDownloadBatchUrl(FileBatchUrlParam param, final DbMoment moment, final VideoMsg videoMsg) {
        return this.momentPhotoServeHttpProxy.getDownloadBatchUrl(param).map(new Func1<DownloadUrlVo, DownloadUrlVo>() {
            @Override
            public DownloadUrlVo call(DownloadUrlVo downloadUrlVo) {
                List<CloudFileResource> urls = downloadUrlVo.getUrls();
                if (urls == null || urls.size() <= 0 || moment == null) {
                    LogUtil.i(MomentPhotoServeImpl.TAG, " getDownloadBatchUrl momentBean = " + moment);
                } else {
                    VideoMsg target = videoMsg;
                    if (target == null) {
                        target = new VideoMsg();
                    }
                    for (int i = 0; i < urls.size(); i++) {
                        if (Utils.isIconKey(urls.get(i).getKey())) {
                            target.setIcon(urls.get(i));
                            target.setLocalThumbnailPath(urls.get(i).getDownloadUrl());
                        } else if (urls.get(i).getKey().contains(FileManager.MP4_FORMAT)) {
                            target.setSource(urls.get(i));
                            target.setLocalVideoPath(urls.get(i).getDownloadUrl());
                        }
                    }
                    if (moment.getType().intValue() == 24) {
                        ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class);
                        shareVideoMoment.setIcon(target.getIcon());
                        shareVideoMoment.setSource(target.getSource());
                        shareVideoMoment.setType("1");
                        moment.setContent(JSONUtil.toJSON(shareVideoMoment));
                    } else if (moment.getType().intValue() == 27) {
                        moment.setPublishContent(JSONUtil.toJSON(target));
                        MomentPhotoServeImpl.this.setVideoContent(moment, target);
                    } else {
                        moment.setContent(JSONUtil.toJSON(target));
                    }
                    boolean updated = MomentPhotoServeImpl.this.momentServe.updateMoment(moment);
                    LogUtil.d(MomentPhotoServeImpl.TAG, "updateMoment: result = [" + updated + "] + momentBean = [" + moment + "]");
                }
                return downloadUrlVo;
            }
        });
    }

    private void setVideoContent(DbMoment moment, VideoMsg videoMsg) {
        String content = moment.getContent();
        if (TextUtils.isEmpty(content)) {
            LogUtil.i(TAG, "setVideoContent, content is empty");
            return;
        }
        MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(content, MultiPhotoContent.class);
        multiPhotoContent.setVideoMsgContent(JSONUtil.toJSON(videoMsg));
        moment.setContent(JSONUtil.toJSON(multiPhotoContent));
    }
}