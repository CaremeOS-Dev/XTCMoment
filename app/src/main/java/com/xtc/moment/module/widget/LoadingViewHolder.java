package com.xtc.moment.module.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xtc.moment.R;

/**
 * Lottie 加载动画弹窗。
 */
public class LoadingViewHolder {

    private final PopupWindow mLoadingAnimationPopup;
    private final LottieAnimationView mLoadingAnimationView;

    public LoadingViewHolder(Context context) {
        View content = LayoutInflater.from(context).inflate(R.layout.loading_view_momen_t, (ViewGroup) null);
        this.mLoadingAnimationView = (LottieAnimationView) content.findViewById(R.id.loading_animation_view);
        LottieComposition.Factory.fromAssetFileName(context, "loading.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                LoadingViewHolder.this.mLoadingAnimationView.setComposition(composition);
                LoadingViewHolder.this.mLoadingAnimationView.loop(true);
            }
        });
        this.mLoadingAnimationPopup = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        this.mLoadingAnimationPopup.setOutsideTouchable(false);
    }

    public void showLoading(View anchor) {
        this.mLoadingAnimationPopup.showAtLocation(anchor, android.view.Gravity.CENTER, 0, 0);
        this.mLoadingAnimationView.playAnimation();
    }

    public void dismissLoading() {
        PopupWindow popupWindow = this.mLoadingAnimationPopup;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.mLoadingAnimationPopup.dismiss();
        }
        LottieAnimationView animationView = this.mLoadingAnimationView;
        if (animationView != null) {
            animationView.cancelAnimation();
        }
    }
}