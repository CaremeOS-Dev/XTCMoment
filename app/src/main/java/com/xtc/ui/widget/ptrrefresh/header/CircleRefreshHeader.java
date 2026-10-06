package com.xtc.ui.widget.ptrrefresh.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.animation.loadinganim.CircleLoadingView;
import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 圆形加载样式下拉刷新头部。 */
public class CircleRefreshHeader extends FrameLayout implements UIRefreshHandler {
    private static final float ARC_SIZE = 90.0f;
    // 反编译常量：0xFFB2B2B2
    private static final int DEFAULT_CIRCLE_COLOR = -5066062;
    private CircleLoadingView mCircleLoadingView;
    private float mDensity;

    public CircleRefreshHeader(Context context) {
        super(context);
        initView();
    }

    public CircleRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CircleRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.mCircleLoadingView = (CircleLoadingView) LayoutInflater.from(getContext())
                .inflate(R.layout.circle_refresh_header, this).findViewById(R.id.circle_anim_view);
        this.mDensity = getResources().getDisplayMetrics().density;
        this.mCircleLoadingView.setRadius((int) (12 * this.mDensity));
        this.mCircleLoadingView.setColor(DEFAULT_CIRCLE_COLOR);
    }

    @Override
    public void onUIReset(BaseFrameLayout frameLayout) {
        this.mCircleLoadingView.setVisibility(4);
        this.mCircleLoadingView.cancelAnim();
    }

    @Override
    public void onUIRefreshPrepare(BaseFrameLayout frameLayout) {
        this.mCircleLoadingView.setVisibility(0);
        this.mCircleLoadingView.cancelAnim();
    }

    @Override
    public void onUIRefreshBegin(BaseFrameLayout frameLayout) {
        this.mCircleLoadingView.setVisibility(0);
        this.mCircleLoadingView.startAnim();
    }

    @Override
    public void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess) {
        this.mCircleLoadingView.setVisibility(0);
    }

    @Override
    public void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator) {
        int headerHeight = indicator.getHeaderHeight();
        int currentPosY = indicator.getCurrentPosY();
        this.mCircleLoadingView.setVisibility(0);
        if (currentPosY < headerHeight && isUnderTouch) {
            float arcSize = (currentPosY * ARC_SIZE) / headerHeight;
            this.mCircleLoadingView.cancelAnim();
            this.mCircleLoadingView.setArcSize(arcSize);
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

    public void setCirCleColor(int color) {
        this.mCircleLoadingView.setColor(color);
    }

    public void setCirCleRadius(int radius) {
        this.mCircleLoadingView.setRadius(radius);
    }
}