package com.xtc.ui.widget.ptrrefresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.ptrrefresh.header.material.SuccCircleView;
import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 带成功对勾动画的下拉刷新头部。 */
public class NewSuccRefreshHeader extends FrameLayout implements UIRefreshHandler {
    private static final int ARC_END_Y = 55;
    private static final int ARC_START_Y = 25;
    private static final String TAG = "NewSuccRefreshHeader";
    private static final int VIEW_TRANS_X = 31;
    // 反编译常量：0x33000000 与 0xFFFFFFFF
    private static final int DEFAULT_NORMAL_COLOR = 855638015;
    private static final int DEFAULT_REFRESH_COLOR = -1;
    private static final int DEFAULT_TEXT_COLOR = -1;
    private float mEndArcY;
    private boolean mIsRefreshSuccess;
    private int mNormalColor;
    private int mRefreshColor;
    private float mStartArcY;
    private AnimatorSet mSuccAnimSet;
    private SuccCircleView mSuccCircleView;
    private TextView mSuccTv;
    private int mTextColor;
    private float mViewTransX;

    public NewSuccRefreshHeader(Context context) {
        super(context);
        this.mIsRefreshSuccess = false;
        initView();
    }

    public NewSuccRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsRefreshSuccess = false;
        initView();
    }

    public NewSuccRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsRefreshSuccess = false;
        initView();
    }

    private void initView() {
        this.mTextColor = DEFAULT_TEXT_COLOR;
        this.mNormalColor = DEFAULT_NORMAL_COLOR;
        this.mRefreshColor = DEFAULT_REFRESH_COLOR;
        View content = LayoutInflater.from(getContext()).inflate(R.layout.new_succ_refresh_header, this);
        float density = getResources().getDisplayMetrics().density;
        this.mStartArcY = 25.0f * density;
        this.mEndArcY = 55.0f * density;
        this.mViewTransX = density * 31.0f;
        this.mSuccTv = (TextView) content.findViewById(R.id.succ_text_view);
        this.mSuccCircleView = (SuccCircleView) content.findViewById(R.id.succ_circle_view);
    }

    @Override
    public void onUIReset(BaseFrameLayout frameLayout) {
        cancelAnim();
        resetUI();
    }

    private void resetUI() {
        this.mSuccCircleView.setArcSize(0.0f);
        this.mSuccTv.setTranslationX(0.0f);
        this.mSuccCircleView.setTranslationX(0.0f);
        this.mSuccCircleView.resetCircle();
        this.mSuccTv.setVisibility(8);
        this.mSuccCircleView.setVisibility(8);
        setColors(this.mRefreshColor, this.mNormalColor, this.mTextColor);
    }

    @Override
    public void onUIRefreshPrepare(BaseFrameLayout frameLayout) {
        LogUtil.d(TAG, "onUIRefreshPrepare");
        this.mSuccTv.setVisibility(8);
        this.mSuccCircleView.setVisibility(0);
    }

    @Override
    public void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess) {
        LogUtil.d(TAG, "onUIRefreshComplete isSuccess = " + isSuccess);
        this.mIsRefreshSuccess = isSuccess;
        if (!this.mIsRefreshSuccess || isSuccAnimRunning()) {
            return;
        }
        this.mSuccCircleView.setVisibility(0);
        startSuccRefreshAnim();
    }

    @Override
    public void onUIRefreshBegin(BaseFrameLayout frameLayout) {
        LogUtil.d(TAG, "onUIRefreshBegin");
    }

    @Override
    public void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator) {
        indicator.getHeaderHeight();
        int currentPosY = indicator.getCurrentPosY();
        if (!isUnderTouch || status != 2) {
            if (isUnderTouch || status != 3) {
                return;
            }
            this.mSuccCircleView.startLoadingAnim();
            return;
        }
        float currentPos = currentPosY;
        float startArcY = this.mStartArcY;
        if (currentPos < startArcY) {
            this.mSuccCircleView.setIsNeedDrawArc(false);
            this.mSuccCircleView.setIsNeedDrawHook(false);
            return;
        }
        float endArcY = this.mEndArcY;
        if (currentPos < endArcY) {
            this.mSuccCircleView.setIsNeedDrawArc(true);
            this.mSuccCircleView.setIsNeedDrawHook(false);
            this.mSuccCircleView.setArcSize(((currentPos - startArcY) * 360.0f) / (endArcY - startArcY));
            return;
        }
        if (endArcY < currentPos) {
            this.mSuccCircleView.setIsNeedDrawArc(true);
            this.mSuccCircleView.setIsNeedDrawHook(false);
            this.mSuccCircleView.setArcSize(360.0f);
        }
    }

    private void startSuccRefreshAnim() {
        cancelAnim();
        ValueAnimator circleAnim = this.mSuccCircleView.createCircleAnim();
        ValueAnimator hookAnim = this.mSuccCircleView.createHookAnim();
        ValueAnimator transAnim = createTransAnim();
        ObjectAnimator refreshAlphaAnim = createRefreshAlphaAnim();
        this.mSuccAnimSet = new AnimatorSet();
        this.mSuccAnimSet.playTogether(circleAnim, hookAnim, transAnim, refreshAlphaAnim);
        this.mSuccAnimSet.start();
    }

    private boolean isSuccAnimRunning() {
        AnimatorSet animatorSet = this.mSuccAnimSet;
        return animatorSet != null && animatorSet.isRunning();
    }

    public void cancelAnim() {
        this.mSuccCircleView.cancelAnim();
        AnimatorSet animatorSet = this.mSuccAnimSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.mSuccAnimSet = null;
        }
    }

    private ValueAnimator createTransAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(320);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationX = (-NewSuccRefreshHeader.this.mViewTransX) * ((Float) animation.getAnimatedValue()).floatValue();
                NewSuccRefreshHeader.this.mSuccTv.setTranslationX(translationX);
                NewSuccRefreshHeader.this.mSuccCircleView.setTranslationX(translationX);
            }
        });
        return animator;
    }

    private ObjectAnimator createRefreshAlphaAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.mSuccTv, "alpha", 0.0f, 1.0f);
        animator.setDuration(200);
        animator.setStartDelay(360);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                NewSuccRefreshHeader.this.mSuccTv.setVisibility(0);
            }
        });
        return animator;
    }

    public void setColors(int refreshColor, int normalColor, int textColor) {
        this.mTextColor = textColor;
        this.mNormalColor = normalColor;
        this.mRefreshColor = refreshColor;
        this.mSuccTv.setTextColor(textColor);
        this.mSuccCircleView.setRefreshColor(refreshColor);
        this.mSuccCircleView.setNormalColor(normalColor);
    }
}