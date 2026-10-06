package com.xtc.assistantapi.core;

import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

/**
 * 指令处理回调，链式传递指令与结果。
 */
public interface DirectiveCallback extends DirectiveCode {

    /** 继续把指令传递给下一个处理者。 */
    void onNext(DirectiveRequest request);

    /** 处理完成。 */
    void onComplete(DirectiveResponse response);

    /** 处理失败。 */
    void onError(DirectiveResponse response);
}