package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.utils.ui.DimenUtil;

/**
 * Comment page variant of {@link MomentOfficialVideoView}.
 */
public class MomentOfficialVideoViewComment extends MomentVideoViewComment {

    private static final String TAG = "MomentOfficialVideoView";

    private static final int THUMBNAIL_CORNER_DP = 4;

    private ImageView ivVideoPlayLogo;
    private MomentContentView tvDescription;

    public MomentOfficialVideoViewComment(Context context) {
        this(context, null);
    }

    public MomentOfficialVideoViewComment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentOfficialVideoViewComment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        android.view.View view = LayoutInflater.from(getContext()).inflate(R.layout.view_official_video_comment, this);
        this.mIcon = (ImageView) view.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) view.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) view.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) view.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) view.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) view.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) view.findViewById(R.id.iv_official_video_thumbnail);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.tvDescription = (MomentContentView) view.findViewById(R.id.tv_moment_description);
        this.rlMomentSender = (RelativeLayout) view.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) view.findViewById(R.id.iv_account_report);
        this.ivVideoPlayLogo = (ImageView) view.findViewById(R.id.iv_official_video_logo);
    }

    @Override
    public void loadDefaultImage(Context context, int resId) {
        super.loadDefaultImage(context, resId);
    }

    @Override
    public void loadImage(final Context context, DbMoment moment) {
        if (checkContextIsNull(context)) {
            return;
        }
        if (this.mContent == null) {
            LogUtil.w(TAG, "loadImage mContent == null");
            return;
        }
        if (moment == null) {
            LogUtil.w(TAG, "loadImage: momentBean is null");
            return;
        }
        final String resource = moment.getResource();
        if (TextUtils.isEmpty(moment.getDescription())) {
            this.tvDescription.setVisibility(GONE);
        } else {
            LogUtil.i(TAG, "loadImage description: " + moment.getDescription());
            this.tvDescription.setContext(getMyContext());
            this.tvDescription.setVisibility(VISIBLE);
            this.tvDescription.setRichText("", moment.getDescription());
        }
        this.mContent.setScaleType(ImageView.ScaleType.FIT_XY);
        String dataUrl = moment.getDataUrl();
        if (TextUtils.isEmpty(dataUrl)) {
            LogUtil.w(TAG, "loadImage: thumbnailPath is empty");
            this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
            return;
        }
        RequestOptions thumbnailOptions = new RequestOptions()
                .error(R.drawable.ic_selfie_album_default)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .transform(new GlideRoundImageView(context, THUMBNAIL_CORNER_DP, moment.getScaleType()))
                .dontAnimate()
                .signature(new ObjectKey(String.valueOf(Math.random())));
        final RequestOptions logoOptions = new RequestOptions()
                .transform(new RoundedCorners(DimenUtil.dp2px(context, THUMBNAIL_CORNER_DP)))
                .override(this.mContent.getWidth(), this.mContent.getHeight())
                .dontAnimate();
        Glide.with(context).load(dataUrl)
                .apply(thumbnailOptions)
                .into(new DrawableImageViewTarget(this.mContent) {
                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        if (ivVideoPlayLogo != null) {
                            ivVideoPlayLogo.setVisibility(GONE);
                        }
                        mContent.setImageResource(R.drawable.ic_selfie_album_default);
                    }

                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        if (ivVideoPlayLogo != null) {
                            Glide.with(context).load(Integer.valueOf(R.drawable.ic_friends_play))
                                    .apply(logoOptions)
                                    .into(ivVideoPlayLogo);
                            ivVideoPlayLogo.setVisibility(VISIBLE);
                        }
                        ImageView view = getView();
                        if (view == null || resource == null) {
                            LogUtil.e(TAG, "onResourceReady: set image error. view: " + view + ", tag: " + resource);
                            return;
                        }
                        if (resource.equals(view.getTag(R.id.moment))) {
                            super.onResourceReady(drawable, transition);
                            LogUtil.i(TAG, "load image complete!!! tag: " + resource);
                            return;
                        }
                        LogUtil.w(TAG, "onResourceReady: view is recycled. tag: " + resource + ", iv.tag: " + view.getTag());
                    }
                });
    }
}