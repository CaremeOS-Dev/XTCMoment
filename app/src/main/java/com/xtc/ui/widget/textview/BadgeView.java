package com.xtc.ui.widget.textview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xtc.moment.R;
import com.xtc.utils.ui.DimenUtil;

/** 未读数量角标：根据数量调整宽高并切换显示/隐藏。 */
public class BadgeView extends TextView {

    public BadgeView(Context context) {
        super(context);
        init(context);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setTextColor(-1);
        setTextSize(DimenUtil.px2sp(context, 24.0f));
        setGravity(17);
        setBackgroundResource(R.drawable.bg_unread_count);
    }

    public BadgeView setWidthAndHeight(int width, int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        invalidate();
        return this;
    }

    public BadgeView setBadgeCount(int count) {
        if (count > 99) {
            count = 99;
        }
        if (count > 9) {
            setWidthAndHeight(46, 36);
        } else {
            setWidthAndHeight(36, 36);
        }
        if (count < 0) {
            setText("");
        } else {
            setText(String.valueOf(count));
        }
        setVisibility(count == 0 ? 4 : 0);
        return this;
    }
}