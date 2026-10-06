package com.xtc.moment.module.share.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.CropTransform;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.Utils;
import com.xtc.moment.widget.LbsLayout;
import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;
import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图片/视频动态的 ViewHolder，负责从本地或网络加载缩略图并处理复用。
 */
public class PhotoViewHolder extends AbsViewHolder {

    private static final String TAG = "PhotoViewHolder";

    public PhotoViewHolder(View view) {
        super(view);
        this.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        this.tvName = (TextView) view.findViewById(R.id.tv_name);
        this.tvTime = (TextView) view.findViewById(R.id.tv_time);
        this.ivContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);
        this.ivContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.tvLikes = (TextView) view.findViewById(R.id.tv_likes);
        this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
        this.ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
        this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
        this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
        if (this.tvLikes != null) {
            this.tvLikes.setHighlightColor(0);
        }
    }
    @Override
    public void setContentOnClickListener(final DbMoment moment, final ShareAdapter.OnContentOnClickListener listener) {
        this.ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(PhotoViewHolder.TAG, "setContentOnClickListener#onClick#momentBean:" + moment);
                String content = moment.getContent();
                if (content != null && !TextUtils.isEmpty(content)) {
                    PhotoMsg photoMsg = JSONUtil.fromJSON(content, PhotoMsg.class);
                    if (listener != null) {
                        if (moment.getType().intValue() == 6 || moment.getType().intValue() == 24) {
                            listener.preVideoView(JSONUtil.toJSON(moment));
                        } else {
                            listener.previewPhoto(photoMsg);
                        }
                    }
                    return;
                }
                PhotoViewHolder.this.getPhotoUrl(PhotoViewHolder.this.getHolderContext(), "1", moment.getResource(),
                        moment, listener);
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(final DbMoment moment, final ShareAdapter.OnContentOnLongClickListener listener) {
        this.ivContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener == null || !PhotoViewHolder.this.ismIsSelf()) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        if (this.ivContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        boolean roundedCorner = moment.getType().intValue() != 9;
        if (11 == moment.getType().intValue()) {
            if (TextUtils.isEmpty(moment.getResource())) {
                return;
            }
            configAndLoadAdvantisePhoto(getHolderContext(), moment, this.ivContent);
            return;
        }
        LogUtil.d(TAG, "loadImage#momentBean:" + moment);
        String resource = moment.getResource();
        String content = moment.getType().intValue() == 27 ? moment.getPublishContent() : moment.getContent();
        if (content != null && !TextUtils.isEmpty(content) && content.contains("source")) {
            PhotoMsg photoMsg = JSONUtil.fromJSON(content, PhotoMsg.class);
            if (photoMsg == null) {
                LogUtil.d(TAG, "photoMsg == null");
                loadImageWithKey(getHolderContext(), this.ivContent, resource, "1", moment, roundedCorner, holder);
                return;
            }
            LogUtil.d(TAG, "PhotoMsg = " + photoMsg);
            if (!TextUtils.isEmpty(photoMsg.getLocalPath())) {
                LogUtil.d(TAG, "photoMsg.getLocalPath():" + photoMsg.getLocalPath());
                if (!new File(photoMsg.getLocalPath()).exists()) {
                    LogUtil.d(TAG, "!photoFile.exists()");
                    dislplayNetPhoto(context, resource, this.ivContent, roundedCorner, moment, holder);
                    return;
                }
                LogUtil.d(TAG, "load local image!!");
                loadDiskPhoto(photoMsg, roundedCorner, moment, holder);
                LogUtil.d(TAG, "load local image complete!!!");
                return;
            }
            LogUtil.d(TAG, "photoMsg.getLocalPath()==empty");
            dislplayNetPhoto(context, resource, this.ivContent, roundedCorner, moment, holder);
            return;
        }
        loadImageWithKey(getHolderContext(), this.ivContent, resource, "1", moment, roundedCorner, holder);
    }
    public void loadDiskPhoto(PhotoMsg photoMsg, boolean roundedCorner, final DbMoment moment, AbsViewHolder holder) {
        String resource = moment.getResource();
        Glide.with(getHolderContext()).clear(this.ivContent);
        this.ivContent.setImageDrawable(null);
        RequestOptions options = new RequestOptions().error(R.drawable.ic_selfie_album_default)
                .placeholder(R.drawable.ic_selfie_album_default)
                .override(this.ivContent.getWidth(), this.ivContent.getHeight())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .signature(new ObjectKey(photoMsg.getLocalPath()));
        if (roundedCorner) {
            options = options.transform((Transformation<Bitmap>) new MultiTransformation(
                    new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(getHolderContext(), 4.0f))));
        }
        if (resource == null || !resource.equals(this.ivContent.getTag(R.id.chat_msg_item_photo_iv))) {
            LogUtil.i(TAG, "loadDiskPhoto: 控件被复用了. photoKey: " + resource + ", view.tag: " + this.ivContent.getTag());
            return;
        }
        if (11 == moment.getType().intValue()) {
            Glide.with(getHolderContext()).load(photoMsg.getLocalPath())
                    .apply(new RequestOptions().error(R.drawable.ic_selfie_album_default)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(this.ivContent.getWidth(), this.ivContent.getHeight())
                            .transform((Transformation<Bitmap>) new RoundedCorners(DimenUtil.dp2px(getHolderContext(), 4.0f)))
                            .dontAnimate()
                            .signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                            PhotoViewHolder.this.loadAdvantagePhoto(moment, drawable, PhotoViewHolder.this.ivContent);
                        }
                    });
            return;
        }
        if (8 == moment.getType().intValue()) {
            LogUtil.d(TAG, "share image load");
            ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                    && shareImageMoment.getMessageBitmapArgs() != null) {
                DialogBitmapArgs dialogBitmapArgs = shareImageMoment.getDialogBitmapArgs();
                options = new RequestOptions().error(R.drawable.ic_selfie_album_default_custom)
                        .placeholder(R.drawable.ic_selfie_album_default_custom)
                        .override(this.ivContent.getWidth(), this.ivContent.getHeight())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .signature(new ObjectKey(photoMsg.getLocalPath()))
                        .transform((Transformation<Bitmap>) new CropTransform(dialogBitmapArgs.getCropWidth(),
                                dialogBitmapArgs.getCropHeight(), dialogBitmapArgs.getCutStart(),
                                dialogBitmapArgs.getCutTop()));
            }
        }
        final VideoViewHolder videoHolder = holder instanceof VideoViewHolder ? (VideoViewHolder) holder : null;
        Glide.with(getHolderContext()).load(photoMsg.getLocalPath()).apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException exception, Object model, Target<Drawable> target,
                            boolean isFirstResource) {
                        if (videoHolder != null && videoHolder.videoPlayLogo != null) {
                            videoHolder.videoPlayLogo.setVisibility(View.GONE);
                            videoHolder.videoPlayLogo.setVisibility(View.INVISIBLE);
                        }
                        PhotoViewHolder.this.ivContent.setImageDrawable(ContextCompat.getDrawable(
                                PhotoViewHolder.this.getHolderContext(), R.drawable.ic_selfie_album_default));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                            DataSource dataSource, boolean isFirstResource) {
                        if (videoHolder != null && videoHolder.videoPlayLogo != null) {
                            videoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                })
                .into(new DrawableImageViewTarget(this.ivContent) {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        ImageView imageView = getView();
                        if (videoHolder != null && videoHolder.videoPlayLogo != null) {
                            videoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                            Glide.with(PhotoViewHolder.this.getHolderContext()).load(R.drawable.ic_friends_play)
                                    .into(videoHolder.videoPlayLogo);
                        }
                        imageView.setImageDrawable(drawable);
                    }
                });
        LogUtil.d("load local image complete!!!");
    }
    public void configAndLoadAdvantisePhoto(Context context, final DbMoment moment, final ImageView imageView) {
        Glide.with(context).load(moment.getResource())
                .apply(new RequestOptions().error(R.drawable.ic_selfie_album_default)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(imageView.getWidth(), imageView.getHeight())
                        .transform((Transformation<Bitmap>) new RoundedCorners(DimenUtil.dp2px(context, 4.0f)))
                        .dontAnimate()
                        .signature(new ObjectKey(String.valueOf(Math.random()))))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        PhotoViewHolder.this.loadAdvantagePhoto(moment, drawable, imageView);
                    }
                });
    }

    private void loadAdvantagePhoto(DbMoment moment, Drawable drawable, ImageView imageView) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        int top = 0;
        int left = 0;
        if (moment.getScaleType() == 2) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                float scaledHeight = width * (intrinsicHeight / height);
                left = (int) ((intrinsicWidth - scaledHeight) * 0.5f);
                intrinsicWidth = ((int) scaledHeight) + left;
            } else {
                float scaledWidth = height * (intrinsicWidth / width);
                top = (int) ((intrinsicHeight - scaledWidth) * 0.5f);
                intrinsicHeight = ((int) scaledWidth) + top;
                left = 0;
            }
            LogUtil.d(TAG, "left:" + left + ";top:" + top + ";right:" + intrinsicWidth + ";bottom:" + intrinsicHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap(), left, top,
                    intrinsicWidth - left, intrinsicHeight - top));
            return;
        }
        if (moment.getScaleType() == 0) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                intrinsicWidth = (int) (width * (intrinsicHeight / height));
            } else {
                intrinsicHeight = (int) (height * (intrinsicWidth / width));
            }
            LogUtil.d(TAG, "left:0;top:0;right:" + intrinsicWidth + ";bottom:" + intrinsicHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap(), 0, 0,
                    intrinsicWidth, intrinsicHeight));
            return;
        }
        if (moment.getScaleType() == 1) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                left = (int) (intrinsicWidth - (width * (intrinsicHeight / height)));
            } else {
                top = (int) (intrinsicHeight - (height * (intrinsicWidth / width)));
                left = 0;
            }
            LogUtil.d(TAG, "left:" + left + ";top:" + top + ";right:" + intrinsicWidth + ";bottom:" + intrinsicHeight);
            imageView.setImageBitmap(Bitmap.createBitmap(((BitmapDrawable) drawable).getBitmap(), left, top,
                    intrinsicWidth - left, intrinsicHeight - top));
            return;
        }
        imageView.setImageDrawable(drawable);
    }
    public void loadImageWithKey(final Context context, final ImageView imageView, String key, final String type,
            final DbMoment moment, final boolean roundedCorner, final AbsViewHolder holder) {
        VideoKeyOrToken videoKeyOrToken;
        final String picKey = ((moment.getType().intValue() == 6 || moment.getType().intValue() == 24)
                && (videoKeyOrToken = JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class)) != null)
                ? videoKeyOrToken.getPicKey() : key;
        LogUtil.d(TAG, "loadImageWithKey#key:" + picKey + ";type:" + type + ";Id = " + moment.getMomentId());
        new MomentPhotoServeImpl(context).getDownloadUrl(new FileUrlParam(picKey, type), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                        LogUtil.d(PhotoViewHolder.TAG, "loadImageWithKey#e" + throwable);
                        if ("1".equals(type)) {
                            PhotoViewHolder.this.loadImageWithKey(context, imageView, picKey, "2", moment,
                                    roundedCorner, holder);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        LogUtil.e(PhotoViewHolder.TAG, "onNext: getKey " + url);
                        if (TextUtils.isEmpty(url)) {
                            return;
                        }
                        if (moment.getResource().equals(imageView.getTag(R.id.chat_msg_item_photo_iv))) {
                            PhotoViewHolder.this.glideWithInto(context, imageView, url, roundedCorner, moment, holder);
                        } else {
                            LogUtil.d(PhotoViewHolder.TAG, "控件被复用了，不加载图片");
                        }
                    }
                });
    }

    public void glideWithInto(final Context context, final ImageView imageView, String url, boolean roundedCorner,
            DbMoment moment, AbsViewHolder holder) {
        LogUtil.i(TAG, "glideWithInto " + url + "   " + context);
        final String resource = moment.getResource();
        Glide.with(context).clear(imageView);
        imageView.setImageDrawable(null);
        RequestOptions options = new RequestOptions().error(R.drawable.ic_selfie_album_default)
                .override(imageView.getWidth(), imageView.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(url));
        final RequestOptions playLogoOptions = new RequestOptions().error(R.drawable.ic_friends_play)
                .placeholder(R.drawable.ic_selfie_album_default)
                .override(imageView.getWidth(), imageView.getHeight())
                .dontAnimate()
                .signature(new ObjectKey(Integer.valueOf(R.drawable.ic_selfie_album_default)));
        VideoViewHolder videoHolder = null;
        if ((moment.getType().intValue() == 6 || moment.getType().intValue() == 24)
                && holder instanceof VideoViewHolder) {
            videoHolder = (VideoViewHolder) holder;
            videoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
        }
        if (roundedCorner) {
            options = options.transform((Transformation<Bitmap>) new MultiTransformation(
                    new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, 4.0f))));
        }
        if (8 == moment.getType().intValue()) {
            LogUtil.d(TAG, "share image load");
            ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (shareImageMoment != null && shareImageMoment.getDialogBitmapArgs() != null
                    && shareImageMoment.getMessageBitmapArgs() != null) {
                imageView.getLayoutParams().height = shareImageMoment.getMessageBitmapArgs().getHeight();
                imageView.getLayoutParams().width = shareImageMoment.getMessageBitmapArgs().getWidth();
                DialogBitmapArgs dialogBitmapArgs = shareImageMoment.getDialogBitmapArgs();
                Glide.with(context).load(url)
                        .apply(new RequestOptions().error(R.drawable.ic_selfie_album_default_custom)
                                .override(imageView.getWidth(), imageView.getHeight())
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .signature(new ObjectKey(url))
                                .transform((Transformation<Bitmap>) new CropTransform(dialogBitmapArgs.getCropWidth(),
                                        dialogBitmapArgs.getCropHeight(), dialogBitmapArgs.getCutStart(),
                                        dialogBitmapArgs.getCutTop())))
                        .into(new DrawableImageViewTarget(imageView) {
                            @Override
                            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                                ImageView view = getView();
                                if (view == null || resource == null) {
                                    LogUtil.e(PhotoViewHolder.TAG, "glideWithInfo DialogBitmapArgs: set image error. view: "
                                            + view + ", tag: " + resource);
                                    return;
                                }
                                if (resource.equals(view.getTag(R.id.chat_msg_item_photo_iv))) {
                                    super.onResourceReady(drawable, transition);
                                    LogUtil.i(PhotoViewHolder.TAG, "glideWithInfo DialogBitmapArgs complete!!! tag: " + resource);
                                    return;
                                }
                                LogUtil.w(PhotoViewHolder.TAG, "glideWithInfo DialogBitmapArgs onResourceReady: view is recycled. tag: "
                                        + resource + ", iv.tag: " + view.getTag());
                            }
                        });
                return;
            }
        }
        final VideoViewHolder playLogoHolder = videoHolder;
        Glide.with(context).load(url).apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException exception, Object model, Target<Drawable> target,
                            boolean isFirstResource) {
                        if (playLogoHolder != null && playLogoHolder.videoPlayLogo != null) {
                            playLogoHolder.videoPlayLogo.setVisibility(View.GONE);
                            playLogoHolder.videoPlayLogo.setVisibility(View.INVISIBLE);
                        }
                        if (PhotoViewHolder.this.getHolderContext() == null) {
                            return true;
                        }
                        imageView.setImageDrawable(PhotoViewHolder.this.getHolderContext().getResources()
                                .getDrawable(R.drawable.ic_selfie_album_default));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                            DataSource dataSource, boolean isFirstResource) {
                        if (playLogoHolder != null && playLogoHolder.videoPlayLogo != null) {
                            playLogoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                })
                .into(new DrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        ImageView view = getView();
                        if (playLogoHolder != null && playLogoHolder.videoPlayLogo != null) {
                            playLogoHolder.videoPlayLogo.setVisibility(View.VISIBLE);
                            Glide.with(context).load(R.drawable.ic_friends_play).apply(playLogoOptions)
                                    .into(playLogoHolder.videoPlayLogo);
                        }
                        imageView.setImageDrawable(drawable);
                        if (view == null || resource == null) {
                            LogUtil.e(PhotoViewHolder.TAG, "onResourceReady: set image error. view: " + view + ", tag: " + resource);
                            return;
                        }
                        if (resource.equals(view.getTag(R.id.chat_msg_item_photo_iv))) {
                            LogUtil.i(PhotoViewHolder.TAG, "load local image complete!!! tag: " + resource);
                            return;
                        }
                        LogUtil.w(PhotoViewHolder.TAG, "onResourceReady: view is recycled. tag: " + resource + ", iv.tag: " + view.getTag());
                    }
                });
    }
    public void dislplayNetPhoto(final Context context, final String key, final ImageView imageView,
            final boolean roundedCorner, final DbMoment moment, final AbsViewHolder holder) {
        int type = moment.getType().intValue();
        if (type == 6 || type == 24) {
            VideoMsg videoMsg = JSONUtil.fromJSON(moment.getContent(), VideoMsg.class);
            CloudFileResource icon = videoMsg.getIcon();
            CloudFileResource source = videoMsg.getSource();
            long urlDeadline = Long.MAX_VALUE;
            String downloadUrl = null;
            String cloudKey = null;
            if (icon != null) {
                urlDeadline = icon.getUrlDeadline();
                downloadUrl = icon.getDownloadUrl();
                cloudKey = icon.getKey();
            } else if (source != null) {
                urlDeadline = source.getUrlDeadline();
                downloadUrl = source.getDownloadUrl();
                cloudKey = source.getKey();
            }
            VideoKeyOrToken videoKeyOrToken = JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class);
            LogUtil.i(TAG, "dislplayNetPhoto " + moment + "\n videoKeyOrToken :" + videoKeyOrToken);
            LogUtil.d(TAG, "videoMsg = " + videoMsg.toString());
            String picKey = (!com.xtc.log.util.TextUtils.isEmpty(cloudKey) || videoKeyOrToken == null)
                    ? key : videoKeyOrToken.getPicKey();
            if (System.currentTimeMillis() > urlDeadline) {
                LogUtil.i(TAG, "重新获取下载地址");
                getServerDownloadUrl(context, picKey, imageView, roundedCorner, moment, videoMsg, holder);
                return;
            }
            if (com.xtc.log.util.TextUtils.isEmpty(downloadUrl)) {
                if (com.xtc.log.util.TextUtils.isEmpty(videoMsg.getSource().getDownloadUrl())) {
                    getServerDownloadUrl(context, picKey, imageView, roundedCorner, moment, videoMsg, holder);
                } else {
                    loadNoFailureUrl(context, picKey, imageView, roundedCorner, moment,
                            videoMsg.getSource().getDownloadUrl(), holder);
                }
                return;
            }
            loadNoFailureUrl(context, picKey, imageView, roundedCorner, moment, downloadUrl, holder);
            return;
        }
        pullNewUrlForPhoto(context, key, imageView, roundedCorner, moment, holder);
    }

    private void pullNewUrlForPhoto(final Context context, final String key, final ImageView imageView,
            final boolean roundedCorner, final DbMoment moment, final AbsViewHolder holder) {
        int retryCount = moment.getRetryCount();
        LogUtil.d(TAG, "pullNewUrl: " + moment.getMomentId());
        if (retryCount > 2) {
            LogUtil.d(TAG, "pullNewUrl: 已经重试过两次了 1 不做重试操作！" + moment.getMomentId());
            return;
        }
        moment.setRetryCount(retryCount + 1);
        PhotoMsg photoMsg = JSONUtil.fromJSON(moment.getContent(), PhotoMsg.class);
        LogUtil.d(TAG, "dislplayNetPhoto: else " + photoMsg);
        if (photoMsg == null) {
            return;
        }
        SmallPicSouce smallPic = photoMsg.getSmallPic();
        CloudFileResource source = photoMsg.getSource();
        long urlDeadline;
        String downloadUrl;
        String cloudKey;
        if (smallPic != null) {
            urlDeadline = smallPic.getUrlDeadline();
            downloadUrl = smallPic.getDownloadUrl();
            cloudKey = smallPic.getKey();
        } else if (source != null) {
            urlDeadline = source.getUrlDeadline();
            downloadUrl = source.getDownloadUrl();
            cloudKey = source.getKey();
        } else {
            urlDeadline = Long.MAX_VALUE;
            downloadUrl = null;
            cloudKey = null;
        }
        if (System.currentTimeMillis() > urlDeadline) {
            new MomentPhotoServeImpl(context).getDownloadUrl(new FileUrlParam(cloudKey, photoMsg.getType()), photoMsg, moment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpSubscriber<String>() {
                        @Override
                        public void onHttpError(Throwable throwable) {
                        }

                        @Override
                        public void onNext(String url) {
                            PhotoViewHolder.this.loadNoFailureUrl(context, key, imageView, roundedCorner, moment, url, holder);
                        }
                    });
            return;
        }
        final String freshUrl = downloadUrl;
        HandlerUtil.runOnUIThreadNoCheck(new Runnable() {
            @Override
            public void run() {
                PhotoViewHolder.this.loadNoFailureUrl(context, key, imageView, roundedCorner, moment, freshUrl, holder);
            }
        });
    }

    public void getServerDownloadUrl(final Context context, String key, final ImageView imageView,
            final boolean roundedCorner, final DbMoment moment, VideoMsg videoMsg, final AbsViewHolder holder) {
        int retryCount = moment.getRetryCount();
        LogUtil.d(TAG, "pullNewUrl: " + moment.getMomentId());
        if (retryCount > 2) {
            LogUtil.d(TAG, "pullNewUrl: 已经重试过两次了 不做重试操作！" + moment.getMomentId());
            return;
        }
        moment.setRetryCount(retryCount + 1);
        MomentPhotoServeImpl photoServe = new MomentPhotoServeImpl(context);
        ArrayList<String> keys = new ArrayList<>();
        CloudFileResource icon = videoMsg.getIcon();
        CloudFileResource source = videoMsg.getSource();
        if (icon != null) {
            keys.add(icon.getKey());
        }
        if (source != null) {
            keys.add(source.getKey());
        }
        photoServe.getDownloadBatchUrl(new FileBatchUrlParam(keys), moment, videoMsg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<DownloadUrlVo>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(DownloadUrlVo downloadUrlVo) {
                        LogUtil.i(PhotoViewHolder.TAG, "onNext onNext ： " + downloadUrlVo);
                        for (int index = 0; index < downloadUrlVo.getUrls().size(); index++) {
                            CloudFileResource url = downloadUrlVo.getUrls().get(index);
                            if (Utils.isIconKey(url.getKey())) {
                                PhotoViewHolder.this.glideWithInto(context, imageView, url.getDownloadUrl(),
                                        roundedCorner, moment, holder);
                                return;
                            }
                        }
                    }
                });
    }

    public void loadNoFailureUrl(Context context, String key, ImageView imageView, boolean roundedCorner,
            DbMoment moment, String downloadUrl, AbsViewHolder holder) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        if (key != null && key.equals(imageView.getTag(R.id.chat_msg_item_photo_iv))) {
            LogUtil.d(TAG, "loadNoFailureUrl: -- downloadUrl=" + downloadUrl);
            glideWithInto(context, imageView, downloadUrl, roundedCorner, moment, holder);
            return;
        }
        LogUtil.d(TAG, "控件被复用了，不加载图片");
    }
}
