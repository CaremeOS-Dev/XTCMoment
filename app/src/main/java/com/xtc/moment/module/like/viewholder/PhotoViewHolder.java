package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentPhotoView;
import com.xtc.moment.widget.LbsLayout;
/**
 * PhotoViewHolder 列表项 ViewHolder。
 */
public class PhotoViewHolder extends AbsViewHolder {
    public PhotoViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentPhotoView) itemView.findViewById(R.id.moment);
        this.llLbs = (LbsLayout) this.momentView.findViewById(R.id.ll_lbs);
    }
}
