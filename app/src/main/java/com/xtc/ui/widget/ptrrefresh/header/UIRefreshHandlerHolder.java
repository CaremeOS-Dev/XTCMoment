package com.xtc.ui.widget.ptrrefresh.header;

import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 下拉刷新 UI 回调的责任链容器。 */
public class UIRefreshHandlerHolder implements UIRefreshHandler {
    private UIRefreshHandler mHandler;
    private UIRefreshHandlerHolder mNext;

    private boolean contains(UIRefreshHandler handler) {
        UIRefreshHandler currentHandler = this.mHandler;
        return currentHandler != null && currentHandler == handler;
    }

    private UIRefreshHandlerHolder() {
    }

    public boolean hasHandler() {
        return this.mHandler != null;
    }

    private UIRefreshHandler getHandler() {
        return this.mHandler;
    }

    public static void addHandler(UIRefreshHandlerHolder holder, UIRefreshHandler handler) {
        if (handler == null || holder == null) {
            return;
        }
        if (holder.mHandler == null) {
            holder.mHandler = handler;
            return;
        }
        while (!holder.contains(handler)) {
            UIRefreshHandlerHolder next = holder.mNext;
            if (next == null) {
                UIRefreshHandlerHolder newHolder = new UIRefreshHandlerHolder();
                newHolder.mHandler = handler;
                holder.mNext = newHolder;
                return;
            }
            holder = next;
        }
    }

    public static UIRefreshHandlerHolder create() {
        return new UIRefreshHandlerHolder();
    }

    public static UIRefreshHandlerHolder removeHandler(UIRefreshHandlerHolder holder, UIRefreshHandler handler) {
        if (holder == null || handler == null || holder.mHandler == null) {
            return holder;
        }
        UIRefreshHandlerHolder head = holder;
        UIRefreshHandlerHolder previous = null;
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
        return head == null ? new UIRefreshHandlerHolder() : head;
    }

    @Override
    public void onUIReset(BaseFrameLayout frameLayout) {
        UIRefreshHandlerHolder holder = this;
        do {
            UIRefreshHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIReset(frameLayout);
            }
            holder = holder.mNext;
        } while (holder != null);
    }

    @Override
    public void onUIRefreshPrepare(BaseFrameLayout frameLayout) {
        if (hasHandler()) {
            UIRefreshHandlerHolder holder = this;
            do {
                UIRefreshHandler handler = holder.getHandler();
                if (handler != null) {
                    handler.onUIRefreshPrepare(frameLayout);
                }
                holder = holder.mNext;
            } while (holder != null);
        }
    }

    @Override
    public void onUIRefreshBegin(BaseFrameLayout frameLayout) {
        UIRefreshHandlerHolder holder = this;
        do {
            UIRefreshHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIRefreshBegin(frameLayout);
            }
            holder = holder.mNext;
        } while (holder != null);
    }

    @Override
    public void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess) {
        UIRefreshHandlerHolder holder = this;
        do {
            UIRefreshHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIRefreshComplete(frameLayout, isSuccess);
            }
            holder = holder.mNext;
        } while (holder != null);
    }

    @Override
    public void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator) {
        UIRefreshHandlerHolder holder = this;
        do {
            UIRefreshHandler handler = holder.getHandler();
            if (handler != null) {
                handler.onUIPositionChange(frameLayout, isUnderTouch, status, indicator);
            }
            holder = holder.mNext;
        } while (holder != null);
    }
}