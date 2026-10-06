package com.xtc.moment.behavior;

/**
 * 发布行为埋点使用的错误码常量集合。
 */
public interface DigitalConstant {

    interface ErrorCode {
        String BEFORE_TOKEN_ERROR = "9002";
        String GET_TOKEN_ERROR = "9003";
        String NET_ERROR = "9001";
        String PUBLIC_ERROR = "9005";
        String SAVE_THUMBNAIL_ERROR = "9006";
        String UNKNOWN_ERROR = "9999";
        String UPLOAD_QINIU_ERROR = "9004";
    }

    interface FailReason {
        String COMPRESS_ERROR = "8881";
        String ERROR_CODE_ILLEGAL = "000008";
        String ERROR_CODE_LIMIT = "000060";
        String ERROR_CODE_OPERATE_FAIL = "000058";
        String ERROR_CODE_PACKAGE = "000064";
        String ERROR_CODE_PARAMS = "000007";
        String ERROR_CODE_RESOURCE_NONENTITY = "000005";
        String ERROR_CODE_SENSITIVE = "000061";
        String ERROR_CODE_SHARE_H5 = "000065";
        String ERROR_CODE_UNLOCK = "999999";
        String ILLEGAL_OPERATE = "000008";
        String INTERFACE_NET_ERROR = "9005";
        String UPLOAD_ERROR = "8882";
    }

    interface MonitorError {
        String ILLEGAL_OPERATE = "000008";
        String MONITOR_ERROR_BOOT_LIMIT_TIME = "1001";
        String MONITOR_ERROR_FREQUENT_REQUEST = "1003";
        String MONITOR_ERROR_HEAVY_TRAFFIC = "1004";
        String MONITOR_ERROR_NO_NETWORK_PERMISSION = "1006";
        String MONITOR_ERROR_REQUESTING = "1002";
        String MONITOR_ERROR_UNKNOWN = "1005";
    }
}