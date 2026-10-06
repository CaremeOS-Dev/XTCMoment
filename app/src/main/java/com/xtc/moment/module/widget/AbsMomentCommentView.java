package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 动态评论视图基类。
 */
public abstract class AbsMomentCommentView extends LinearLayout {

    public abstract void initView();

    public AbsMomentCommentView(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public AbsMomentCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public AbsMomentCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setOrientation(VERTICAL);
    }
}