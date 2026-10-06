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
import com.xtc.moment.module.widget.MomentReminderView;
import com.xtc.moment.module.widget.MomentView;
import com.xtc.moment.widget.LbsLayout;
/**
 * ViewHolder 列表项 ViewHolder。
 */
public class ViewHolder extends AbsViewHolder {
    public ViewHolder(View view) {
        super(view);
        this.momentView = (MomentView) view.findViewById(R.id.moment);
        this.ivBanner = (ImageView) view.findViewById(R.id.iv_banner);
        this.ivLbs = (ImageView) view.findViewById(R.id.iv_lbs);
        this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
        this.tvTime = (TextView) view.findViewById(R.id.tv_time);
        this.momentLike = (MomentLikeView) view.findViewById(R.id.moment_like);
        this.momentReminderView = (MomentReminderView) view.findViewById(R.id.moment_reminder);
        this.ivMomentComment = (CommentIconView) view.findViewById(R.id.iv_moment_comment);
        this.momentCommentView = (MainMomentCommentView) view.findViewById(R.id.moment_comment);
        this.commentRecyclerView = (RecyclerView) view.findViewById(R.id.moment_comment_rec);
        if (this.commentRecyclerView != null) {
            this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        this.ivMomentRange = (ImageView) view.findViewById(R.id.iv_moment_range);
    }
}
