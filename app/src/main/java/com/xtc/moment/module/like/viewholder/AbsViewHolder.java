package com.xtc.moment.module.like.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xtc.moment.module.bean.MomentNewMsgBean;
import com.xtc.moment.module.widget.AbsMomentView;
import com.xtc.moment.module.widget.CommentPraiseView;
import com.xtc.moment.widget.LbsLayout;

/**
 * 点赞列表 ViewHolder 基类。
 */
public abstract class AbsViewHolder extends RecyclerView.ViewHolder {

    public CommentPraiseView commentPraiseView;
    public LbsLayout llLbs;
    public AbsMomentView momentView;

    public AbsViewHolder(View itemView) {
        super(itemView);
    }

    public void setCommentType(int type, Drawable drawable, MomentNewMsgBean message) {
        CommentPraiseView view = this.commentPraiseView;
        if (view != null) {
            view.setCommentType(type, drawable, message);
        }
    }

    public void setAccountName(String accountName) {
        CommentPraiseView view = this.commentPraiseView;
        if (view != null) {
            view.setAccountName(accountName);
        }
    }

    public void setTime(Context context, long time) {
        CommentPraiseView view = this.commentPraiseView;
        if (view != null) {
            view.setTime(context, time);
        }
    }

    public void setComment(String comment) {
        CommentPraiseView view = this.commentPraiseView;
        if (view != null) {
            view.setComment(comment);
        }
    }

    public void hideView() {
        AbsMomentView view = this.momentView;
        if (view != null) {
            view.hideView();
        }
    }

    public void showView() {
        AbsMomentView view = this.momentView;
        if (view != null) {
            view.showView();
        }
    }
}