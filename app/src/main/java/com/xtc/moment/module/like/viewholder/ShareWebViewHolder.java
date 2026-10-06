package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentShareWebView;
/**
 * ShareWebViewHolder 列表项 ViewHolder。
 */
public class ShareWebViewHolder extends AbsViewHolder {
    public ShareWebViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentShareWebView) itemView.findViewById(R.id.moment);
    }
}
