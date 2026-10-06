package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentVideoViewComment;
import com.xtc.moment.widget.LbsLayout;
/**
 * VideoViewHolder 列表项 ViewHolder。
 */
public class VideoViewHolder extends AbsViewHolder {
    public VideoViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentVideoViewComment) itemView.findViewById(R.id.moment);
        this.llLbs = (LbsLayout) this.momentView.findViewById(R.id.ll_lbs);
    }
}
