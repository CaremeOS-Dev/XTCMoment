package com.xtc.moment.behavior;

import android.text.TextUtils;

/**
 * 从上传/发布异常中解析埋点失败原因。
 */
public class DigitalHelper {

    public static String getFailReson(Throwable throwable, String defaultReason) {
        if (throwable == null) {
            return DigitalConstant.ErrorCode.UNKNOWN_ERROR;
        }
        String message = throwable.getMessage();
        if (TextUtils.isEmpty(message)) {
            return DigitalConstant.ErrorCode.UNKNOWN_ERROR;
        }
        if (message.contains(DigitalConstant.FailReason.ERROR_CODE_PARAMS)) {
            return DigitalConstant.FailReason.ERROR_CODE_PARAMS;
        }
        if (message.contains("000008")) {
            return "000008";
        }
        if (message.contains(DigitalConstant.FailReason.ERROR_CODE_PACKAGE)) {
            return DigitalConstant.FailReason.ERROR_CODE_PACKAGE;
        }
        if (message.contains(DigitalConstant.FailReason.ERROR_CODE_SHARE_H5)) {
            return DigitalConstant.FailReason.ERROR_CODE_SHARE_H5;
        }
        if (message.contains("000005")) {
            return "000005";
        }
        if (message.contains(DigitalConstant.FailReason.ERROR_CODE_UNLOCK)) {
            return DigitalConstant.FailReason.ERROR_CODE_UNLOCK;
        }
        if (message.contains("000060")) {
            return "000060";
        }
        if (message.contains("000061")) {
            return "000061";
        }
        return message.contains(DigitalConstant.FailReason.ERROR_CODE_OPERATE_FAIL)
                ? DigitalConstant.FailReason.ERROR_CODE_OPERATE_FAIL
                : defaultReason;
    }
}