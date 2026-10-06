package com.xtc.moment.module.widget;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xtc.moment.R;
import com.xtc.moment.util.HandlerUtil;

/**
 * Full screen popup that plays the "publishing" Lottie animation and then swaps to the success
 * animation. Used while a moment post is being uploaded.
 */
public class LoadingPupWindowHolder {

    private PopupWindow loadingPopup;
    private LottieAnimationView loadingAnimationView;
    private LottieAnimationView succeedAnimationView;
    private TextView successTv;

    private Cancellable loadingCancellable;
    private Cancellable succeedCancellable;
    private Runnable onSuccessAction;

    public LoadingPupWindowHolder(Context context) {
        initLoadingAnimation(context);
    }

    public void setOnSuccessAction(Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }

    public void showLoading(View anchor) {
        this.loadingPopup.showAtLocation(anchor, 0, 0, 0);
        this.loadingAnimationView.playAnimation();
    }

    public void dismissLoading() {
        PopupWindow popup = this.loadingPopup;
        if (popup != null && popup.isShowing()) {
            this.loadingPopup.dismiss();
        }
        LottieAnimationView loading = this.loadingAnimationView;
        if (loading != null) {
            loading.cancelAnimation();
        }
        LottieAnimationView succeed = this.succeedAnimationView;
        if (succeed != null) {
            succeed.cancelAnimation();
        }
    }

    private void initLoadingAnimation(final Context context) {
        View content = LayoutInflater.from(context).inflate(R.layout.popup_big_loading_publish, (ViewGroup) null);
        this.loadingAnimationView = (LottieAnimationView) content.findViewById(R.id.loading_animation_view);
        this.succeedAnimationView = (LottieAnimationView) content.findViewById(R.id.succeed_animation_view);
        this.successTv = (TextView) content.findViewById(R.id.send_success_tv);

        // The compositions are parsed off the main thread because the JSON assets are large.
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                loadCompositions(context);
            }
        });

        this.loadingAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                LoadingPupWindowHolder.this.loadingAnimationView.setVisibility(View.VISIBLE);
                LoadingPupWindowHolder.this.succeedAnimationView.setVisibility(View.GONE);
                LoadingPupWindowHolder.this.successTv.setVisibility(View.GONE);
            }
        });

        this.succeedAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                LoadingPupWindowHolder.this.successTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (LoadingPupWindowHolder.this.onSuccessAction != null) {
                    LoadingPupWindowHolder.this.onSuccessAction.run();
                }
            }
        });

        this.loadingPopup = new PopupWindow(content, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
    }

    /** Loads both Lottie assets; called from the background handler. */
    private void loadCompositions(Context context) {
        this.loadingCancellable = LottieComposition.Factory.fromAssetFileName(context, "bigloadingmoment.json",
                new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        LoadingPupWindowHolder.this.loadingAnimationView.setComposition(composition);
                        LoadingPupWindowHolder.this.loadingAnimationView.loop(true);
                    }
                });
        this.succeedCancellable = LottieComposition.Factory.fromAssetFileName(context, "succeedmoment.json",
                new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        LoadingPupWindowHolder.this.succeedAnimationView.setComposition(composition);
                        LoadingPupWindowHolder.this.succeedAnimationView.loop(false);
                    }
                });
    }

    public void setText(String text) {
        this.successTv.setText(text);
    }

    /** Switches from the loading animation to the success animation. */
    public void showSuccess() {
        this.loadingAnimationView.setVisibility(View.GONE);
        this.loadingAnimationView.cancelAnimation();
        this.succeedAnimationView.setVisibility(View.VISIBLE);
        this.succeedAnimationView.playAnimation();
    }

    /** Releases the pending Lottie parse requests. */
    public void clean() {
        Cancellable loading = this.loadingCancellable;
        if (loading != null) {
            loading.cancel();
        }
        Cancellable succeed = this.succeedCancellable;
        if (succeed != null) {
            succeed.cancel();
        }
    }
}