package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 动态评论子项视图基类。
 */
public abstract class AbsMomentCommentItemView extends View {

    public AbsMomentCommentItemView(Context context) {
        super(context);
    }

    public AbsMomentCommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsMomentCommentItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}