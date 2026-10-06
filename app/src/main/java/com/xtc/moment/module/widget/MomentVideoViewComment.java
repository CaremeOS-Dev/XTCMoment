package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.Utils;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;
import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Comment page video row: shows the video cover plus the optional "like" caption.
 */
public class MomentVideoViewComment extends MomentPhotoView {

    private static final String TAG = "MomentVideoViewComment";

    private static final int COVER_CORNER_DP = 6;
    private static final int PLAY_LOGO_CORNER_DP = 4;
    private static final String URL_TYPE_QN = "1";
    private static final String URL_TYPE_WS = "2";

    private TextView mVideoLike;
    private ImageView videoPlayLogo;

    public MomentVideoViewComment(Context context) {
        super(context);
    }

    public MomentVideoViewComment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MomentVideoViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_video_moment_comment, this);
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
        this.mVideoLike = (TextView) view.findViewById(R.id.new_video_like);
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
        String content = moment.getContent();
        String resource = moment.getResource();
        String originalResource = moment.getResource();
        String publishContent = moment.getPublishContent();
        VideoKeyOrToken videoKey = (VideoKeyOrToken) JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class);
        if (videoKey != null) {
            LogUtil.i(TAG, "loadImage " + moment.getContent() + "\n videoKeyOrToken :" + videoKey.getContent()
                    + "   content  :" + publishContent);
            resource = videoKey.getPicKey();
        }
        this.mVideoLike.setVisibility(GONE);
        if (moment.getType().intValue() == 27 && !TextUtils.isEmpty(content)) {
            MultiPhotoContent multiPhotoContent = (MultiPhotoContent) JSONUtil.fromJSON(content, MultiPhotoContent.class);
            if (multiPhotoContent != null && !TextUtils.isEmpty(multiPhotoContent.getContent())) {
                this.mVideoLike.setVisibility(VISIBLE);
                this.mVideoLike.setText(multiPhotoContent.getContent());
            }
        }
        if (publishContent != null && !TextUtils.isEmpty(publishContent) && publishContent.contains("source")) {
            VideoMsg videoMsg = (VideoMsg) JSONUtil.fromJSON(publishContent, VideoMsg.class);
            if (videoMsg == null) {
                LogUtil.d(TAG, "photoMsg == null");
                loadImageWithKey(context, resource, URL_TYPE_QN, moment);
                return;
            }
            LogUtil.d(TAG, "VideoMsg = " + videoMsg);
            String localThumbnailPath = videoMsg.getLocalThumbnailPath();
            if (localThumbnailPath == null) {
                localThumbnailPath = resource;
            }
            if (!new File(localThumbnailPath).exists()) {
                displayNetPhoto(getContext(), videoMsg, originalResource, moment);
            } else {
                glideWithInto(getContext(), localThumbnailPath, moment);
            }
            return;
        }
        loadImageWithKey(context, resource, URL_TYPE_QN, moment);
    }

    @Override
    public void setTags(int key, Object value) {
        this.mContent.setTag(key, value);
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    private void displayNetPhoto(final Context context, VideoMsg videoMsg, final String resource, final DbMoment moment) {
        LogUtil.d(TAG, "displayNetPhoto videoMsg = " + videoMsg);
        CloudFileResource source = videoMsg.getSource();
        CloudFileResource icon = videoMsg.getIcon();
        long urlDeadline;
        String downloadUrl;
        if (icon != null && !TextUtils.isEmpty(icon.getDownloadUrl())) {
            urlDeadline = icon.getUrlDeadline();
            downloadUrl = icon.getDownloadUrl();
        } else if (source == null || TextUtils.isEmpty(source.getDownloadUrl())) {
            urlDeadline = Long.MAX_VALUE;
            downloadUrl = null;
        } else {
            urlDeadline = source.getUrlDeadline();
            downloadUrl = source.getDownloadUrl();
        }
        boolean missingUrl = (icon != null && TextUtils.isEmpty(icon.getDownloadUrl()))
                || (source != null && TextUtils.isEmpty(source.getDownloadUrl()));
        if (System.currentTimeMillis() > urlDeadline || missingUrl) {
            ArrayList<String> keys = new ArrayList<String>();
            if (videoMsg.getIcon() != null) {
                keys.add(videoMsg.getIcon().getKey());
            }
            if (videoMsg.getSource() != null) {
                keys.add(videoMsg.getSource().getKey());
            }
            new MomentPhotoServeImpl(context)
                    .getDownloadBatchUrl(new FileBatchUrlParam(keys), moment, videoMsg)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpSubscriber<DownloadUrlVo>() {
                        @Override
                        public void onHttpError(Throwable e) {
                            if (videoPlayLogo != null) {
                                videoPlayLogo.setVisibility(GONE);
                            }
                        }

                        @Override
                        public void onNext(DownloadUrlVo vo) {
                            if (resource == null || !resource.equals(mContent.getTag(R.id.moment)) || vo == null) {
                                LogUtil.d(TAG, "控件被复用了，不加载图片258");
                                return;
                            }
                            for (int i = 0; i < vo.getUrls().size(); i++) {
                                if (Utils.isIconKey(vo.getUrls().get(i).getKey())) {
                                    glideWithInto(context, vo.getUrls().get(i).getDownloadUrl(), moment);
                                    return;
                                }
                            }
                        }
                    });
            return;
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        if (resource != null && resource.equals(this.mContent.getTag(R.id.moment))) {
            glideWithInto(context, downloadUrl, moment);
        } else {
            LogUtil.d(TAG, "控件被复用了，不加载图片268");
        }
    }

    private void loadImageWithKey(final Context context, String key, String urlType, final DbMoment moment) {
        LogUtil.d(TAG, "loadImageWithKey#key:" + key + ";type:" + urlType);
        new MomentPhotoServeImpl(context)
                .getDownloadUrl(new FileUrlParam(key, urlType), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        if (videoPlayLogo != null) {
                            videoPlayLogo.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        if (moment.getResource() == null || !moment.getResource().equals(mContent.getTag(R.id.moment))) {
                            LogUtil.d(TAG, "控件被复用了，不加载图片");
                        } else {
                            glideWithInto(context, url, moment);
                        }
                    }
                });
    }

    @Override
    public void glideWithInto(final Context context, String url, DbMoment moment) {
        LogUtil.i(TAG, "glideWithInto " + url);
        final String resource = moment.getResource();
        Glide.with(getMyContext()).clear(this.mContent);
        this.mContent.setImageDrawable(null);
        RequestOptions coverOptions = new RequestOptions()
                .error(R.drawable.pi_friends_default)
                .transform(new RoundedCorners(DimenUtil.dp2px(context, COVER_CORNER_DP)))
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(url));
        final RequestOptions logoOptions = new RequestOptions()
                .transform(new RoundedCorners(DimenUtil.dp2px(context, PLAY_LOGO_CORNER_DP)))
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate();
        this.mContent.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getMyContext()).load(url)
                .apply(coverOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        if (videoPlayLogo != null) {
                            videoPlayLogo.setVisibility(GONE);
                        }
                        mContent.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pi_friends_default));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        if (videoPlayLogo != null) {
                            Glide.with(getMyContext()).load(Integer.valueOf(R.drawable.ic_friends_play))
                                    .apply(logoOptions)
                                    .into(videoPlayLogo);
                        }
                        mContent.setImageDrawable(drawable);
                        return true;
                    }
                })
                .into(new DrawableImageViewTarget(this.mContent) {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        ImageView view = getView();
                        if (view == null || resource == null) {
                            LogUtil.e(TAG, "onResourceReady: set image error. view: " + view + ", tag: " + resource);
                            return;
                        }
                        if (!resource.equals(view.getTag(R.id.moment))) {
                            LogUtil.w(TAG, "onResourceReady: view is recycled. tag: " + resource + ", iv.tag: " + view.getTag());
                            return;
                        }
                        super.onResourceReady(drawable, transition);
                        LogUtil.i(TAG, "load local image complete!!! tag: " + resource);
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
                clickView(getContext(), moment, listener);
            }
        });
        this.videoPlayLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(getContext(), moment, listener);
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
                if (listener == null || moment.getType().intValue() != 6) {
                    return;
                }
                listener.preVideoView(JSONUtil.toJSON(moment), true);
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
        this.mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment);
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
        this.videoPlayLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment);
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}