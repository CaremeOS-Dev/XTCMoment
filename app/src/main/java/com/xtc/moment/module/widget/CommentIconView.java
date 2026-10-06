package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xtc.moment.R;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;

/**
 * 评论图标控件，禁用发送时切换为不可评论样式。
 */
public class CommentIconView extends RelativeLayout {

    private ImageView ivComment;
    private int useResId = R.drawable.ic_comment;

    public CommentIconView(Context context) {
        this(context, null);
    }

    public CommentIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.ivComment = (ImageView) LayoutInflater.from(getContext())
                .inflate(R.layout.view_moment_comment, this).findViewById(R.id.ivComment);
    }

    public void setIcon() {
        int resId = IllegalMessageHandler.getInstance(getContext()).isDisableSend()
                ? R.drawable.ic_comment_not
                : R.drawable.ic_comment;
        if (resId == this.useResId) {
            return;
        }
        this.useResId = resId;
        this.ivComment.setImageResource(resId);
    }
}