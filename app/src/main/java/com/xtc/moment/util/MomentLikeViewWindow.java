package com.xtc.moment.util;

import android.animation.Animator;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.widget.VideoAnimView;
import com.xtc.moment.prerogative.IPrerogativeServe;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;

import java.io.File;

public class MomentLikeViewWindow {

    private static final String TAG = "MomentLikeViewWindow";

    private Context mContext;
    private LifecycleOwner lifecycleOwner;
    private LifecycleRegistry lifecycleRegistry;
    private View inflate;
    private PopupWindow mLikeAnimationWindow;
    private LottieAnimationView mLikeAnimationView;
    private VideoAnimView mVideoAnimationView;
    private DbMomentPrerogativeLike currentUseLikeAnimationLocalPath;
    private boolean isVideoLikeDynamic = false;

    public void initLikeAnimation(Context context, LifecycleOwner lifecycleOwner, LifecycleRegistry lifecycleRegistry) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleRegistry = lifecycleRegistry;
        this.mContext = context;
    }

    public void initAnim() {
        if (inflate == null) {
            inflate = LayoutInflater.from(mContext).inflate(R.layout.view_prerogative_like_animation, (ViewGroup) null);
            mLikeAnimationWindow = new PopupWindow(inflate, -1, -1);
        }
        IPrerogativeServe prerogativeServe = MomentPrerogativeServeImpl.getInstance(mContext);
        int emotionId = prerogativeServe.getCurrentUseLikeEmotionId();
        if (emotionId > 0) {
            currentUseLikeAnimationLocalPath = prerogativeServe.getPrerogativeLikeByEmotionId(emotionId);
        }
        DbMomentPrerogativeLike like = currentUseLikeAnimationLocalPath;
        isVideoLikeDynamic = like != null && !TextUtils.isEmpty(like.getLocalEmotionPath());
        LogUtil.d(TAG, "initLikeAnimation: " + currentUseLikeAnimationLocalPath);
        if (!isVideoLikeDynamic) {
            if (mLikeAnimationView == null) {
                mLikeAnimationView = (LottieAnimationView) inflate.findViewById(R.id.like_animation_view);
            }
            mLikeAnimationView.setVisibility(View.VISIBLE);
            if (mVideoAnimationView != null) {
                mVideoAnimationView.setVisibility(View.GONE);
            }
            try {
                LottieComposition.Factory.fromAssetFileName(mContext, "liked.json", new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        mLikeAnimationView.setComposition(composition);
                        mLikeAnimationView.setImageAssetsFolder("images/");
                        mLikeAnimationView.loop(false);
                    }
                });
                mLikeAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
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
                        if (mLikeAnimationWindow == null || !mLikeAnimationWindow.isShowing()) {
                            return;
                        }
                        mLikeAnimationWindow.dismiss();
                        if (mVideoAnimationView != null) {
                            mVideoAnimationView.setVisibility(View.GONE);
                        }
                        mLikeAnimationView.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (mVideoAnimationView == null) {
            mVideoAnimationView = (VideoAnimView) inflate.findViewById(R.id.video_anim);
        }
        mVideoAnimationView.setVisibility(View.VISIBLE);
        if (mLikeAnimationView != null) {
            mLikeAnimationView.setVisibility(View.GONE);
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        mVideoAnimationView.setLooping(false);
        mVideoAnimationView.initPlayerController(mContext, lifecycleOwner, new VideoAnimView.VideoAnimationListener() {
            @Override
            public void onFirstFrame() {
            }

            @Override
            public void onPlayerReady() {
            }

            @Override
            public void onEnd() {
                if (mLikeAnimationWindow == null || !mLikeAnimationWindow.isShowing()) {
                    return;
                }
                mLikeAnimationWindow.dismiss();
                mVideoAnimationView.setVisibility(View.GONE);
                if (mLikeAnimationView != null) {
                    mLikeAnimationView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String playType, int what, int extra, String message) {
                LogUtil.w(TAG, "onError: playType=" + playType + " what=" + what + "  extra=" + extra);
            }
        });
    }

    public void showLikeAnimation(View view) {
        if (!isVideoLikeDynamic) {
            if (mLikeAnimationWindow == null || mLikeAnimationView == null) {
                return;
            }
            mLikeAnimationView.setVisibility(View.VISIBLE);
            mLikeAnimationWindow.showAtLocation(view, 0, 0, 0);
            mLikeAnimationView.playAnimation();
            return;
        }
        if (currentUseLikeAnimationLocalPath == null || mVideoAnimationView == null) {
            return;
        }
        mLikeAnimationWindow.showAtLocation(view, 0, 0, 0);
        mVideoAnimationView.setVisibility(View.VISIBLE);
        mVideoAnimationView.startAnimation(
                FileManager.getMomentLikeRootPath() + currentUseLikeAnimationLocalPath.getNetDynamicName() + File.separator,
                currentUseLikeAnimationLocalPath.getEmotionCode());
    }
}