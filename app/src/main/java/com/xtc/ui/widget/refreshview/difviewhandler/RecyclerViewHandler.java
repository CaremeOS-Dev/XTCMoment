package com.xtc.ui.widget.refreshview.difviewhandler;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.refreshview.interfaces.LoadMoreHandler;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollBottomListener;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollCallBackListener;
import com.xtc.ui.widget.refreshview.interfaces.PtlmUIHandler;

/** RecyclerView 的加载更多适配实现。 */
public class RecyclerViewHandler implements LoadMoreHandler {
    private int direction = 0;
    private View mFooter;
    private RecyclerAdapterWithHF mRecyclerAdapter;

    @Override
    public void setOnScrollBottomCallBackListener(View view, OnScrollBottomListener bottomListener, OnScrollCallBackListener scrollCallBackListener) {
    }

    @Override
    public void refreshDirection(int newDirection) {
        this.direction = newDirection;
    }

    @Override
    public boolean handleSetAdapter(View view, PtlmUIHandler.ILoadMoreView loadMoreView, View.OnClickListener onClickListener) {
        final RecyclerView recyclerView = (RecyclerView) view;
        this.mRecyclerAdapter = (RecyclerAdapterWithHF) recyclerView.getAdapter();
        if (loadMoreView == null) {
            return false;
        }
        final Context applicationContext = recyclerView.getContext().getApplicationContext();
        loadMoreView.init(new PtlmUIHandler.FootViewAdder() {
            @Override
            public View addFootView(int layoutResId) {
                View footView = LayoutInflater.from(applicationContext).inflate(layoutResId, (ViewGroup) recyclerView, false);
                RecyclerViewHandler.this.mFooter = footView;
                return addFootView(footView);
            }

            @Override
            public View addFootView(View footView) {
                RecyclerViewHandler.this.mRecyclerAdapter.addFooter(footView);
                return footView;
            }
        }, onClickListener);
        return true;
    }

    @Override
    public void addFooter() {
        View footer;
        if (this.mRecyclerAdapter.getFootSize() > 0 || (footer = this.mFooter) == null) {
            return;
        }
        this.mRecyclerAdapter.addFooter(footer);
    }

    @Override
    public void removeFooter() {
        View footer;
        if (this.mRecyclerAdapter.getFootSize() <= 0 || (footer = this.mFooter) == null) {
            return;
        }
        this.mRecyclerAdapter.removeFooter(footer);
    }

    @Override
    public void setOnScrollBottomListener(View view, OnScrollBottomListener bottomListener) {
        ((RecyclerView) view).addOnScrollListener(new RecyclerViewOnScrollListener(bottomListener));
    }

    private static boolean canRecyclerViewScrollBottom(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstCompletelyVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
            LogUtil.d("test", "canRecyclerViewScrollUp findFirstCompletelyVisibleItemPosition = " + firstCompletelyVisible);
            View firstChild = recyclerView.getChildAt(0);
            if (firstChild == null) {
                return false;
            }
            if (firstCompletelyVisible == 0) {
                if (firstChild.getHeight() == 0) {
                    return false;
                }
            } else {
                int decoratedTop = layoutManager.getDecoratedTop(firstChild);
                LogUtil.d("test", "canRecyclerViewScrollUp firstVisibleViewTop = " + decoratedTop);
                if (decoratedTop == 0) {
                    return false;
                }
            }
        }
        return ViewCompat.canScrollVertically(recyclerView, 1);
    }

    /** RecyclerView 滚动监听。 */
    private static class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
        private OnScrollBottomListener onScrollBottomListener;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        public RecyclerViewOnScrollListener(OnScrollBottomListener bottomListener) {
            this.onScrollBottomListener = bottomListener;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            OnScrollBottomListener bottomListener;
            if (newState == 0 && isScollBottom(recyclerView) && (bottomListener = this.onScrollBottomListener) != null) {
                bottomListener.onScorllBootom();
            }
        }

        private boolean isScollBottom(RecyclerView recyclerView) {
            return !isCanScollVertically(recyclerView);
        }

        private boolean isCanScollVertically(RecyclerView recyclerView) {
            if (Build.VERSION.SDK_INT < 14) {
                return ViewCompat.canScrollVertically(recyclerView, 1) || recyclerView.getScrollY() < recyclerView.getHeight();
            }
            return ViewCompat.canScrollVertically(recyclerView, 1);
        }
    }
}