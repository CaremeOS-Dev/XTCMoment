package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentShareTextView;
/**
 * ShareTextViewHolder 列表项 ViewHolder。
 */
public class ShareTextViewHolder extends AbsViewHolder {
    public ShareTextViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentShareTextView) itemView.findViewById(R.id.moment);
    }
}
