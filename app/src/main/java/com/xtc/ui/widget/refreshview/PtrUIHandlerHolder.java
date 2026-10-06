package com.xtc.ui.widget.refreshview;

import com.xtc.ui.widget.refreshview.interfaces.PtrUIHandler;

/** 下拉刷新 UI 回调的责任链容器。 */
class PtrUIHandlerHolder implements PtrUIHandler {
    private PtrUIHandlerHolder mNext;
    private PtrUIHandler mPtrUIHandler;

    private boolean contains(PtrUIHandler handler) {
        PtrUIHandler currentHandler = this.mPtrUIHandler;
        return currentHandler != null && currentHandler == handler;
    }

    private PtrUIHandlerHolder() {
    }

    public boolean hasHandler() {
        return this.mPtrUIHandler != null;
    }

    private PtrUIHandler getHandler() {
        return this.mPtrUIHandler;
    }

    public static void addHandler(PtrUIHandlerHolder holder, PtrUIHandler handler) {
        if (handler == null || holder == null) {
            return;
        }
        if (holder.mPtrUIHandler == null) {
            holder.mPtrUIHandler = handler;
            return;
        }
        while (!holder.contains(handler)) {
            PtrUIHandlerHolder next = holder.mNext;
            if (next == null) {
                PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
                newHolder.mPtrUIHandler = handler;
                holder.mNext = newHolder;
                return;
            }
            holder = next;
        }
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder holder, PtrUIHandler handler) {
        if (holder == null || handler == null || holder.mPtrUIHandler == null) {
            return holder;
        }
        PtrUIHandlerHolder head = holder;
        PtrUIHandlerHolder previous = null;
        do {
            if (!holder.contains(handler)) {
                previous = holder;
                holder = holder.mNext;
            } else if (previous == null) {
                head = holder.mNext;
                holder.mNext = null;
                holder = head;
            } else {
                previous.mNext = holder.mNext;
                holder.mNext = null;
                holder = previous.mNext;
            }
        } while (holder != null);
        return head == null ? new PtrUIHandlerHolder() : head;
    }

    @Override
    public void onUIReset(PtrFrameLayout frameLayout) {
        PtrUIHandlerHolder holder = this;
        do {
            PtrUIHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIReset(frameLayout);
            }
            holder = holder.mNext;
        } while (holder != null);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frameLayout) {
        if (hasHandler()) {
            PtrUIHandlerHolder holder = this;
            do {
                PtrUIHandler handler = holder.getHandler();
                if (handler != null) {
                    handler.onUIRefreshPrepare(frameLayout);
                }
                holder = holder.mNext;
            } while (holder != null);
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frameLayout) {
        PtrUIHandlerHolder holder = this;
        do {
            PtrUIHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIRefreshBegin(frameLayout);
            }
            holder = holder.mNext;
        } while (holder != null);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frameLayout) {
        PtrUIHandlerHolder holder = this;
        do {
            PtrUIHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIRefreshComplete(frameLayout);
            }
            holder = holder.mNext;
        } while (holder != null);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frameLayout, boolean isUnderTouch, byte status, PtrIndicator indicator) {
        PtrUIHandlerHolder holder = this;
        do {
            PtrUIHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIPositionChange(frameLayout, isUnderTouch, status, indicator);
            }
            holder = holder.mNext;
        } while (holder != null);
    }
}