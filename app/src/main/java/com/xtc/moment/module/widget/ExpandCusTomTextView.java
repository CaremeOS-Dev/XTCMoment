package com.xtc.moment.module.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 测量后回调是否超出最大行数的 TextView。
 */
public class ExpandCusTomTextView extends AppCompatTextView {

    private static final String TAG = "ExpandCusTomTextView";

    private OnMeasureCallback onMeasureCallback;

    interface OnMeasureCallback {
        void measureBack(boolean exceeded);
    }

    public ExpandCusTomTextView(Context context) {
        super(context);
    }

    public ExpandCusTomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        OnMeasureCallback callback = this.onMeasureCallback;
        if (callback != null) {
            callback.measureBack(getLineCount() > getMaxLines());
        }
    }

    public void setOnMeasureCallback(OnMeasureCallback callback) {
        this.onMeasureCallback = callback;
    }

    public void removeMeasureCallback() {
        if (this.onMeasureCallback != null) {
            this.onMeasureCallback = null;
        }
    }
}