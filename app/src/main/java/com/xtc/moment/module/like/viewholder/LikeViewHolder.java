package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentView;
import com.xtc.moment.widget.LbsLayout;
/**
 * LikeViewHolder 列表项 ViewHolder。
 */
public class LikeViewHolder extends AbsViewHolder {
    public LikeViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentView) view.findViewById(R.id.moment);
        if (this.momentView != null) {
            this.llLbs = (LbsLayout) this.momentView.findViewById(R.id.ll_lbs);
        }
    }
}
