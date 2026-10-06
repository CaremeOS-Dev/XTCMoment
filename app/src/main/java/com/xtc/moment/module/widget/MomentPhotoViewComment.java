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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.CropTransform;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Photo row shown inside the comment page. Same behaviour as {@link MomentPhotoView} but laid out
 * with {@code view_photo_moment_comment}.
 */
public class MomentPhotoViewComment extends MomentPhotoView {

    private static final String TAG = "MomentPhotoViewComment";

    private static final int IMAGE_CORNER_DP = 4;
    private static final String URL_TYPE_QN = "1";
    private static final String URL_TYPE_WS = "2";

    protected ImageView mContent;
    protected View rootView;

    public MomentPhotoViewComment(Context context) {
        this(context, null);
    }

    public MomentPhotoViewComment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentPhotoViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_photo_moment_comment, this);
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
        String content = moment.getContent();
        String resource = moment.getResource();
        if (moment.getType().intValue() == 8) {
            ShareImageMoment shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (this.mContent != null && shareImageMoment != null && shareImageMoment.getMessageBitmapArgs() != null) {
                this.mContent.getLayoutParams().height = shareImageMoment.getMessageBitmapArgs().getHeight();
                this.mContent.getLayoutParams().width = shareImageMoment.getMessageBitmapArgs().getWidth();
            }
        } else if (moment.getType().intValue() == 6) {
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
            LogUtil.d(TAG, "PhotoMsg = " + photoMsg);
            if (!TextUtils.isEmpty(photoMsg.getLocalPath())) {
                LogUtil.d(TAG, "photoMsg.getLocalPath():" + photoMsg.getLocalPath());
                if (!new File(photoMsg.getLocalPath()).exists()) {
                    LogUtil.d(TAG, "!photoFile.exists()");
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

    private String getVideoThumbnailKey(DbMoment moment, String resource) {
        if (moment.getType().intValue() == 6) {
            VideoKeyOrToken videoKey = (VideoKeyOrToken) JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class);
            return videoKey.getPicKey();
        }
        return resource;
    }

    private void loadDiskPhoto(Context context, PhotoMsg photoMsg, DbMoment moment) {
        final String resource = moment.getResource();
        Glide.with(context).clear(this.mContent);
        this.mContent.setImageDrawable(null);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_selfie_album_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(photoMsg.getLocalPath()));
        if (moment.getType().intValue() != 9) {
            this.mContent.setScaleType(ImageView.ScaleType.CENTER_CROP);
            options = options.transform(new MultiTransformation<Bitmap>(
                    new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, IMAGE_CORNER_DP))));
        } else {
            this.mContent.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        ShareImageMoment shareImageMoment = null;
        if (moment.getType().intValue() == 8) {
            shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
        }
        if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                && shareImageMoment.getMessageBitmapArgs() != null) {
            DialogBitmapArgs dialogArgs = shareImageMoment.getDialogBitmapArgs();
            Glide.with(context).load(photoMsg.getLocalPath())
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_selfie_album_default_custom)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(this.mContent.getWidth(), this.mContent.getHeight())
                            .dontAnimate()
                            .signature(new ObjectKey(photoMsg.getLocalPath()))
                            .transform(new CropTransform(dialogArgs.getCropWidth(), dialogArgs.getCropHeight(),
                                    dialogArgs.getCutStart(), dialogArgs.getCutTop())))
                    .into(new TagCheckedTarget(this.mContent, resource));
        } else {
            Glide.with(context).load(photoMsg.getLocalPath())
                    .apply(options)
                    .into(new TagCheckedTarget(this.mContent, resource));
        }
    }

    /** Applies the loaded drawable only while the view still holds the matching resource tag. */
    private static class TagCheckedTarget extends DrawableImageViewTarget {

        private final String tag;

        TagCheckedTarget(ImageView view, String tag) {
            super(view);
            this.tag = tag;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            ImageView view = getView();
            if (view == null || this.tag == null) {
                LogUtil.e(TAG, "onResourceReady: set image error. view: " + view + ", tag: " + this.tag);
                return;
            }
            if (this.tag.equals(view.getTag(R.id.moment))) {
                super.onResourceReady(resource, transition);
                LogUtil.i(TAG, "onResourceReady: load local image complete!!! tag: " + this.tag);
                return;
            }
            LogUtil.w(TAG, "onResourceReady: view is recycled. tag: " + this.tag + ", iv.tag: " + view.getTag());
        }
    }

    @Override
    public void setTags(int key, Object value) {
        this.mContent.setTag(key, value);
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    private void displayNetPhoto(final Context context, PhotoMsg photoMsg, final String photoKey, final DbMoment moment) {
        LogUtil.d(TAG, "photoMsg = " + photoMsg);
        SmallPicSouce smallPic = photoMsg.getSmallPic();
        CloudFileResource source = photoMsg.getSource();
        long urlDeadline;
        String downloadUrl;
        if (smallPic != null) {
            urlDeadline = smallPic.getUrlDeadline();
            downloadUrl = smallPic.getDownloadUrl();
        } else if (source != null) {
            urlDeadline = source.getUrlDeadline();
            downloadUrl = source.getDownloadUrl();
        } else {
            urlDeadline = Long.MAX_VALUE;
            downloadUrl = null;
        }
        if (System.currentTimeMillis() > urlDeadline) {
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

    @Override
    public void glideWithInto(Context context, String url, DbMoment moment) {
        final String resource = moment.getResource();
        Glide.with(context).clear(this.mContent);
        this.mContent.setImageDrawable(null);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_selfie_album_default)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(url));
        if (moment.getType().intValue() != 9) {
            this.mContent.setScaleType(ImageView.ScaleType.CENTER_CROP);
            options = options.transform(new MultiTransformation<Bitmap>(
                    new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, IMAGE_CORNER_DP))));
        } else {
            this.mContent.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        ShareImageMoment shareImageMoment = null;
        if (moment.getType().intValue() == 8) {
            shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
        }
        if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                && shareImageMoment.getMessageBitmapArgs() != null) {
            DialogBitmapArgs dialogArgs = shareImageMoment.getDialogBitmapArgs();
            Glide.with(context).load(url)
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_selfie_album_default_custom)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .override(this.mContent.getWidth(), this.mContent.getHeight())
                            .dontAnimate()
                            .signature(new ObjectKey(url))
                            .transform(new CropTransform(dialogArgs.getCropWidth(), dialogArgs.getCropHeight(),
                                    dialogArgs.getCutStart(), dialogArgs.getCutTop())))
                    .into(new TagCheckedTarget(this.mContent, resource));
        } else {
            Glide.with(context).load(url)
                    .apply(options)
                    .into(new TagCheckedTarget(this.mContent, resource));
        }
    }

    @Override
    public void setContentOnClickListener(Context context, DbMoment moment,
                                          AbsMomentView.OnContentOnClickListener listener) {
        ImageView content = this.mContent;
        if (content == null) {
            LogUtil.d(TAG, "mContent == null");
        } else {
            content.setOnClickListener(new PhotoClickListener(context, moment, listener));
        }
    }

    /** Opens the photo preview for this row. */
    private class PhotoClickListener implements View.OnClickListener {

        private final Context context;
        private final DbMoment moment;
        private final AbsMomentView.OnContentOnClickListener listener;

        PhotoClickListener(Context context, DbMoment moment, AbsMomentView.OnContentOnClickListener listener) {
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
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
        ImageView content = this.mContent;
        if (content == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        content.setOnLongClickListener(new View.OnLongClickListener() {
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