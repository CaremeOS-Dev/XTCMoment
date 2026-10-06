package com.xtc.ui.widget.viewfilpper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.xtc.ui.widget.R;

/** 一次只显示一个子视图的 FrameLayout，支持切换时的进出动画。 */
public class ViewAnimator extends FrameLayout {
    boolean mAnimateFirstTime;
    boolean mFirstTime;
    Animation mInAnimation;
    Animation mOutAnimation;
    int mWhichChild;

    public ViewAnimator(Context context) {
        super(context);
        this.mWhichChild = 0;
        this.mFirstTime = true;
        this.mAnimateFirstTime = true;
        initViewAnimator(context, null);
    }

    public ViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWhichChild = 0;
        this.mFirstTime = true;
        this.mAnimateFirstTime = true;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ViewAnimator);
        int inAnimationResId = attributes.getResourceId(R.styleable.ViewAnimator_inAnimation, 0);
        if (inAnimationResId > 0) {
            setInAnimation(context, inAnimationResId);
        }
        int outAnimationResId = attributes.getResourceId(R.styleable.ViewAnimator_outAnimation, 0);
        if (outAnimationResId > 0) {
            setOutAnimation(context, outAnimationResId);
        }
        setAnimateFirstView(attributes.getBoolean(R.styleable.ViewAnimator_animateFirstView, true));
        attributes.recycle();
        initViewAnimator(context, attrs);
    }

    private void initViewAnimator(Context context, AttributeSet attrs) {
        setMeasureAllChildren(true);
    }

    public void setDisplayedChild(int whichChild) {
        this.mWhichChild = whichChild;
        if (whichChild >= getChildCount()) {
            this.mWhichChild = 0;
        } else if (whichChild < 0) {
            this.mWhichChild = getChildCount() - 1;
        }
        boolean hasFocus = getFocusedChild() != null;
        showOnly(this.mWhichChild);
        if (hasFocus) {
            requestFocus(2);
        }
    }

    public int getDisplayedChild() {
        return this.mWhichChild;
    }

    public void showNext() {
        setDisplayedChild(this.mWhichChild + 1);
    }

    public void showPrevious() {
        setDisplayedChild(this.mWhichChild - 1);
    }

    void showOnly(int whichChild, boolean animate) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (index == whichChild) {
                if (animate && this.mInAnimation != null) {
                    child.startAnimation(this.mInAnimation);
                }
                child.setVisibility(0);
                this.mFirstTime = false;
            } else {
                if (animate && this.mOutAnimation != null && child.getVisibility() == 0) {
                    child.startAnimation(this.mOutAnimation);
                } else if (child.getAnimation() == this.mInAnimation) {
                    child.clearAnimation();
                }
                child.setVisibility(8);
            }
        }
    }

    void showOnly(int whichChild) {
        showOnly(whichChild, !this.mFirstTime || this.mAnimateFirstTime);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (getChildCount() == 1) {
            child.setVisibility(0);
        } else {
            child.setVisibility(8);
        }
        int whichChild = this.mWhichChild;
        if (index < 0 || whichChild < index) {
            return;
        }
        setDisplayedChild(whichChild + 1);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        this.mWhichChild = 0;
        this.mFirstTime = true;
    }

    @Override
    public void removeView(View view) {
        int index = indexOfChild(view);
        if (index >= 0) {
            removeViewAt(index);
        }
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        int childCount = getChildCount();
        if (childCount == 0) {
            this.mWhichChild = 0;
            this.mFirstTime = true;
            return;
        }
        int whichChild = this.mWhichChild;
        if (whichChild >= childCount) {
            setDisplayedChild(childCount - 1);
        } else if (whichChild == index) {
            setDisplayedChild(whichChild);
        }
    }

    @Override
    public void removeViewInLayout(View view) {
        removeView(view);
    }

    @Override
    public void removeViews(int start, int count) {
        super.removeViews(start, count);
        if (getChildCount() == 0) {
            this.mWhichChild = 0;
            this.mFirstTime = true;
            return;
        }
        int whichChild = this.mWhichChild;
        if (whichChild < start || whichChild >= start + count) {
            return;
        }
        setDisplayedChild(whichChild);
    }

    @Override
    public void removeViewsInLayout(int start, int count) {
        removeViews(start, count);
    }

    public View getCurrentView() {
        return getChildAt(this.mWhichChild);
    }

    public Animation getInAnimation() {
        return this.mInAnimation;
    }

    public void setInAnimation(Animation animation) {
        this.mInAnimation = animation;
    }

    public Animation getOutAnimation() {
        return this.mOutAnimation;
    }

    public void setOutAnimation(Animation animation) {
        this.mOutAnimation = animation;
    }

    public void setInAnimation(Context context, int resId) {
        setInAnimation(AnimationUtils.loadAnimation(context, resId));
    }

    public void setOutAnimation(Context context, int resId) {
        setOutAnimation(AnimationUtils.loadAnimation(context, resId));
    }

    public boolean getAnimateFirstView() {
        return this.mAnimateFirstTime;
    }

    public void setAnimateFirstView(boolean animateFirstTime) {
        this.mAnimateFirstTime = animateFirstTime;
    }

    @Override
    public int getBaseline() {
        return getCurrentView() != null ? getCurrentView().getBaseline() : super.getBaseline();
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ViewAnimator.class.getName();
    }
}