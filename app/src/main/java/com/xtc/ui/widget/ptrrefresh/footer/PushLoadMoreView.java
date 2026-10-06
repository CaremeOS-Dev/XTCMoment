package com.xtc.ui.widget.ptrrefresh.footer;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.animation.loadinganim.CircleLoadingView;

/** 上推加载更多的底部视图：圆形加载动画 + 提示文字。 */
public class PushLoadMoreView implements LoadMoreView {
    private static final float ARC_SIZE = 90.0f;
    // 反编译常量：0xFFB2B2B2
    private static final int DEFAULT_CIRCLE_COLOR = -5066062;
    private CircleLoadingView mCircleLoadingView;
    private float mDensity;
    private View mFooter;
    private TextView mFooterTv;

    @Override
    public void init(FootViewAdder footViewAdder, View.OnClickListener onClickListener) {
        this.mFooter = footViewAdder.addFootView(R.layout.push_loadmore_footer);
        this.mFooterTv = (TextView) this.mFooter.findViewById(R.id.footer_text_view);
        this.mCircleLoadingView = (CircleLoadingView) this.mFooter.findViewById(R.id.Loading_anim_view);
        this.mDensity = this.mFooter.getResources().getDisplayMetrics().density;
        this.mCircleLoadingView.setRadius((int) (8 * this.mDensity));
        this.mCircleLoadingView.setColor(DEFAULT_CIRCLE_COLOR);
        this.mCircleLoadingView.setArcSize(ARC_SIZE);
        this.mFooter.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                PushLoadMoreView.this.mFooter.getViewTreeObserver().removeOnPreDrawListener(this);
                PushLoadMoreView.this.showNormal();
                return false;
            }
        });
        this.mFooter.setOnClickListener(onClickListener);
    }

    @Override
    public void showNormal() {
        this.mFooterTv.setVisibility(8);
        this.mCircleLoadingView.setVisibility(8);
        this.mCircleLoadingView.cancelAnim();
    }

    @Override
    public void showLoading() {
        this.mFooterTv.setText(R.string.loading);
        this.mFooterTv.setVisibility(0);
        this.mCircleLoadingView.setVisibility(0);
        this.mCircleLoadingView.startAnim();
    }

    @Override
    public void showNoMore() {
        this.mFooterTv.setText(R.string.no_mores);
        this.mFooterTv.setVisibility(0);
        this.mCircleLoadingView.setVisibility(8);
        this.mCircleLoadingView.cancelAnim();
        this.mFooter.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                PushLoadMoreView.this.mFooter.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    @Override
    public void showFail(Exception exception) {
        this.mFooterTv.setVisibility(8);
        this.mCircleLoadingView.setVisibility(8);
        this.mCircleLoadingView.cancelAnim();
    }

    public void setFooterHeight(int height) {
        ViewGroup.LayoutParams layoutParams = this.mFooter.getLayoutParams();
        layoutParams.height = height;
        this.mFooter.setLayoutParams(layoutParams);
    }

    public void getFooterHeight(int unusedHeight) {
        View footer = this.mFooter;
        if (footer != null) {
            footer.getHeight();
        }
    }

    public void hideFooter() {
        this.mFooterTv.setVisibility(8);
        this.mCircleLoadingView.setVisibility(8);
        this.mCircleLoadingView.cancelAnim();
    }

    public void setCirCleColor(int color) {
        this.mCircleLoadingView.setColor(color);
    }

    public void setCirCleRadius(int radius) {
        this.mCircleLoadingView.setRadius(radius);
    }
}