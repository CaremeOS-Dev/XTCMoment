package com.xtc.assistantapi.core;

import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;
import com.xtc.assistantapi.util.PriorityList;

import java.util.Iterator;
import java.util.List;

/**
 * 链式处理器，按优先级依次尝试处理指令，任一处理完成即停止。
 */
public class ChainedHandler extends DirectiveHandler {

    private final PriorityList<DirectiveHandler> handlers = new PriorityList<>();

    public ChainedHandler addHandler(DirectiveHandler handler, int priority) {
        this.handlers.add(handler, priority);
        return this;
    }

    public ChainedHandler addHandler(DirectiveHandler handler) {
        return addHandler(handler, 0);
    }

    protected List<DirectiveHandler> getHandlers() {
        return this.handlers;
    }

    @Override
    protected boolean canHandle(DirectiveRequest request) {
        return !this.handlers.isEmpty();
    }

    @Override
    protected void handle(DirectiveRequest request, DirectiveCallback callback) {
        handle(this.handlers.iterator(), request, callback);
    }

    private void handle(final Iterator<DirectiveHandler> iterator, DirectiveRequest request,
                        final DirectiveCallback callback) {
        if (iterator.hasNext()) {
            iterator.next().process(request, new DirectiveCallback() {
                @Override
                public void onNext(DirectiveRequest request) {
                    handle(iterator, request, callback);
                }

                @Override
                public void onComplete(DirectiveResponse response) {
                    callback.onComplete(response);
                }

                @Override
                public void onError(DirectiveResponse response) {
                    callback.onError(response);
                }
            });
        } else {
            callback.onNext(request);
        }
    }
}