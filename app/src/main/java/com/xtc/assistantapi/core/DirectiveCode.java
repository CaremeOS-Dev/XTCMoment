package com.xtc.assistantapi.core;

/**
 * 指令处理结果码。
 */
public interface DirectiveCode {

    /** 处理成功。 */
    int SUCCESS = 200;
    /** 已消费但无返回。 */
    int CONSUMED = 300;
    /** 请求错误。 */
    int BAD_REQUEST = 400;
    /** 无权限。 */
    int FORBIDDEN = 403;
    /** 未找到处理者。 */
    int NOT_FOUND = 404;
    /** 内部错误。 */
    int INTERNAL_ERROR = 500;
}