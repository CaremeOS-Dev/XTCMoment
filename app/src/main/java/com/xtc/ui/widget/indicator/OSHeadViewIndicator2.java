package com.xtc.ui.widget.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/** 页头 + 指示器组合控件（已废弃）。 */
@Deprecated
public class OSHeadViewIndicator2 extends FrameLayout {
    private static final String TAG = "OSHeadViewIndicator2";
    private OSHeadViewIndicator osHeadIndicatorView;
    private HeadView osHeadView;
    private LinearLayout rootView;

    public OSHeadViewIndicator2(Context context, int index, int count, String title, int titleColor, boolean hideAddBtn) {
        this(context, null, index, count, title, titleColor, hideAddBtn);
    }

    public OSHeadViewIndicator2(Context context, AttributeSet attrs, int index, int count, String title, int titleColor, boolean hideAddBtn) {
        super(context, attrs);
        LogUtil.i(TAG, "HeadView: current view: index = " + index + "count = " + count);
        if (index > count) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index, count));
        }
        LayoutInflater.from(context).inflate(R.layout.view_os_head_indicator2, this);
        setLayoutParams(new ViewGroup.LayoutParams(-1, context.getResources().getDimensionPixelSize(R.dimen.dp_31)));
        this.rootView = (LinearLayout) findViewById(R.id.ll_os_hv_root2);
        this.osHeadIndicatorView = new OSHeadViewIndicator(context, index, count);
        this.osHeadIndicatorView.getLayoutParams().height = context.getResources().getDimensionPixelSize(R.dimen.dp_9);
        this.rootView.addView(this.osHeadIndicatorView);
        this.osHeadView = new HeadView(context);
        this.osHeadView.setTitleText(title);
        this.osHeadView.setTitleColor(titleColor);
        if (hideAddBtn) {
            this.osHeadView.hideAddBtn();
        } else {
            this.osHeadView.showAddBtn();
        }
        this.rootView.addView(this.osHeadView);
    }

    public HeadView getOsHeadView() {
        return this.osHeadView;
    }

    public OSHeadViewIndicator getOsHeadIndicatorView() {
        return this.osHeadIndicatorView;
    }

    private String outOfBoundsMsg(int index, int count) {
        return "Index: " + index + ", Count: " + count;
    }
}