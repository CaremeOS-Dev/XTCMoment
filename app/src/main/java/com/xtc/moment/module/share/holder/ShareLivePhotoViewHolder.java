package com.xtc.moment.module.share.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.utils.encode.JSONUtil;

/**
 * 分享实况照片动态的 ViewHolder。
 */
public class ShareLivePhotoViewHolder extends LivePhotoViewHolder {

    private static final String TAG = "ShareLivePhotoViewHolde";

    ImageView ivAppIcon;
    RelativeLayout rlShareContent;
    TextView tvAppName;
    MomentContentView tvNoSupport;

    public ShareLivePhotoViewHolder(View view) {
        super(view);
        this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
        this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        ShareImageMoment shareImageMoment = JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
        if (shareImageMoment == null) {
            LogUtil.w(TAG, "share image moment is null!");
            this.tvNoSupport.setVisibility(View.VISIBLE);
            this.rlShareContent.setVisibility(View.GONE);
            return;
        }
        this.tvNoSupport.setVisibility(View.GONE);
        this.rlShareContent.setVisibility(View.VISIBLE);
        super.loadImage(context, moment, holder);
        this.tvAppName.setText(shareImageMoment.getAppName());
        if (shareImageMoment.getAppIcon() != null) {
            Glide.with(context).load(shareImageMoment.getAppIcon())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.ivAppIcon);
        }
    }
}