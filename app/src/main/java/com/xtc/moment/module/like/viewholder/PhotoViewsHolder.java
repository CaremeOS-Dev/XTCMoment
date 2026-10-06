package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentPhotoView;
import com.xtc.moment.module.widget.MomentPhotoView;
import com.xtc.moment.widget.LbsLayout;
/**
 * PhotoViewsHolder 列表项 ViewHolder。
 */
public class PhotoViewsHolder extends AbsViewHolder {
    public PhotoViewsHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentPhotoView) itemView.findViewById(R.id.moment_new_vp);
        this.llLbs = (LbsLayout) this.itemView.findViewById(R.id.ll_lbs1);
    }
}
