package com.xtc.moment.module.like.viewholder;

import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.module.widget.MomentOfficialPhotoH5TextViewComment;
/**
 * OfficialPhotoH5ViewHolder 列表项 ViewHolder。
 */
public class OfficialPhotoH5ViewHolder extends AbsViewHolder {
    public OfficialPhotoH5ViewHolder(View view) {
        super(view);
        this.commentPraiseView = (CommentPraiseView) this.itemView.findViewById(R.id.comment_praise);
        this.momentView = (MomentOfficialPhotoH5TextViewComment) itemView.findViewById(R.id.moment);
    }
}
