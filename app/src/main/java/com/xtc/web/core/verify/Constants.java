package com.xtc.web.core.verify;

/** H5 白名单校验相关常量。 */
public class Constants {

    /** H5 请求拦截埋点与结果码。 */
    interface Intercepter {
        int CACHE_EMPTY = 0;
        String FUNCTION_NAME_INTERCEPTE_H5 = "function_name_intercepte_h5";
        String INTERCEPTER_RESULT = "intercepter_result";
        String INTERCEPTER_URL_VALUE = "intercepter_url_value";
        int LOCAL_VERIFY = 3;
        int VERIFY_FAILED = 2;
        int VERIFY_SUCCESS = 1;
    }

    /** 校验相关的数据库表名。 */
    interface TableName {
        String SHARE_WHITE_TABLE_NAME = "verify.db";
    }
}