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
import com.xtc.moment.module.bean.ShareWebMoment;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.util.SystemUtil;
import com.xtc.utils.encode.JSONUtil;

import android.net.Uri;
import android.text.TextUtils;

/**
 * 分享网页动态的 ViewHolder，点击打开 H5。
 */
public class ShareWebViewHolder extends PhotoViewHolder {

    private static final String TAG = "ShareWebViewHolder";

    ImageView ivAppIcon;
    MomentContentView mDescriptions;
    MomentContentView mNoSupport;
    RelativeLayout rlShareContent;
    TextView tvAppName;

    public ShareWebViewHolder(View view) {
        super(view);
        this.mDescriptions = (MomentContentView) view.findViewById(R.id.tv_moment_description);
        this.mNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
        this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        if (TextUtils.isEmpty(moment.getResource())) {
            LogUtil.w(TAG, "momentBean.getResource == null");
            return;
        }
        super.loadImage(context, moment, holder);
        ShareWebMoment shareWebMoment = JSONUtil.fromJSON(moment.getContent(), ShareWebMoment.class);
        if (shareWebMoment == null) {
            this.mNoSupport.setVisibility(View.VISIBLE);
            this.rlShareContent.setVisibility(View.GONE);
            return;
        }
        this.mNoSupport.setVisibility(View.GONE);
        this.rlShareContent.setVisibility(View.VISIBLE);
        String desc = shareWebMoment.getDesc();
        if (TextUtils.isEmpty(desc)) {
            this.mDescriptions.setVisibility(View.GONE);
        } else {
            LogUtil.d(TAG, "Description:" + desc);
            this.mDescriptions.setVisibility(View.VISIBLE);
            this.mDescriptions.setContext(context);
            this.mDescriptions.setText(desc);
        }
        this.tvAppName.setText(shareWebMoment.getAppName());
        if (shareWebMoment.getAppIcon() != null) {
            Glide.with(context).load(shareWebMoment.getAppIcon())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.ivAppIcon);
        }
    }

    @Override
    public void setContentOnClickListener(final DbMoment moment, final ShareAdapter.OnContentOnClickListener listener) {
        if (moment == null) {
            LogUtil.d(TAG, "click share app, moment bean is null! ");
            return;
        }
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.d(ShareWebViewHolder.TAG, "isFastDoubleClick");
                    return;
                }
                if (listener == null || !ShareWebViewHolder.this.ismIsSelf()) {
                    return;
                }
                Uri.Builder builder = Uri.parse(JSONUtil.fromJSON(moment.getContent(), ShareWebMoment.class)
                        .getWebLink()).buildUpon();
                builder.appendQueryParameter("momentWatchId", moment.getWatchId());
                builder.appendQueryParameter("momentId", moment.getMomentId());
                listener.previewH5(builder.build().toString());
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(final DbMoment moment, final ShareAdapter.OnContentOnLongClickListener listener) {
        this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener == null || !ShareWebViewHolder.this.ismIsSelf()) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }
}