package com.xtc.moment.module.viewholder.comment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.util.SystemUtil;

import java.util.List;

/**
 * 单条评论列表项。
 */
public class CommentViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "CommentViewHolder";

    public TextView tvComment;

    private DbMomentComment comment;
    private List<DbMomentComment> momentComments;
    private MomentCommentAdapter.OnMomentCommentListener listener;
    private MomentCommentAdapter.OnCommentDeleteListener deleteListener;

    public CommentViewHolder(View itemView) {
        super(itemView);
        this.tvComment = (TextView) itemView.findViewById(R.id.item_tv_comment);
        this.tvComment.setOnClickListener(this);
        this.tvComment.setOnLongClickListener(this);
    }

    public void setComment(DbMomentComment comment) {
        this.comment = comment;
    }

    public void setDbMomentComments(List<DbMomentComment> comments) {
        this.momentComments = comments;
    }

    @Override
    public void onClick(View view) {
        if (SystemUtil.isFastDoubleClick()) {
            LogUtil.i(TAG, "onClick: click too fast.");
            return;
        }
        LogUtil.d(TAG, "onClick: -------" + this.momentComments);
        if (this.momentComments == null) {
            return;
        }
        for (int index = 0; index < this.momentComments.size(); index++) {
            if (this.comment.getCommentId().equals(this.momentComments.get(index).getCommentId())
                    && this.listener != null
                    && !this.momentComments.isEmpty()) {
                this.listener.onMomentCommentClick(index, this.comment);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (this.momentComments == null) {
            return false;
        }
        for (int index = 0; index < this.momentComments.size(); index++) {
            if (this.comment.getCommentId().equals(this.momentComments.get(index).getCommentId())
                    && this.listener != null
                    && !this.momentComments.isEmpty()) {
                this.deleteListener.onCommentDeleteClick(index, this.comment);
                return true;
            }
        }
        return false;
    }

    public void setOnMomentCommentListener(MomentCommentAdapter.OnMomentCommentListener listener) {
        this.listener = listener;
    }

    public void setOnCommentDeleteListener(MomentCommentAdapter.OnCommentDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }
}