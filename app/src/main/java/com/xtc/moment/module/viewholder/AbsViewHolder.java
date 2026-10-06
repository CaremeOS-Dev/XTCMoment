package com.xtc.moment.module.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.viewholder.comment.MomentCommentAdapter;
import com.xtc.moment.module.widget.AbsMomentView;
import com.xtc.moment.module.widget.CommentIconView;
import com.xtc.moment.module.widget.MainMomentCommentView;
import com.xtc.moment.module.widget.MomentLikeView;
import com.xtc.moment.module.widget.MomentReminderView;
import com.xtc.moment.widget.LbsLayout;

/**
 * 好友圈列表项 ViewHolder 基类。
 */
public class AbsViewHolder extends RecyclerView.ViewHolder {

    public MomentCommentAdapter commentAdapter;
    public RecyclerView commentRecyclerView;
    public ImageView ivBanner;
    public ImageView ivLbs;
    public CommentIconView ivMomentComment;
    public ImageView ivMomentRange;
    public LbsLayout llLbs;
    public MainMomentCommentView momentCommentView;
    public MomentLikeView momentLike;
    public MomentReminderView momentReminderView;
    public AbsMomentView momentView;
    public TextView tvContext;
    public TextView tvTime;

    private Context context;
    private int datePosition;
    private DbMoment dbMoment;

    public AbsViewHolder(View itemView) {
        super(itemView);
        this.commentAdapter = new MomentCommentAdapter(itemView.getContext());
    }

    public int getMomentContentHeight() {
        int height = 0;
        CommentIconView commentIconView = this.ivMomentComment;
        if (commentIconView != null) {
            height += commentIconView.getHeight();
        }
        MomentLikeView likeView = this.momentLike;
        return likeView != null ? height + likeView.getHeight() : height;
    }

    public void showView() {
        AbsMomentView view = this.momentView;
        if (view != null) {
            view.showView();
        }
    }

    public void hideComment() {
        CommentIconView commentIconView = this.ivMomentComment;
        if (commentIconView != null) {
            commentIconView.setVisibility(View.GONE);
        }
    }

    public void showComment() {
        CommentIconView commentIconView = this.ivMomentComment;
        if (commentIconView != null) {
            commentIconView.setVisibility(View.VISIBLE);
            this.ivMomentComment.setIcon();
        }
    }

    public void hideRecyclerComment() {
        RecyclerView recyclerView = this.commentRecyclerView;
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        MainMomentCommentView commentView = this.momentCommentView;
        if (commentView == null) {
            return;
        }
        commentView.setVisibility(View.VISIBLE);
        DbMoment moment = this.dbMoment;
        if (moment == null || moment.getCommentsTotalCount() != 0) {
            return;
        }
        this.momentCommentView.setVisibility(View.GONE);
    }

    public void showRecyclerComment() {
        RecyclerView recyclerView = this.commentRecyclerView;
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        MainMomentCommentView commentView = this.momentCommentView;
        if (commentView != null) {
            commentView.setVisibility(View.GONE);
        }
    }

    public int getDatePosition() {
        return this.datePosition;
    }

    public void setDatePosition(int datePosition) {
        this.datePosition = datePosition;
    }

    public DbMoment getDbMoment() {
        return this.dbMoment;
    }

    public void setDbMoment(DbMoment moment) {
        this.dbMoment = moment;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}