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

public class LoadingViewHolder {

    private static final String TAG = "LoadingViewHolder";
    private static final int REPEAT_MAX_TIMES = 45;

    public static final int STATUS_FAILED = -1;
    public static final int STATUS_LOADING = 0;
    public static final int STATUS_SUCCESS = 1;

    private Context mContext;
    private PopupWindow mLoadingAnimationPopup;
    private LottieAnimationView mLoadingAnimationView;
    private LottieAnimationView mSucceedAnimationView;
    private TextView tipsText;
    private int loadingStatus;
    private int mTimes;
    private String loadingStr;
    private String failedStr;
    private String successStr;
    private Runnable onSuccessAction;
    private boolean isSuccessAutoDismiss = true;
    private boolean isAutoLoadFail = true;

    public LoadingViewHolder(Context context) {
        mContext = context;
        initLoadingAnimation(context);
    }

    private void initLoadingAnimation(Context context) {
        View content = LayoutInflater.from(context).inflate(R.layout.loading_view, (ViewGroup) null);
        tipsText = (TextView) content.findViewById(R.id.loading_text);
        mLoadingAnimationView = (LottieAnimationView) content.findViewById(R.id.loading_animation_view);
        LottieComposition.Factory.fromAssetFileName(context, "bigloading.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                mLoadingAnimationView.setComposition(composition);
                mLoadingAnimationView.loop(true);
            }
        });
        mSucceedAnimationView = (LottieAnimationView) content.findViewById(R.id.succeed_animation_view);
        LottieComposition.Factory.fromAssetFileName(context, "succeed.json", new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                mSucceedAnimationView.setComposition(composition);
                mSucceedAnimationView.loop(false);
            }
        });
        addAnimatorListener();
        mLoadingAnimationPopup = new PopupWindow(content, -1, -1, false);
        mLoadingAnimationPopup.setOutsideTouchable(false);
    }

    private void addAnimatorListener() {
        mLoadingAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (!TextUtils.isEmpty(loadingStr)) {
                    tipsText.setText(loadingStr);
                }
                loadingStatus = STATUS_LOADING;
                LogUtil.d(TAG, "onAnimationStart");
                mLoadingAnimationView.setVisibility(View.VISIBLE);
                mSucceedAnimationView.setVisibility(View.GONE);
                mTimes = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mSucceedAnimationView.setVisibility(View.VISIBLE);
                mLoadingAnimationView.setVisibility(View.GONE);
                if (loadingStatus == STATUS_SUCCESS) {
                    mSucceedAnimationView.playAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                mTimes++;
                LogUtil.d(TAG, "repeatTimes: " + mTimes + ", loadingStatus: " + loadingStatus);
                if (mTimes == REPEAT_MAX_TIMES && isAutoLoadFail && loadingStatus != STATUS_SUCCESS) {
                    LogUtil.i(TAG, "onAnimationRepeat: " + mTimes);
                    loadingStatus = STATUS_FAILED;
                }
                int status = loadingStatus;
                if (status == STATUS_FAILED) {
                    showLoadFailed();
                } else if (status == STATUS_SUCCESS) {
                    mLoadingAnimationView.cancelAnimation();
                }
            }
        });
        mSucceedAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (!TextUtils.isEmpty(successStr)) {
                    tipsText.setText(successStr);
                }
                mLoadingAnimationView.setVisibility(View.GONE);
                mSucceedAnimationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (onSuccessAction != null) {
                    onSuccessAction.run();
                }
                if (isSuccessAutoDismiss) {
                    dismissLoading();
                }
            }
        });
    }

    private void showLoadFailed() {
        if (!TextUtils.isEmpty(failedStr)) {
            ToastUtil.showShort(mContext, failedStr);
        }
        dismissLoading();
    }

    public void showLoading(View view) {
        mLoadingAnimationPopup.showAtLocation(view, 17, 0, 0);
        mLoadingAnimationView.playAnimation();
    }

    public void loadSuccess() {
        loadingStatus = STATUS_SUCCESS;
        if (mLoadingAnimationView != null && mSucceedAnimationView != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mLoadingAnimationView.cancelAnimation();
                    mSucceedAnimationView.playAnimation();
                }
            });
        }
        LogUtil.d(TAG, "setLoadingSuccess, loadingStatus: " + loadingStatus);
    }

    public void loadSuccess(String successStr) {
        setSuccessStr(successStr);
        loadSuccess();
    }

    public void loadFailed() {
        loadingStatus = STATUS_FAILED;
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
        if (mLoadingAnimationPopup != null && mLoadingAnimationPopup.isShowing()) {
            mLoadingAnimationPopup.dismiss();
        }
        if (mLoadingAnimationView != null && mLoadingAnimationView.isAnimating()) {
            mLoadingAnimationView.cancelAnimation();
        }
        if (mSucceedAnimationView != null && mSucceedAnimationView.isAnimating()) {
            mSucceedAnimationView.cancelAnimation();
        }
    }

    public void setOnSuccessAction(Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }

    public void setSuccessAutoDismiss(boolean autoDismiss) {
        isSuccessAutoDismiss = autoDismiss;
    }

    public void setAutoLoadFail(boolean autoLoadFail) {
        isAutoLoadFail = autoLoadFail;
    }

    public boolean isShowing() {
        return mLoadingAnimationPopup != null && mLoadingAnimationPopup.isShowing();
    }
}