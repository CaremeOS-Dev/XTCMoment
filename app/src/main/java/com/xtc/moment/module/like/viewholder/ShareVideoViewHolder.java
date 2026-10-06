package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentShareVideoViewComment;
import com.xtc.moment.widget.LbsLayout;
/**
 * ShareVideoViewHolder 列表项 ViewHolder。
 */
public class ShareVideoViewHolder extends AbsViewHolder {
    public ShareVideoViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentShareVideoViewComment) itemView.findViewById(R.id.moment);
        this.llLbs = (LbsLayout) view.findViewById(R.id.ll_lbs);
    }
}
