package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentShareImageViewComment;
/**
 * ShareImageViewHolder 列表项 ViewHolder。
 */
public class ShareImageViewHolder extends AbsViewHolder {
    public ShareImageViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentShareImageViewComment) itemView.findViewById(R.id.moment);
    }
}
