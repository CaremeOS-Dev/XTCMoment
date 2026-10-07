package com.xtc.moment.util;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.moment.R;

/**
 * 发布失败提示的统一映射。
 *
 * <p>服务端业务错误码经 {@code HttpRxJavaCallback} 抛出后，异常 message 即原始 code，
 * 由各 Presenter/Task 透传到 View 的 {@code publishFail(String)}。此前每个页面各写一份
 * if-else，新增错误码时容易漏改，这里统一收口。</p>
 */
public final class PublishErrorUtil {

    /** 请求处理中。 */
    private static final String ERROR_CODE_REQUESTING = "1002";
    /** 请求过于频繁。 */
    private static final String ERROR_CODE_FREQUENT_REQUEST = "1003";
    /** 账号异常（主错误码）。 */
    private static final String ERROR_CODE_ACCOUNT_ABNORMAL = "000007";
    /** 账号异常（备用错误码，服务端部分场景返回）。 */
    private static final String ERROR_CODE_ACCOUNT_ABNORMAL_ALT = "000002";

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
        if (message.contains(ERROR_CODE_ACCOUNT_ABNORMAL) || message.contains(ERROR_CODE_ACCOUNT_ABNORMAL_ALT)) {
            return R.string.account_abnormal;
        }
        return R.string.publish_fail;
    }

    /**
     * 展示发布失败提示。调用方若对无网络有特殊处理，请自行提前判断。
     */
    public static void showFailMessage(Context context, String message) {
        ToastUtil.showShortCover(context, getFailMessageRes(message));
    }
}
