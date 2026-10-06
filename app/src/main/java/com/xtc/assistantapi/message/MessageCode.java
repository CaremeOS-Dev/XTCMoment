package com.xtc.assistantapi.message;

/**
 * 消息结果码。
 */
public interface MessageCode {

    /** 处理完成。 */
    String CODE_COMPLETE = "000001";
    /** 继续处理。 */
    String CODE_NEXT = "000002";
    /** 处理出错。 */
    String CODE_ERROR = "000003";
}