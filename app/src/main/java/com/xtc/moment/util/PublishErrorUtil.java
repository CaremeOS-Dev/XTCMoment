package com.xtc.moment.util;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.illegal.widget.AccountBannedDialog;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.ReviewRequestBean;
import com.xtc.moment.serve.ServerCache;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 发布失败提示的统一映射。
 *
 * <p>服务端业务错误码经 {@code HttpRxJavaCallback} 抛出后，异常 message 即原始 code，
 * 由各 Presenter/Task 透传到 View 的 {@code publishFail(String)}。此前每个页面各写一份
 * if-else，新增错误码时容易漏改，这里统一收口。</p>
 */
public final class PublishErrorUtil {

    private static final String TAG = "PublishErrorUtil";

    /** 防止同一个封禁错误在多个页面重复弹窗。 */
    private static volatile boolean bannedDialogShowing;

    /** 请求处理中。 */
    private static final String ERROR_CODE_REQUESTING = "1002";
    /** 请求过于频繁。 */
    private static final String ERROR_CODE_FREQUENT_REQUEST = "1003";
    /** 账号异常（主错误码）。 */
    private static final String ERROR_CODE_ACCOUNT_ABNORMAL = "000007";

    private PublishErrorUtil() {
    }

    /**
     * 解析发布失败提示文案。
     *
     * @param message 发布失败回调透传的错误码，允许为 null
     * @return 文案资源 id；未识别时返回「发布失败」
     */
    public static int getFailMessageRes(String message) {
        if (TextUtils.isEmpty(message)) {
            return R.string.publish_fail;
        }
        if (message.contains(ERROR_CODE_REQUESTING) || message.contains(ERROR_CODE_FREQUENT_REQUEST)) {
            return R.string.frequent_request;
        }
        if (message.contains(ERROR_CODE_ACCOUNT_ABNORMAL)) {
            return R.string.account_abnormal;
        }
        return R.string.publish_fail;
    }

    /**
     * 展示发布失败提示。调用方若对无网络有特殊处理，请自行提前判断。
     */
    public static void showFailMessage(Context context, String message) {
        if (isAccountBanned(message)) {
            // 账号封禁不是「发布失败」，而是一个需要申诉/导出的独立弹窗。
            showAccountBannedDialog(context);
            return;
        }
        ToastUtil.showShortCover(context, getFailMessageRes(message));
    }

    /** @return true when the server code is the suspended-account one. */
    public static boolean isAccountBanned(String message) {
        return !TextUtils.isEmpty(message) && message.contains(ERROR_CODE_ACCOUNT_ABNORMAL);
    }

    /**
     * 从异常里识别封禁并弹窗。
     *
     * <p>{@code HttpRxJavaCallback} 抛出的异常 message 就是服务端 code，
     * 因此这里直接匹配 000007。</p>
     *
     * @return true 表示已识别为封禁
     */
    public static boolean maybeShowAccountBanned(Context context, Throwable throwable) {
        String message = throwable == null ? null : throwable.getMessage();
        if (!isAccountBanned(message)) {
            return false;
        }
        showAccountBannedDialog(context);
        return true;
    }

    /**
     * 展示「你的账户已被封禁」弹窗。
     *
     * <p>文案与线上一致：标题、正文，以及「申请复核」「下载我发布过的动态」
     * 「我知道了」三个按钮。申请复核会调用 {@code /extra/review-request}；
     * 该接口对封禁账号放行，因此这里不会再次拿到 000007。</p>
     */
    public static void showAccountBannedDialog(final Context context) {
        if (context == null || bannedDialogShowing) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            com.xtc.moment.util.HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    showAccountBannedDialog(context);
                }
            });
            return;
        }
        bannedDialogShowing = true;
        AccountBannedDialog dialog = new AccountBannedDialog(context);
        dialog.setBannedDialogListener(new AccountBannedDialog.BannedDialogListener() {
            @Override
            public void onAppealClick() {
                submitReviewRequest(context);
            }

            @Override
            public void onDownloadClick() {
                // 「下载我发布过的动态」：打开好友圈主页面，用户自己的动态在那里，
                // 可以逐条查看/保存。原厂没有批量导出接口，这里保持同样行为。
                try {
                    android.content.Intent intent = new android.content.Intent();
                    intent.setClassName(context, "com.xtc.moment.module.main.MomentActivity");
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    LogUtil.e(TAG, "open my moments failed: ", e);
                }
                dialog.dismiss();
            }

            @Override
            public void onConfirmClick() {
                // 「我知道了」：仅关闭弹窗。
            }
        });
        dialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(android.content.DialogInterface dialogInterface) {
                bannedDialogShowing = false;
            }
        });
        dialog.show();
    }

    private static void submitReviewRequest(final Context context) {
        ReviewRequestBean body = new ReviewRequestBean();
        body.setWatchId(com.xtc.moment.MomentApp.getWatchId());
        body.setReason("");
        body.setContact("");
        MomentHttpServiceProxy proxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
        if (proxy == null) {
            LogUtil.e(TAG, "submitReviewRequest: proxy is null");
            return;
        }
        proxy.submitReviewRequest(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "submitReviewRequest failed: ", throwable);
                    }

                    @Override
                    public void onNext(String value) {
                        LogUtil.i(TAG, "submitReviewRequest success: " + value);
                        Toast.makeText(context, R.string.account_banned_review_sent,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
