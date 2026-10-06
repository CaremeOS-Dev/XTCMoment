package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;

/**
 * Moment row for an official/advert photo: loads the resource directly with the moment scale type.
 */
public class MomentOfficialPhotoView extends MomentPhotoView {

    private static final String TAG = MomentOfficialPhotoView.class.toString();

    private static final int IMAGE_CORNER_DP = 4;

    public MomentOfficialPhotoView(Context context) {
        this(context, null);
    }

    public MomentOfficialPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentOfficialPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        super.initView();
        this.ivOfficialEnterpriseIcon = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise_bg);
    }

    @Override
    public void loadDefaultImage(Context context, int resId) {
        super.loadDefaultImage(context, resId);
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
        if (TextUtils.isEmpty(moment.getResource())) {
            return;
        }
        configAndLoadAdvantisePhoto(context, moment);
    }

    private void configAndLoadAdvantisePhoto(Context context, DbMoment moment) {
        this.mContent.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(moment.getResource())
                .apply(new RequestOptions()
                        .error(R.drawable.ic_selfie_album_default)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .override(this.mContent.getWidth(), this.mContent.getHeight())
                        .transform(new GlideRoundImageView(context, IMAGE_CORNER_DP, moment.getScaleType()))
                        .dontAnimate()
                        .signature(new ObjectKey(String.valueOf(Math.random()))))
                .into(this.mContent);
    }

    @Override
    public void setTags(int key, Object value) {
        this.mContent.setTag(key, value);
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    @Override
    public void setContentOnClickListener(Context context, final DbMoment moment,
                                          final AbsMomentView.OnContentOnClickListener listener) {
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + moment.toString());
                if (listener != null) {
                    listener.previewPhoto(moment.getResource());
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
        this.mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment.toString());
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}