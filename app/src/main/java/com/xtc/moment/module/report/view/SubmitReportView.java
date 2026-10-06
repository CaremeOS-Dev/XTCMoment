package com.xtc.moment.module.report.view;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xtc.moment.R;
import com.xtc.moment.module.report.interfaces.IJumpView;

/**
 * 举报提交页面：先播放提交中动画，提交成功后切换到成功动画并自动关闭。
 */
public class SubmitReportView extends AbsSelectReportView {

    public static final int FINISH_TIME = 3000;

    private TextView tvReportSubmitSuccess;
    private TextView tvWaitOfficialDeal;
    private LottieAnimationView mLikeAnimationView;
    private LottieAnimationView mSucceedAnimationView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_report_submit;
    }

    @Override
    protected void loadData() {
    }

    public SubmitReportView(Context context, IJumpView iJumpView) {
        super(context, iJumpView);
    }

    public SubmitReportView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void findViewId() {
        this.tvReportSubmitSuccess = (TextView) findId(R.id.report_submit_success);
        this.tvWaitOfficialDeal = (TextView) findId(R.id.wait_official_deal);
        this.mLikeAnimationView = (LottieAnimationView) findId(R.id.loading_animation_view);
        LottieComposition.Factory.fromAssetFileName(this.mContext, "bigloading.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                if (composition == null) {
                    return;
                }
                SubmitReportView.this.mLikeAnimationView.setComposition(composition);
                SubmitReportView.this.mLikeAnimationView.loop(true);
            }
        });
        this.mSucceedAnimationView = (LottieAnimationView) findId(R.id.succeed_animation_view);
        LottieComposition.Factory.fromAssetFileName(this.mContext, "succeed.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                if (composition == null) {
                    return;
                }
                SubmitReportView.this.mSucceedAnimationView.setComposition(composition);
                SubmitReportView.this.mSucceedAnimationView.loop(false);
            }
        });
    }

    @Override
    protected void initData() {
        this.mSucceedAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                SubmitReportView.this.mSucceedAnimationView.cancelAnimation();
                SubmitReportView.this.mSucceedAnimationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SubmitReportView.this.finishView();
                    }
                }, FINISH_TIME);
            }
        });
    }

    @Override
    public void viewShow(int informSource) {
        super.viewShow(informSource);
        this.reportPresenter.pushReportData();
        this.mLikeAnimationView.loop(true);
        this.mLikeAnimationView.playAnimation();
    }

    /** 提交成功后切换成成功提示动画。 */
    public void pushSuccess() {
        this.tvReportSubmitSuccess.setText(getResources().getString(R.string.report_submit_success));
        this.mLikeAnimationView.cancelAnimation();
        this.mLikeAnimationView.setVisibility(View.GONE);
        this.tvWaitOfficialDeal.setVisibility(View.VISIBLE);
        this.tvReportSubmitSuccess.setVisibility(View.VISIBLE);
        this.mSucceedAnimationView.setVisibility(View.VISIBLE);
        this.mSucceedAnimationView.playAnimation();
    }
}