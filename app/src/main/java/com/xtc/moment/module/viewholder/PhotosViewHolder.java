package com.xtc.moment.module.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentIconView;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentLikeView;
import com.xtc.moment.module.widget.MomentPhotosView;
import com.xtc.moment.module.widget.MomentReminderView;
import com.xtc.moment.widget.LbsLayout;
/**
 * PhotosViewHolder 列表项 ViewHolder。
 */
public class PhotosViewHolder extends AbsViewHolder {
    public PhotosViewHolder(View view) {
        super(view);
        this.momentView = (MomentPhotosView) view.findViewById(R.id.moment_vp);
        this.ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
        this.ivLbs = (ImageView) view.findViewById(R.id.iv_lbs);
        this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
        this.tvTime = (TextView) view.findViewById(R.id.tv_time);
        this.momentLike = (MomentLikeView) view.findViewById(R.id.moment_like);
        this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        this.ivMomentComment = (CommentIconView) view.findViewById(R.id.iv_moment_comment);
        this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
        this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.ivMomentRange = (ImageView) view.findViewById(R.id.iv_moment_range);
    }
}
