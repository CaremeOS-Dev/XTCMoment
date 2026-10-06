package com.xtc.assistantapi.core;

import com.xtc.assistantapi.message.DirectiveRequest;

/**
 * 指令处理完成监听。
 */
public interface OnCompleteListener extends DirectiveCode {

    /** 处理完成。 */
    void onComplete(DirectiveRequest request);

    /** 处理失败并返回错误码。 */
    void onError(DirectiveRequest request, int errorCode);
}