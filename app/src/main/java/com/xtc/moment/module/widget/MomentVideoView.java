package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.publish.multi.util.GlideRoundTransform;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.report.bean.VideoResource;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;
import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Moment row that shows a video cover with a play badge.
 *
 * <p>The cover is resolved through the batch download url service and the video itself is played
 * by the host activity through the content click listener.
 */
public class MomentVideoView extends MomentPhotoView {

    private static final String TAG = "MomentVideoView";

    /** Maximum number of url refresh attempts for a single moment. */
    private static final int MAX_URL_RETRY = 2;
    /** Corner radius of the play badge, in dp. */
    private static final int PLAY_LOGO_CORNER_DP = 4;

    private IAccountInfoServe accountInfoServe;
    private ExpandTextView expandTextView;
    private TextView mVideoText;
    private ImageView videoPlayLogo;
    private String selfWatchId;
    private boolean isSelf;

    public MomentVideoView(Context context) {
        super(context);
    }

    public MomentVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MomentVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_video_moment, this);
        this.mIcon = (ImageView) view.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) view.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) view.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) view.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) view.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) view.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.pi_friends_default);
        this.rlMomentSender = (RelativeLayout) view.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) view.findViewById(R.id.iv_account_report);
        this.videoPlayLogo = (ImageView) view.findViewById(R.id.view_video_moment_video_logo);
        this.mVideoText = (TextView) view.findViewById(R.id.video_text);
        this.expandTextView = (ExpandTextView) view.findViewById(R.id.ev_text);
    }

    @Override
    public void loadDefaultImage(Context context, int resId) {
        super.loadDefaultImage(context, resId);
        this.mContent.setImageResource(resId);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        if (checkContextIsNull(context)) {
            return;
        }
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.accountInfoServe = AccountInfoServerImpl.getInstance(context);
        this.selfWatchId = this.accountInfoServe.getWatchAccountInfo().getWatchId(context);
        this.isSelf = this.selfWatchId != null && this.selfWatchId.equals(moment.getWatchId());

        final MultiPhotoContent multiPhotoContent;
        final String content;
        if (moment.getType().intValue() == 27) {
            multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(moment.getContent(), MultiPhotoContent.class);
            content = multiPhotoContent != null ? multiPhotoContent.getVideoMsgContent() : null;
        } else {
            content = moment.getContent();
            multiPhotoContent = null;
        }
        String resource = moment.getResource();
        if (TextUtils.isEmpty(resource)) {
            LogUtil.i(TAG, "loadImage, resource is null");
            return;
        }
        VideoKeyOrToken videoKey = (VideoKeyOrToken) JSONUtil.fromJSON(resource, VideoKeyOrToken.class);
        LogUtil.i(TAG, "dialogType() " + moment.getType());
        String picKey = videoKey == null ? null : videoKey.getPicKey();

        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (multiPhotoContent != null && expandTextView != null && moment.getType().intValue() == 27) {
                    LogUtil.d(TAG, "text content ：" + multiPhotoContent.getContent());
                    setMultiVideoText(multiPhotoContent.getContent());
                } else if (expandTextView != null) {
                    expandTextView.setVisibility(GONE);
                }
            }
        });

        if (TextUtils.isEmpty(content)) {
            LogUtil.i(TAG, "loadImage, content is null");
            pullNewUrl(context, videoKey, moment, null);
            return;
        }
        if (!content.contains("source")) {
            return;
        }
        VideoMsg videoMsg = (VideoMsg) JSONUtil.fromJSON(content, VideoMsg.class);
        if (videoMsg == null) {
            LogUtil.i(TAG, "loadImage, VideoMsg is null");
            pullNewUrl(context, videoKey, moment, null);
            return;
        }
        String localThumbnailPath = videoMsg.getLocalThumbnailPath();
        LogUtil.d(TAG, "thumbnail path = " + localThumbnailPath);
        String thumbnail = localThumbnailPath == null ? picKey : localThumbnailPath;
        if (!new File(thumbnail).exists()) {
            displayNetPhoto(getContext(), videoMsg, resource, moment, videoKey);
        } else if (TextUtils.isEmpty(thumbnail) && videoMsg.getIcon() != null
                && !TextUtils.isEmpty(videoMsg.getIcon().getDownloadUrl())) {
            glideWithInto(getContext(), videoMsg.getIcon().getDownloadUrl(), moment, videoKey, videoMsg);
        } else {
            glideWithInto(getContext(), thumbnail, moment, videoKey, videoMsg);
        }
    }

    /** Shows the text attached to a multi-photo (video) moment. */
    private void setMultiVideoText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        this.expandTextView.setVisibility(VISIBLE);
        if (isTextSupportExpand()) {
            this.expandTextView.setMaxLines(3);
            this.expandTextView.setSupportExpand(true);
        }
        this.expandTextView.setText(text, new ExpandTextView.ClickCheckAllListener() {
            @Override
            public void click() {
                MomentVideoView view = MomentVideoView.this;
                view.startDetailActivity(view.getDbMoment());
            }
        });
    }

    @Override
    public void setTags(int key, Object value) {
        this.mContent.setTag(key, value);
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    /** Loads the thumbnail from the url cached in the video message, refreshing it when expired. */
    private void displayNetPhoto(Context context, VideoMsg videoMsg, String resource, DbMoment moment,
                                 VideoKeyOrToken videoKey) {
        CloudFileResource icon = videoMsg.getIcon();
        boolean noUrl = icon == null || TextUtils.isEmpty(icon.getDownloadUrl());
        long urlDeadline;
        String downloadUrl;
        if (noUrl) {
            urlDeadline = Long.MAX_VALUE;
            downloadUrl = null;
        } else {
            urlDeadline = icon.getUrlDeadline();
            downloadUrl = icon.getDownloadUrl();
        }
        if (System.currentTimeMillis() > urlDeadline || noUrl) {
            LogUtil.i(TAG, "链接过期：" + moment.getMomentId());
            pullNewUrl(context, videoKey, moment, videoMsg);
            return;
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            LogUtil.i(TAG, "displayNetPhoto, downloadUrl is empty");
            pullNewUrl(context, videoKey, moment, videoMsg);
        } else if (resource != null && resource.equals(this.mContent.getTag(R.id.moment))) {
            glideWithInto(context, downloadUrl, moment, videoKey, videoMsg);
        } else {
            LogUtil.d(TAG, "控件被复用了，不加载图片");
        }
    }

    /** Requests the video and thumbnail urls in one batch call. */
    private void pullNewUrl(final Context context, final VideoKeyOrToken videoKey, final DbMoment moment,
                            VideoMsg videoMsg) {
        if (videoKey == null) {
            LogUtil.d(TAG, "loadImageWithKey: videoKeyOrToken is null");
            return;
        }
        int retryCount = moment.getRetryCount();
        LogUtil.d(TAG, "pullNewUrl: " + moment.getMomentId());
        if (retryCount > MAX_URL_RETRY) {
            LogUtil.d(TAG, "pullNewUrl: 已经重试过两次了 不做重试操作！" + moment.getMomentId());
            return;
        }
        moment.setRetryCount(retryCount + 1);
        ArrayList<String> keys = new ArrayList<String>();
        keys.add(videoKey.getVideoKey());
        keys.add(videoKey.getPicKey());
        FileBatchUrlParam param = new FileBatchUrlParam(keys);
        if (videoMsg == null) {
            videoMsg = new VideoMsg();
        }
        final VideoMsg requestVideoMsg = videoMsg;
        new MomentPhotoServeImpl(context)
                .getDownloadBatchUrl(param, moment, requestVideoMsg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<DownloadUrlVo>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        super.onHttpError(e);
                        LogUtil.i(TAG, "onHttpError: " + e);
                        if (videoPlayLogo != null) {
                            videoPlayLogo.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onNext(DownloadUrlVo vo) {
                        if (moment.getResource() == null || !moment.getResource().equals(mContent.getTag(R.id.moment))) {
                            LogUtil.d(TAG, "控件被复用了，不加载图片");
                            return;
                        }
                        if (requestVideoMsg.getIcon() == null) {
                            LogUtil.d(TAG, "获取的链接为空");
                        } else {
                            glideWithInto(context, requestVideoMsg.getIcon().getDownloadUrl(), moment,
                                    videoKey, requestVideoMsg);
                        }
                    }
                });
    }

    public void glideWithInto(final Context context, final String url, final DbMoment moment,
                              final VideoKeyOrToken videoKey, final VideoMsg videoMsg) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "glideWithInto " + url);
                Glide.with(getMyContext()).clear(mContent);
                mContent.setImageDrawable(null);
                Glide.with(getMyContext()).load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                        boolean isFirstResource) {
                                if (videoPlayLogo != null) {
                                    videoPlayLogo.setVisibility(GONE);
                                }
                                LogUtil.d(TAG, "onResourceReady:  onLoadFailed" + moment.getMomentId());
                                pullNewUrl(getMyContext(), videoKey, moment, videoMsg);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                           DataSource dataSource, boolean isFirstResource) {
                                if (videoPlayLogo != null) {
                                    videoPlayLogo.setVisibility(VISIBLE);
                                    Glide.with(getMyContext()).load(Integer.valueOf(R.drawable.ic_friends_play))
                                            .apply(new RequestOptions()
                                                    .transform(new RoundedCorners(DimenUtil.dp2px(context, PLAY_LOGO_CORNER_DP)))
                                                    .override(mContent.getWidth(), mContent.getHeight())
                                                    .dontAnimate())
                                            .into(videoPlayLogo);
                                }
                                LogUtil.d(TAG, "onResourceReady:  onResourceReady" + moment.getMomentId());
                                return false;
                            }
                        })
                        .apply(new RequestOptions()
                                .error(R.drawable.pi_friends_default)
                                .placeholder(R.drawable.pi_friends_default)
                                .transform(new GlideRoundTransform(context, 6))
                                .dontAnimate())
                        .into(this.mContent);
            }
        });
    }

    @Override
    public void setContentOnClickListener(final Context context, final DbMoment moment,
                                          final AbsMomentView.OnContentOnClickListener listener) {
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(context, moment, listener);
            }
        });
        this.videoPlayLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(context, moment, listener);
            }
        });
    }

    private void clickView(Context context, final DbMoment moment,
                           final AbsMomentView.OnContentOnClickListener listener) {
        LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + moment);
        if (moment == null) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if ((listener != null && moment.getType().intValue() == 6)
                        || moment.getType().intValue() == 24 || moment.getType().intValue() == 27) {
                    listener.preVideoView(JSONUtil.toJSON(moment), true);
                }
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.expandTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "expandTextView longClick momentBean = " + moment);
                if (moment == null) {
                    return true;
                }
                MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(moment.getContent(),
                        MultiPhotoContent.class);
                if (multiPhotoContent == null) {
                    return true;
                }
                dealReport(listener, moment, multiPhotoContent.getContent(), 1);
                return true;
            }
        });
        this.videoPlayLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "videoPlayLogo longClick momentBean = " + moment);
                String resource = moment.getResource();
                if (TextUtils.isEmpty(resource)) {
                    LogUtil.i(TAG, "resource data error");
                    return true;
                }
                VideoResource videoResource = (VideoResource) JSONUtil.fromJSON(resource, VideoResource.class);
                dealReport(listener, moment, videoResource.getVideoKey(), 3);
                return true;
            }
        });
    }

    /** Deletes the moment when it belongs to the user, otherwise opens the report dialog. */
    public void dealReport(AbsMomentView.OnContentOnLongClickListener listener, final DbMoment moment,
                           final String resourceKey, final int contentType) {
        LogUtil.i(TAG, "dealReport, resourceKey = " + resourceKey);
        if (this.isSelf && listener != null) {
            listener.deleteItem(moment);
        } else {
            showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                @Override
                public void onRightBtnClick() {
                    startReportActivity(moment, "", resourceKey, null, contentType);
                }
            });
        }
    }
}