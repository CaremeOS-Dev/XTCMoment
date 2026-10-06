package com.xtc.moment.module.widget.like;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * "Like" animation view that emits randomly chosen like icons flying along a bezier curve from
 * the bottom of the view up to the top while fading out.
 */
public class KsgLikeView extends AnimationLayout {

    private final String tag = KsgLikeView.class.getName();

    /** Duration in milliseconds of the curve travel animation. */
    private int curveDuration;
    /** Duration in milliseconds of the scale/alpha entrance animation. */
    private int enterDuration;

    private List<Integer> likeResources;

    public KsgLikeView(Context context) {
        this(context, null);
    }

    public KsgLikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(attrs);
    }

    @Override
    protected void init() {
        super.init();
        this.likeResources = new ArrayList<Integer>();
    }

    private void initTypedArray(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.KsgLikeView);
        this.enterDuration = typedArray.getInteger(R.styleable.KsgLikeView_ksg_enter_duration, 1500);
        this.curveDuration = typedArray.getInteger(R.styleable.KsgLikeView_ksg_curve_duration, 4500);
        typedArray.recycle();
    }

    @Override
    public void addLikeImage(int resId) {
        addLikeImages(Integer.valueOf(resId));
    }

    @Override
    public void addLikeImages(Integer... resIds) {
        addLikeImages(Arrays.asList(resIds));
    }

    @Override
    public void addLikeImages(List<Integer> resIds) {
        this.likeResources.addAll(resIds);
    }

    @Override
    public void addFavor() {
        if (this.likeResources.isEmpty()) {
            LogUtil.e(this.tag, "请添加资源文件！");
            return;
        }
        int resId = Math.abs(this.likeResources.get(this.random.nextInt(100) % this.likeResources.size()).intValue());
        FrameLayout.LayoutParams layoutParams = createLayoutParams(resId);
        AppCompatImageView imageView = new AppCompatImageView(getContext());
        imageView.setImageResource(resId);
        start(imageView, this, layoutParams);
    }

    private FrameLayout.LayoutParams createLayoutParams(int resId) {
        getPictureInfo(resId);
        return new FrameLayout.LayoutParams((int) this.picWidth, (int) this.picHeight, 81);
    }

    /** Starts the combined entrance + curve animation and adds the child to the layout. */
    private void start(View child, ViewGroup parent, FrameLayout.LayoutParams layoutParams) {
        AnimatorSet enterAnimation = generateEnterAnimation(child);
        ValueAnimator curveAnimation = generateCurveAnimation(child);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(curveAnimation, enterAnimation);
        animatorSet.addListener(new AnimationLayout.AnimationEndListener(child, parent, animatorSet));
        animatorSet.start();
        parent.addView(child, layoutParams);
    }

    /** Scale and alpha fade-in played when the icon appears. */
    private AnimatorSet generateEnterAnimation(View child) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(child, (Property<View, Float>) View.ALPHA, 0.2f, 1.0f),
                ObjectAnimator.ofFloat(child, (Property<View, Float>) View.SCALE_X, 0.2f, 1.0f),
                ObjectAnimator.ofFloat(child, (Property<View, Float>) View.SCALE_Y, 0.2f, 1.0f));
        animatorSet.setInterpolator(new LinearInterpolator());
        return animatorSet.setDuration(this.enterDuration);
    }

    /** Bezier travel from the bottom center of the view up beyond the top edge. */
    private ValueAnimator generateCurveAnimation(View child) {
        ValueAnimator animator = ValueAnimator.ofObject(
                this.evaluatorRecord.getCurrentPath(getTogglePoint(1), getTogglePoint(2)),
                new PointF((this.viewWidth - this.picWidth) / 2.0f, this.viewHeight - this.picHeight),
                new PointF(((this.viewWidth - this.picWidth) / 2.0f) + this.random.nextInt(100), 0.0f));
        animator.addUpdateListener(new AnimationLayout.CurveUpdateLister(child));
        animator.setInterpolator(new LinearInterpolator());
        return animator.setDuration(this.curveDuration);
    }

    /** Random control point for the bezier curve, vertically compressed by {@code divisor}. */
    private PointF getTogglePoint(int divisor) {
        int range = Math.abs(this.viewWidth - 100);
        PointF point = new PointF();
        point.x = this.random.nextInt(range);
        point.y = this.random.nextInt(range) / divisor;
        return point;
    }
}