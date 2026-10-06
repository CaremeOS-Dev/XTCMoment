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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
import com.xtc.moment.module.bean.CropTransform;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;
import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Moment row that shows a single photo or a video cover.
 *
 * <p>Images are loaded from the local cache when available and otherwise fetched through the
 * download-url service, which is also asked to refresh expired urls.
 */
public class MomentPhotoView extends AbsMomentView {

    private static final String TAG = "MomentPhotoView";

    /** Maximum number of url refresh attempts for a single moment. */
    private static final int MAX_URL_RETRY = 2;
    /** Corner radius of the loaded image, in dp. */
    private static final int IMAGE_CORNER_DP = 4;
    /** Url type of the qiniu download service. */
    private static final String URL_TYPE_QN = "1";
    /** Url type of the fallback web download service. */
    private static final String URL_TYPE_WS = "2";

    protected ImageView mContent;
    protected View rootView;

    private final String selfWatchId;
    private final IMomentServe momentServe;

    public MomentPhotoView(Context context) {
        this(context, null);
    }

    public MomentPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.selfWatchId = AccountInfoServerImpl.getInstance(context).getWatchAccountInfo().getWatchId(context);
        this.momentServe = MomentServeImpl.getInstance(context.getApplicationContext());
    }

    @Override
    public void initView() {
        if (this.rootView == null) {
            this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_photo_moment, this);
        }
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) this.rootView.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) this.rootView.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
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
        super.loadImage(context, moment);
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        if (moment == null) {
            LogUtil.d(TAG, "loadImage momentBean == null");
            return;
        }
        LogUtil.i(TAG, "loadImage MomentId = " + moment.getMomentId());
        String content = moment.getContent();
        String resource = moment.getResource();

        if (this instanceof MomentShareImageView || this instanceof MomentShareImageViewComment) {
            ShareImageMoment shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (this.mContent != null && shareImageMoment != null && shareImageMoment.getMessageBitmapArgs() != null) {
                this.mContent.getLayoutParams().height = shareImageMoment.getMessageBitmapArgs().getHeight();
                this.mContent.getLayoutParams().width = shareImageMoment.getMessageBitmapArgs().getWidth();
            }
        } else if (this instanceof MomentVideoView) {
            VideoMsg videoMsg = (VideoMsg) JSONUtil.fromJSON(moment.getContent(), VideoMsg.class);
            if (videoMsg != null && videoMsg.getIcon() != null && videoMsg.getIcon().getDownloadUrl() != null
                    && videoMsg.getIcon().getUrlDeadline() > System.currentTimeMillis()) {
                glideWithInto(context, videoMsg.getIcon().getDownloadUrl(), moment);
                return;
            }
        } else if (moment.getType().intValue() == 11) {
            glideWithInto(context, moment.getResource(), moment);
            return;
        }

        if (content != null && !TextUtils.isEmpty(content) && content.contains("source")) {
            PhotoMsg photoMsg = (PhotoMsg) JSONUtil.fromJSON(content, PhotoMsg.class);
            String videoThumbnailKey = getVideoThumbnailKey(moment, resource);
            if (photoMsg == null) {
                LogUtil.d(TAG, "photoMsg == null");
                loadImageWithKey(context, videoThumbnailKey, URL_TYPE_QN, moment);
                return;
            }
            if (!TextUtils.isEmpty(photoMsg.getLocalPath())) {
                if (!new File(photoMsg.getLocalPath()).exists()) {
                    displayNetPhoto(getContext(), photoMsg, videoThumbnailKey, moment);
                } else {
                    loadDiskPhoto(context, photoMsg, moment);
                }
                return;
            }
            LogUtil.d(TAG, "photoMsg.getLocalPath()==empty");
            displayNetPhoto(getContext(), photoMsg, videoThumbnailKey, moment);
            return;
        }
        loadImageWithKey(context, resource, URL_TYPE_QN, moment);
    }

    private PhotoMsg convertToPhotoMsg(String json) {
        return (PhotoMsg) JSONUtil.fromJSON(json, PhotoMsg.class);
    }

    /** Key of the thumbnail: video moments store it separately from the resource. */
    private String getVideoThumbnailKey(DbMoment moment, String resource) {
        if (moment.getType().intValue() == 6) {
            VideoKeyOrToken videoKey = (VideoKeyOrToken) JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class);
            return videoKey.getPicKey();
        }
        return resource;
    }

    private void loadDiskPhoto(final Context context, final PhotoMsg photoMsg, final DbMoment moment) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                doLoadDiskPhoto(context, photoMsg, moment);
            }
        });
    }

    private void doLoadDiskPhoto(Context context, PhotoMsg photoMsg, final DbMoment moment) {
        LogUtil.d(TAG, "loadDiskPhoto");
        final String resource = moment.getResource();
        this.mContent.setImageDrawable(null);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_selfie_album_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(photoMsg.getLocalPath()));
        if (!(this instanceof MomentShareAppView)) {
            this.mContent.setScaleType(ImageView.ScaleType.CENTER_CROP);
            options = options.transform(new MultiTransformation<Bitmap>(
                    new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, IMAGE_CORNER_DP))));
        } else {
            this.mContent.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        ShareImageMoment shareImageMoment = null;
        if (this instanceof MomentShareImageView || this instanceof MomentShareImageViewComment) {
            shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
        }
        if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                && shareImageMoment.getMessageBitmapArgs() != null) {
            final DialogBitmapArgs dialogArgs = shareImageMoment.getDialogBitmapArgs();
            Glide.with(getMyContext()).load(photoMsg.getLocalPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                    boolean isFirstResource) {
                            LogUtil.d(TAG, "onResourceReady:   onLoadFailed");
                            pullNewUrl(getMyContext(), convertToPhotoMsg(moment.getContent()), resource, moment);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            mContent.setImageDrawable(drawable);
                            LogUtil.d(TAG, "onResourceReady:   onResourceReady");
                            return true;
                        }
                    })
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_selfie_album_default_custom)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(this.mContent.getWidth(), this.mContent.getHeight())
                            .dontAnimate()
                            .signature(new ObjectKey(photoMsg.getLocalPath()))
                            .transform(new CropTransform(dialogArgs.getCropWidth(), dialogArgs.getCropHeight(),
                                    dialogArgs.getCutStart(), dialogArgs.getCutTop())))
                    .into(new TagCheckedDrawableTarget(this.mContent, resource));
        } else {
            Glide.with(getMyContext()).load(photoMsg.getLocalPath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                    boolean isFirstResource) {
                            LogUtil.d(TAG, "onResourceReady:   onLoadFailed");
                            pullNewUrl(getMyContext(), convertToPhotoMsg(moment.getContent()), resource, moment);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            mContent.setImageDrawable(drawable);
                            LogUtil.d(TAG, "onResourceReady:   onResourceReady");
                            return false;
                        }
                    })
                    .apply(options)
                    .into(new TagCheckedDrawableTarget(this.mContent, resource));
        }
    }

    /** Target that only applies the image when the view still shows the same resource tag. */
    private static class TagCheckedDrawableTarget extends DrawableImageViewTarget {

        private final String tag;

        TagCheckedDrawableTarget(ImageView view, String tag) {
            super(view);
            this.tag = tag;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            ImageView view = getView();
            if (view == null || this.tag == null) {
                LogUtil.e(TAG, "DialogBitmapArgs onResourceReady: set image error. view: " + view + ", tag: " + this.tag);
                return;
            }
            if (this.tag.equals(view.getTag(R.id.moment))) {
                super.onResourceReady(resource, transition);
                LogUtil.i(TAG, "DialogBitmapArgs onResourceReady: load local image complete!!! tag: " + this.tag);
                return;
            }
            LogUtil.w(TAG, "DialogBitmapArgs onResourceReady: view is recycled. tag: " + this.tag
                    + ", iv.tag: " + view.getTag());
        }
    }

    @Override
    public void setTags(int key, Object value) {
        ImageView content = this.mContent;
        if (content != null) {
            content.setTag(key, value);
        }
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    /** Loads the network url stored in the photo message, refreshing it when it has expired. */
    private void displayNetPhoto(Context context, PhotoMsg photoMsg, String photoKey, DbMoment moment) {
        LogUtil.d(TAG, "dislplayNetPhoto: photoKey = [" + photoKey + "]");
        String downloadUrl = null;
        SmallPicSouce smallPic;
        com.xtc.moment.module.bean.CloudFileResource source;
        if (photoMsg != null) {
            smallPic = photoMsg.getSmallPic();
            source = photoMsg.getSource();
        } else {
            smallPic = null;
            source = null;
        }
        long urlDeadline = Long.MAX_VALUE;
        long now = System.currentTimeMillis();
        if (smallPic != null && now < smallPic.getUrlDeadline()) {
            urlDeadline = smallPic.getUrlDeadline();
            downloadUrl = smallPic.getDownloadUrl();
        } else if (source != null) {
            urlDeadline = source.getUrlDeadline();
            downloadUrl = source.getDownloadUrl();
        }
        if (now > urlDeadline) {
            pullNewUrl(context, photoMsg, photoKey, moment);
            return;
        }
        if (android.text.TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        if (photoKey != null && photoKey.equals(this.mContent.getTag(R.id.moment))) {
            glideWithInto(context, downloadUrl, moment);
        } else {
            LogUtil.d(TAG, "控件被复用了，不加载图片");
        }
    }

    /** Requests a fresh download url when the cached one has expired. */
    private void pullNewUrl(final Context context, PhotoMsg photoMsg, final String photoKey, final DbMoment moment) {
        int retryCount = moment.getRetryCount();
        LogUtil.d(TAG, "pullNewUrl: " + moment.getMomentId());
        if (retryCount > MAX_URL_RETRY) {
            LogUtil.d(TAG, "pullNewUrl: 已经重试过两次了 不做重试操作！" + moment.getMomentId());
            return;
        }
        moment.setRetryCount(retryCount + 1);
        new MomentPhotoServeImpl(context)
                .getDownloadUrl(new FileUrlParam(getVideoThumbnailKey(moment, photoKey), photoMsg.getType()), photoMsg, moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        super.onHttpError(e);
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        if (photoKey != null && photoKey.equals(mContent.getTag(R.id.moment))) {
                            glideWithInto(context, url, moment);
                        } else {
                            LogUtil.d(TAG, "控件被复用了，不加载图片");
                        }
                    }
                });
    }

    /** Resolves the download url for {@code key} and loads it, falling back to the web url type. */
    private void loadImageWithKey(final Context context, String key, final String urlType, final DbMoment moment) {
        String videoThumbnailKey = getVideoThumbnailKey(moment, key);
        final String resource = moment.getResource();
        LogUtil.d(TAG, "loadImageWithKey#key:" + videoThumbnailKey + ";type:" + urlType);
        new MomentPhotoServeImpl(context)
                .getDownloadUrl(new FileUrlParam(videoThumbnailKey, urlType), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        super.onHttpError(e);
                        LogUtil.d(TAG, "loadImageWithKey#e:" + e);
                        if (URL_TYPE_QN.equals(urlType)) {
                            loadImageWithKey(context, resource, URL_TYPE_WS, moment);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        LogUtil.d(TAG, "loadImageWithKey#url:" + url);
                        LogUtil.d(TAG, "loadImageWithKey#key:" + resource + ";photoView.getTag:"
                                + mContent.getTag(R.id.moment));
                        if (resource.equals(mContent.getTag(R.id.moment))) {
                            glideWithInto(context, url, moment);
                        } else {
                            LogUtil.d(TAG, "控件被复用了，不加载图片");
                        }
                    }
                });
    }

    public void glideWithInto(final Context context, final String url, final DbMoment moment) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                doGlideWithInto(context, url, moment);
            }
        });
    }

    private void doGlideWithInto(Context context, String url, final DbMoment moment) {
        LogUtil.d(TAG, "glideWithInto: " + url + context);
        final String resource = moment.getResource();
        this.mContent.setImageDrawable(null);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_selfie_album_default)
                .placeholder(R.drawable.ic_selfie_album_default)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate()
                .transform(new MultiTransformation<Bitmap>(
                        new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, IMAGE_CORNER_DP))));
        this.mContent.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ShareImageMoment shareImageMoment = null;
        if (this instanceof MomentShareImageView || this instanceof MomentShareImageViewComment) {
            shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
        }
        if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                && shareImageMoment.getMessageBitmapArgs() != null) {
            final DialogBitmapArgs dialogArgs = shareImageMoment.getDialogBitmapArgs();
            Glide.with(getMyContext()).load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                    boolean isFirstResource) {
                            LogUtil.d(TAG, "onResourceReady:   onLoadFailed");
                            pullNewUrl(getMyContext(), convertToPhotoMsg(moment.getContent()), resource, moment);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            mContent.setImageDrawable(drawable);
                            LogUtil.d(TAG, "onResourceReady:   onResourceReady");
                            return false;
                        }
                    })
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_selfie_album_default_custom)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .override(this.mContent.getWidth(), this.mContent.getHeight())
                            .dontAnimate()
                            .signature(new ObjectKey(url))
                            .transform(new CropTransform(dialogArgs.getCropWidth(), dialogArgs.getCropHeight(),
                                    dialogArgs.getCutStart(), dialogArgs.getCutTop())))
                    .into(new TagCheckedDrawableTarget(this.mContent, resource));
            return;
        }

        Context myContext = getMyContext();
        if (myContext == null) {
            myContext = this.mContent.getContext();
        }
        if (myContext == null) {
            LogUtil.e(TAG, "glideWithInto: myContext == null");
        } else {
            Glide.with(myContext).load(url)
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                    boolean isFirstResource) {
                            LogUtil.d(TAG, "onResourceReady:  onLoadFailed" + moment.getMomentId());
                            pullNewUrl(getMyContext(), convertToPhotoMsg(moment.getContent()), resource, moment);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            mContent.setImageDrawable(drawable);
                            LogUtil.d(TAG, "onResourceReady:   onResourceReady" + moment.getMomentId());
                            return true;
                        }
                    })
                    .into(this.mContent);
        }
    }

    @Override
    public void setContentOnClickListener(Context context, DbMoment moment,
                                          AbsMomentView.OnContentOnClickListener listener) {
        ImageView content = this.mContent;
        if (content == null) {
            LogUtil.d(TAG, "mContent == null");
        } else {
            content.setOnClickListener(new PhotoContentClickListener(context, moment, listener));
        }
    }

    /** Opens the photo preview, resolving the url first when it is not cached locally. */
    private class PhotoContentClickListener implements View.OnClickListener {

        private final Context context;
        private final DbMoment moment;
        private final AbsMomentView.OnContentOnClickListener listener;

        PhotoContentClickListener(Context context, DbMoment moment, AbsMomentView.OnContentOnClickListener listener) {
            this.context = context;
            this.moment = moment;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + this.moment);
            final DbMoment momentBean = this.moment;
            if (momentBean == null) {
                return;
            }
            final String content = momentBean.getContent();
            if (content == null || TextUtils.isEmpty(content)) {
                getPhotoUrl(this.context, momentBean.getResource(), URL_TYPE_QN, momentBean, this.listener);
            } else {
                final AbsMomentView.OnContentOnClickListener clickListener = this.listener;
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        PhotoMsg photoMsg = (PhotoMsg) JSONUtil.fromJSON(content, PhotoMsg.class);
                        if (clickListener != null) {
                            clickListener.previewPhoto(photoMsg);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void setContentOnLongClickListener(final Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
        ImageView content = this.mContent;
        if (content == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (moment.getWatchId().equals(selfWatchId)) {
                    if (listener != null) {
                        listener.deleteItem(moment);
                    }
                } else {
                    showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                        @Override
                        public void onRightBtnClick() {
                            if (Objects.equals(moment.getType(), 25)) {
                                ToastUtil.showShort(context, context.getResources().getString(R.string.report_not_support));
                            } else {
                                startReportActivity(moment.getWatchId(), moment.getMomentId(), "");
                            }
                        }
                    });
                }
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment);
                return true;
            }
        });
    }

    /** Resolves a url only for the purpose of previewing the photo. */
    private void getPhotoUrl(final Context context, String key, final String urlType, final DbMoment moment,
                             final AbsMomentView.OnContentOnClickListener listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            ToastUtil.showNoConnected(context);
            return;
        }
        LogUtil.d(TAG, "getPhotoUrl#key:" + key + ";type:" + urlType);
        new MomentPhotoServeImpl(context)
                .getDownloadUrl(new FileUrlParam(key, urlType), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        super.onHttpError(e);
                        LogUtil.d(TAG, "getPhotoUrl#e:" + e);
                        if (URL_TYPE_QN.equals(urlType)) {
                            getPhotoUrl(context, moment.getResource(), URL_TYPE_WS, moment, listener);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        LogUtil.d(TAG, "getPhotoUrl#url:" + url);
                        if (listener != null) {
                            listener.previewPhoto(url);
                        }
                    }
                });
    }
}