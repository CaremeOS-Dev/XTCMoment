package com.xtc.ui.widget.ptrrefresh.header;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.animation.loadinganim.CircleLoadingView;
import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 支持成功态展示的下拉刷新头部。 */
public class SuccRefreshHeader extends FrameLayout implements UIRefreshHandler {
    private static final float ARC_SIZE = 270.0f;
    // 反编译常量：0xFFB2B2B2
    private static final int DEFAULT_CIRCLE_COLOR = -5066062;
    private boolean isRefreshSuccess;
    private CircleLoadingView mCircleLoadingView;
    private float mDensity;
    private ImageView mSuccessIV;
    private RelativeLayout mSuccessRefreshLayout;
    private TextView mSuccessTV;

    public SuccRefreshHeader(Context context) {
        super(context);
        this.isRefreshSuccess = false;
        initView();
    }

    public SuccRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isRefreshSuccess = false;
        initView();
    }

    public SuccRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isRefreshSuccess = false;
        initView();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.success_refresh_header, this);
        this.mCircleLoadingView = (CircleLoadingView) content.findViewById(R.id.circle_anim_view);
        this.mDensity = getResources().getDisplayMetrics().density;
        this.mCircleLoadingView.setRadius((int) (12 * this.mDensity));
        this.mCircleLoadingView.setColor(DEFAULT_CIRCLE_COLOR);
        this.mSuccessRefreshLayout = (RelativeLayout) content.findViewById(R.id.refresh_success_layout);
        this.mSuccessTV = (TextView) content.findViewById(R.id.refresh_success_tv);
        this.mSuccessIV = (ImageView) content.findViewById(R.id.refresh_success_iv);
    }

    @Override
    public void onUIReset(BaseFrameLayout frameLayout) {
        this.mCircleLoadingView.setVisibility(4);
        this.mCircleLoadingView.cancelAnim();
        this.mSuccessRefreshLayout.setVisibility(4);
    }

    @Override
    public void onUIRefreshPrepare(BaseFrameLayout frameLayout) {
        this.isRefreshSuccess = false;
        this.mCircleLoadingView.setVisibility(0);
        this.mCircleLoadingView.cancelAnim();
        this.mSuccessRefreshLayout.setVisibility(4);
    }

    @Override
    public void onUIRefreshBegin(BaseFrameLayout frameLayout) {
        this.isRefreshSuccess = false;
        this.mCircleLoadingView.setVisibility(0);
        this.mCircleLoadingView.startAnim();
        this.mSuccessRefreshLayout.setVisibility(4);
    }

    @Override
    public void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess) {
        this.isRefreshSuccess = isSuccess;
        if (this.isRefreshSuccess) {
            this.mCircleLoadingView.setVisibility(4);
            this.mCircleLoadingView.cancelAnim();
            if (this.mSuccessRefreshLayout.getVisibility() != 0) {
                this.mSuccessRefreshLayout.setAlpha(0.0f);
                this.mSuccessRefreshLayout.setVisibility(0);
                ObjectAnimator animator = ObjectAnimator.ofFloat(this.mSuccessRefreshLayout, "alpha", 0.0f, 1.0f);
                animator.setDuration(200L);
                animator.start();
            }
            return;
        }
        this.mCircleLoadingView.setVisibility(0);
        this.mSuccessRefreshLayout.setVisibility(4);
    }

    @Override
    public void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator) {
        if (this.isRefreshSuccess) {
            this.mCircleLoadingView.setVisibility(4);
            this.mSuccessRefreshLayout.setVisibility(0);
            return;
        }
        int headerHeight = indicator.getHeaderHeight();
        int currentPosY = indicator.getCurrentPosY();
        this.mCircleLoadingView.setVisibility(0);
        this.mSuccessRefreshLayout.setVisibility(4);
        if (currentPosY < headerHeight && isUnderTouch) {
            this.mCircleLoadingView.cancelAnim();
            this.mCircleLoadingView.setArcSize((currentPosY * ARC_SIZE) / headerHeight);
            this.mCircleLoadingView.setRotateAngle(0.0f);
            return;
        }
        if (currentPosY >= headerHeight) {
            this.mCircleLoadingView.setArcSize(ARC_SIZE);
            if (this.mCircleLoadingView.isAnimRunning()) {
                return;
            }
            this.mCircleLoadingView.startAnim();
            return;
        }
        this.mCircleLoadingView.cancelAnim();
    }

    public void setViewColor(int color, int iconResId) {
        this.mCircleLoadingView.setColor(color);
        this.mSuccessTV.setTextColor(color);
        this.mSuccessIV.setBackgroundResource(iconResId);
    }

    public void setCirCleRadius(int radius) {
        this.mCircleLoadingView.setRadius(radius);
    }

    public boolean isRefreshSuccess() {
        return this.isRefreshSuccess;
    }

    public void setIsRefreshSuccess(boolean isRefreshSuccess) {
        this.isRefreshSuccess = isRefreshSuccess;
    }

    public void stopAnim() {
        CircleLoadingView circleLoadingView = this.mCircleLoadingView;
        if (circleLoadingView != null) {
            circleLoadingView.cancelAnim();
        }
    }
}