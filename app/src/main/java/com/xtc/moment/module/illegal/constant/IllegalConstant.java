package com.xtc.moment.module.illegal.constant;

/**
 * 违规处置常量。
 */
public interface IllegalConstant {

    interface IllegalServerStatus {
        int NORMAL_STATUS = 0;
        int ILLEGAL_STATUS = 1;
        int DELAY_STATUS = 2;
        int DISABLE_STATUS = 3;
    }

    interface IllegalServerType {
        int DELAY_TYPE = 0;
        int DISABLE_TYPE = 1;
        int DE_BLOCK_TYPE = 2;
    }
}