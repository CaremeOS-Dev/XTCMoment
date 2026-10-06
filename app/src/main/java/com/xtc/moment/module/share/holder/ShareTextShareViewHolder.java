package com.xtc.moment.module.share.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.ShareTextPublish;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.utils.encode.JSONUtil;

/**
 * 分享文本动态的 ViewHolder，正文内容来自分享文本实体。
 */
public class ShareTextShareViewHolder extends ShareViewHolder {

    private static final String TAG = "ShareTextViewHolder";

    ImageView appIcon;
    TextView appName;
    RelativeLayout rlShareContent;
    MomentContentView tvNoSupport;

    public ShareTextShareViewHolder(View view) {
        super(view);
        this.appIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        this.appName = (TextView) view.findViewById(R.id.tv_app_name);
        this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
        this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
        this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.ivShareReportIcon = (ImageView) view.findViewById(R.id.iv_share_account_report);
    }

    @Override
    public void setTvContent(String resource, String text) {
        ShareTextPublish shareTextPublish = JSONUtil.fromJSON(text, ShareTextPublish.class);
        if (shareTextPublish == null) {
            this.tvNoSupport.setVisibility(View.VISIBLE);
            this.rlShareContent.setVisibility(View.GONE);
            return;
        }
        this.tvNoSupport.setVisibility(View.GONE);
        this.rlShareContent.setVisibility(View.VISIBLE);
        this.tvContent.setContext(getHolderContext());
        this.tvContent.setText(shareTextPublish.getContent());
        this.appName.setText(shareTextPublish.getAppName());
        if (shareTextPublish.getAppIcon() != null) {
            Glide.with(this.appIcon.getContext()).load(shareTextPublish.getAppIcon())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.appIcon);
        }
    }

    @Override
    public void setTvContent(int resId, String text) {
        this.tvContent.setContext(getHolderContext());
        this.tvContent.setRichText(resId, text);
    }
}