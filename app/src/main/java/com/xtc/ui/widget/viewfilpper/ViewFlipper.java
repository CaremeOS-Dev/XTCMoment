package com.xtc.ui.widget.viewfilpper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.xtc.moment.R;

/** 支持自动定时轮播子视图的 ViewAnimator。 */
public class ViewFlipper extends ViewAnimator {
    private static final int DEFAULT_INTERVAL = 3000;
    private static final String TAG = "ViewFlipper";
    private boolean mAutoStart;
    private int mFlipInterval;
    private final Runnable mFlipRunnable;
    private boolean mRunning;
    private boolean mStarted;
    private boolean mVisible;

    public ViewFlipper(Context context) {
        super(context);
        this.mFlipInterval = 3000;
        this.mAutoStart = false;
        this.mRunning = false;
        this.mStarted = false;
        this.mVisible = false;
        this.mFlipRunnable = new Runnable() {
            @Override
            public void run() {
                if (ViewFlipper.this.mRunning) {
                    ViewFlipper.this.showNext();
                    ViewFlipper viewFlipper = ViewFlipper.this;
                    viewFlipper.postDelayed(viewFlipper.mFlipRunnable, ViewFlipper.this.mFlipInterval);
                }
            }
        };
    }

    public ViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFlipInterval = 3000;
        this.mAutoStart = false;
        this.mRunning = false;
        this.mStarted = false;
        this.mVisible = false;
        this.mFlipRunnable = new Runnable() {
            @Override
            public void run() {
                if (ViewFlipper.this.mRunning) {
                    ViewFlipper.this.showNext();
                    ViewFlipper viewFlipper = ViewFlipper.this;
                    viewFlipper.postDelayed(viewFlipper.mFlipRunnable, ViewFlipper.this.mFlipInterval);
                }
            }
        };
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ViewFlipper);
        this.mFlipInterval = attributes.getInt(R.styleable.ViewFlipper_flipInterval, 3000);
        this.mAutoStart = attributes.getBoolean(R.styleable.ViewFlipper_autoStart, false);
        attributes.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAutoStart) {
            startFlipping();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        this.mVisible = visibility == 0;
        updateRunning(false);
    }

    public void setFlipInterval(int flipInterval) {
        this.mFlipInterval = flipInterval;
    }

    public void startFlipping() {
        this.mStarted = true;
        updateRunning();
    }

    public void startFlipping(boolean animate) {
        this.mStarted = true;
        updateRunning(animate);
    }

    public void stopFlipping() {
        this.mStarted = false;
        updateRunning();
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ViewFlipper.class.getName();
    }

    private void updateRunning() {
        updateRunning(true);
    }

    private void updateRunning(boolean animate) {
        boolean shouldRun = this.mVisible && this.mStarted;
        if (shouldRun != this.mRunning) {
            if (shouldRun) {
                showOnly(getDisplayedChild(), animate);
                postDelayed(this.mFlipRunnable, this.mFlipInterval);
            } else {
                removeCallbacks(this.mFlipRunnable);
            }
            this.mRunning = shouldRun;
        }
    }

    public boolean isFlipping() {
        return this.mStarted;
    }

    public void setAutoStart(boolean autoStart) {
        this.mAutoStart = autoStart;
    }

    public boolean isAutoStart() {
        return this.mAutoStart;
    }
}