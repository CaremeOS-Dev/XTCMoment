package com.xtc.ui.widget.scalablecontainer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.PressAnimHelper;
import com.xtc.ui.widget.util.TypeArrayUtils;
import com.xtc.ui.widget.util.UiTouchPointUtil;

/** 带按压缩放反馈的线性布局，可指定不参与按压反馈的子视图。 */
public class AppLinearLayout extends LinearLayout {
    private static final String TAG = "AppLinearLayout";
    int extraBottom;
    int extraLeft;
    int extraRight;
    int extraTop;
    private View forbidView;
    private PressAnimHelper pressAnimHelper;
    private final Runnable pressTask;
    private final Runnable releaseTask;
    private Point touchPoint;

    public AppLinearLayout(Context context) {
        this(context, null);
    }

    public AppLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.touchPoint = new Point();
        this.pressTask = new Runnable() {
            @Override
            public void run() {
                AppLinearLayout.this.pressAnimHelper.press();
            }
        };
        this.releaseTask = new Runnable() {
            @Override
            public void run() {
                AppLinearLayout.this.pressAnimHelper.release();
            }
        };
        setClickable(true);
        TypedArray attributes = attrs != null ? context.obtainStyledAttributes(attrs, R.styleable.AppLinearLayout) : null;
        boolean useAlpha = TypeArrayUtils.optBoolean(attributes, R.styleable.AppLinearLayout_useAlphaForLL, true);
        boolean useZoom = TypeArrayUtils.optBoolean(attributes, R.styleable.AppLinearLayout_useZoomForLL, true);
        if (attributes != null) {
            attributes.recycle();
        }
        this.pressAnimHelper = new PressAnimHelper(this, useAlpha, useZoom);
    }

    public View getForbidView() {
        return this.forbidView;
    }

    public void setForbidView(View forbidView) {
        this.forbidView = forbidView;
    }

    @Deprecated
    public void setForbidViewExtra(int extraLeft, int extraRight, int extraTop, int extraBottom) {
        this.extraLeft = extraLeft;
        this.extraRight = extraRight;
        this.extraTop = extraTop;
        this.extraBottom = extraBottom;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.touchPoint.set((int) event.getRawX(), (int) event.getRawY());
        if (UiTouchPointUtil.isTouchPointInView(this.forbidView, this.touchPoint)) {
            this.pressAnimHelper.release();
            return super.dispatchTouchEvent(event);
        }
        int action = event.getAction();
        if (action == 0) {
            post(this.pressTask);
        } else if (action == 1 || action == 3) {
            post(this.releaseTask);
        }
        return super.dispatchTouchEvent(event);
    }
}