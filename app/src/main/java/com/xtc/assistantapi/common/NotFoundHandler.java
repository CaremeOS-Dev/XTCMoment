package com.xtc.assistantapi.common;

import com.xtc.assistantapi.core.DirectiveCallback;
import com.xtc.assistantapi.core.DirectiveHandler;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

/**
 * 未找到处理器时返回 404。
 */
public class NotFoundHandler extends DirectiveHandler {

    @Override
    protected boolean canHandle(DirectiveRequest request) {
        return false;
    }

    public static NotFoundHandler getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    protected void handle(DirectiveRequest request, DirectiveCallback callback) {
        callback.onError(new DirectiveResponse(404, "not found directive"));
    }

    private static class Holder {
        private static final NotFoundHandler INSTANCE = new NotFoundHandler();

        private Holder() {
        }
    }
}