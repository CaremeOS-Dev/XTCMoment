package com.xtc.moment.module.share.holder;

import android.content.Context;
import android.text.TextUtils;
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
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.widget.CommentIconView;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.module.widget.MomentLikeView;
import com.xtc.moment.module.widget.MomentReminderView;
import com.xtc.utils.encode.JSONUtil;

/**
 * 分享视频动态的 ViewHolder，展示来源应用信息与视频文案。
 */
public class ShareVideoHolder extends VideoViewHolder {

    private static final String TAG = "个人主页 分享视频ViewHolder ShareVideoHolder";

    ImageView ivAppIcon;
    RelativeLayout rlShareContent;
    TextView shareContents;
    TextView tvAppName;
    MomentContentView tvNoSupport;

    public ShareVideoHolder(View view) {
        super(view);
        this.ivAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
        this.tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
        this.shareContents = (TextView) view.findViewById(R.id.share_video_contents);
        this.rlShareContent = (RelativeLayout) view.findViewById(R.id.rl_share_content);
        this.tvNoSupport = (MomentContentView) view.findViewById(R.id.tv_no_support);
        this.ivMomentRange = (ImageView) view.findViewById(R.id.iv_moment_range);
        this.ivMomentComment = (CommentIconView) view.findViewById(R.id.iv_moment_comment);
        this.momentLike = (MomentLikeView) view.findViewById(R.id.moment_like);
        this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        super.loadImage(context, moment, holder);
        String content = moment.getContent();
        ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(content, ShareVideoMoment.class);
        if (shareVideoMoment == null) {
            LogUtil.w(TAG, "share image moment is null!");
            this.tvNoSupport.setVisibility(View.VISIBLE);
            this.rlShareContent.setVisibility(View.GONE);
            return;
        }
        LogUtil.d(TAG, "loadImage: " + content);
        LogUtil.d(TAG, "loadImage: " + shareVideoMoment.getAppName());
        if (this.rlShareContent != null && this.tvNoSupport != null) {
            this.tvNoSupport.setVisibility(View.GONE);
            this.rlShareContent.setVisibility(View.VISIBLE);
        }
        super.loadImage(context, moment, holder);
        this.tvAppName.setText(shareVideoMoment.getAppName());
        if (shareVideoMoment.getAppIcon() != null) {
            Glide.with(context).load(shareVideoMoment.getAppIcon())
                    .apply(new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random()))))
                    .into(this.ivAppIcon);
        }
        this.shareContents.setVisibility(View.GONE);
        if (TextUtils.isEmpty(shareVideoMoment.getTextMsg())) {
            return;
        }
        this.shareContents.setText(shareVideoMoment.getTextMsg());
        this.shareContents.setVisibility(View.VISIBLE);
    }
}