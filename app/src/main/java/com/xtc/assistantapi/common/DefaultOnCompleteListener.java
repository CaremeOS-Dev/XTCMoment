package com.xtc.assistantapi.common;

import com.xtc.assistantapi.core.OnCompleteListener;
import com.xtc.assistantapi.message.DirectiveRequest;

/**
 * 默认完成监听，不做任何处理。
 */
public class DefaultOnCompleteListener implements OnCompleteListener {

    @Override
    public void onComplete(DirectiveRequest request) {
    }

    @Override
    public void onError(DirectiveRequest request, int errorCode) {
    }

    public static DefaultOnCompleteListener getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final DefaultOnCompleteListener INSTANCE = new DefaultOnCompleteListener();

        private Holder() {
        }
    }
}