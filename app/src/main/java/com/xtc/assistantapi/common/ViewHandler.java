package com.xtc.assistantapi.common;

import com.xtc.assistantapi.core.DirectiveCallback;
import com.xtc.assistantapi.core.DirectiveHandler;
import com.xtc.assistantapi.message.DirectiveRequest;

/**
 * 视图处理器占位实现（原实现不处理任何指令）。
 */
public class ViewHandler extends DirectiveHandler {

    @Override
    protected void handle(DirectiveRequest request, DirectiveCallback callback) {
    }

    @Override
    protected boolean canHandle(DirectiveRequest request) {
        return false;
    }
}