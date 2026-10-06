package com.xtc.moment.module.personalinfo.widget;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.util.ToastUtil;

/**
 * 全屏 Lottie 加载弹窗，用于个人中心的加载/成功/失败状态展示。
 *
 * <p>加载动画每循环一次计数一次，当超过 {@link #REPEAT_MAX_TIMES} 次仍未成功且开启了自动失败，
 * 则切换为失败状态并提示。
 */
public class LoadingViewHolder {

    private static final String TAG = "LoadingViewHolder";

    /** 加载动画最大循环次数，超过则视为超时失败。 */
    private static final int REPEAT_MAX_TIMES = 45;

    public static final int STATUS_FAILED = -1;
    public static final int STATUS_LOADING = 0;
    public static final int STATUS_SUCCESS = 1;

    private final Context mContext;
    private final PopupWindow mLoadingAnimationPopup;
    private final LottieAnimationView mLoadingAnimationView;
    private final LottieAnimationView mSucceedAnimationView;
    private final TextView tipsText;

    private String failedStr;
    private String loadingStr;
    private String successStr;
    private int loadingStatus;
    private int repeatTimes;
    private Runnable onSuccessAction;
    private boolean isSuccessAutoDismiss = true;
    private boolean isAutoLoadFail = true;

    public LoadingViewHolder(Context context) {
        this.mContext = context;
        View content = LayoutInflater.from(context).inflate(R.layout.loading_view, (ViewGroup) null);
        this.tipsText = (TextView) content.findViewById(R.id.loading_text);
        this.mLoadingAnimationView = (LottieAnimationView) content.findViewById(R.id.loading_animation_view);
        LottieComposition.Factory.fromAssetFileName(context, "bigloading.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                LoadingViewHolder.this.mLoadingAnimationView.setComposition(composition);
                LoadingViewHolder.this.mLoadingAnimationView.loop(true);
            }
        });
        this.mSucceedAnimationView = (LottieAnimationView) content.findViewById(R.id.succeed_animation_view);
        LottieComposition.Factory.fromAssetFileName(context, "succeed.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                LoadingViewHolder.this.mSucceedAnimationView.setComposition(composition);
                LoadingViewHolder.this.mSucceedAnimationView.loop(false);
            }
        });
        addAnimatorListener();
        this.mLoadingAnimationPopup = new PopupWindow(content, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        this.mLoadingAnimationPopup.setOutsideTouchable(false);
    }

    private void addAnimatorListener() {
        this.mLoadingAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (!TextUtils.isEmpty(LoadingViewHolder.this.loadingStr)) {
                    LoadingViewHolder.this.tipsText.setText(LoadingViewHolder.this.loadingStr);
                }
                LoadingViewHolder.this.loadingStatus = STATUS_LOADING;
                LogUtil.d(TAG, "onAnimationStart");
                LoadingViewHolder.this.mLoadingAnimationView.setVisibility(View.VISIBLE);
                LoadingViewHolder.this.mSucceedAnimationView.setVisibility(View.GONE);
                LoadingViewHolder.this.repeatTimes = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                LoadingViewHolder.this.mSucceedAnimationView.setVisibility(View.VISIBLE);
                LoadingViewHolder.this.mLoadingAnimationView.setVisibility(View.GONE);
                if (LoadingViewHolder.this.loadingStatus == STATUS_SUCCESS) {
                    LoadingViewHolder.this.mSucceedAnimationView.playAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                LoadingViewHolder.this.repeatTimes++;
                LogUtil.d(TAG, "repeatTimes: " + LoadingViewHolder.this.repeatTimes + ", loadingStatus: "
                        + LoadingViewHolder.this.loadingStatus);
                if (LoadingViewHolder.this.repeatTimes == REPEAT_MAX_TIMES && LoadingViewHolder.this.isAutoLoadFail
                        && LoadingViewHolder.this.loadingStatus != STATUS_SUCCESS) {
                    LogUtil.i(TAG, "onAnimationRepeat: " + LoadingViewHolder.this.repeatTimes);
                    LoadingViewHolder.this.loadingStatus = STATUS_FAILED;
                }
                int status = LoadingViewHolder.this.loadingStatus;
                if (status == STATUS_FAILED) {
                    LoadingViewHolder.this.showLoadFailed();
                } else if (status == STATUS_SUCCESS) {
                    LoadingViewHolder.this.mLoadingAnimationView.cancelAnimation();
                }
            }
        });
        this.mSucceedAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (!TextUtils.isEmpty(LoadingViewHolder.this.successStr)) {
                    LoadingViewHolder.this.tipsText.setText(LoadingViewHolder.this.successStr);
                }
                LoadingViewHolder.this.mLoadingAnimationView.setVisibility(View.GONE);
                LoadingViewHolder.this.mSucceedAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (LoadingViewHolder.this.onSuccessAction != null) {
                    LoadingViewHolder.this.onSuccessAction.run();
                }
                if (LoadingViewHolder.this.isSuccessAutoDismiss) {
                    LoadingViewHolder.this.dismissLoading();
                }
            }
        });
    }

    /** 加载超时或失败时提示并关闭弹窗。 */
    private void showLoadFailed() {
        if (!TextUtils.isEmpty(this.failedStr)) {
            ToastUtil.showShort(this.mContext, this.failedStr);
        }
        dismissLoading();
    }

    public void showLoading(View anchor) {
        this.mLoadingAnimationPopup.showAtLocation(anchor, android.view.Gravity.CENTER, 0, 0);
        this.mLoadingAnimationView.playAnimation();
    }

    public void loadSuccess() {
        this.loadingStatus = STATUS_SUCCESS;
        if (this.mLoadingAnimationView != null && this.mSucceedAnimationView != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    LoadingViewHolder.this.mLoadingAnimationView.cancelAnimation();
                    LoadingViewHolder.this.mSucceedAnimationView.playAnimation();
                }
            });
        }
        LogUtil.d(TAG, "setLoadingSuccess, loadingStatus: " + this.loadingStatus);
    }

    public void loadSuccess(String successStr) {
        setSuccessStr(successStr);
        loadSuccess();
    }

    public void loadFailed() {
        this.loadingStatus = STATUS_FAILED;
    }

    public void loadFailed(String failedStr) {
        setFailedStr(failedStr);
        loadFailed();
    }

    public void setHelperStr(String loadingStr, String failedStr, String successStr) {
        this.loadingStr = loadingStr;
        this.failedStr = failedStr;
        this.successStr = successStr;
    }

    public void setFailedStr(String failedStr) {
        this.failedStr = failedStr;
    }

    public void setSuccessStr(String successStr) {
        this.successStr = successStr;
    }

    public void dismissLoading() {
        PopupWindow popupWindow = this.mLoadingAnimationPopup;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.mLoadingAnimationPopup.dismiss();
        }
        LottieAnimationView loadingAnimationView = this.mLoadingAnimationView;
        if (loadingAnimationView != null && loadingAnimationView.isAnimating()) {
            this.mLoadingAnimationView.cancelAnimation();
        }
        LottieAnimationView succeedAnimationView = this.mSucceedAnimationView;
        if (succeedAnimationView != null && succeedAnimationView.isAnimating()) {
            this.mSucceedAnimationView.cancelAnimation();
        }
    }

    public void setOnSuccessAction(Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }

    public void setSuccessAutoDismiss(boolean successAutoDismiss) {
        this.isSuccessAutoDismiss = successAutoDismiss;
    }

    public void setAutoLoadFail(boolean autoLoadFail) {
        this.isAutoLoadFail = autoLoadFail;
    }

    public boolean isShowing() {
        PopupWindow popupWindow = this.mLoadingAnimationPopup;
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }
}