package com.xtc.ui.widget.ptrrefresh.footer.LoadMoreHelper;

import android.view.View;
import android.widget.AbsListView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.ptrrefresh.footer.LoadMoreView;
import com.xtc.ui.widget.ptrrefresh.footer.OnLoadMoreListener;

/** 加载更多辅助基类：维护加载状态与视图切换。 */
public abstract class LoadMoreHelper {
    private static final String TAG = "LoadMoreHelper";
    protected OnCallbackScrollListener mOnCallbackScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoadingMore = false;
    private boolean isLoadMoreEnable = false;
    private LoadMoreView mLoadMoreView = null;

    /** 滚动回调。 */
    public interface OnCallbackScrollListener {
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

        void onScrollStateChanged(AbsListView view, int scrollState);
    }

    public abstract void addFooter();

    public abstract void attachToView(View view, LoadMoreView loadMoreView);

    public abstract void removeFooter();

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.isLoadMoreEnable = loadMoreEnable;
        if (this.isLoadMoreEnable) {
            addFooter();
        } else {
            removeFooter();
        }
    }

    protected void setLoadMoreView(LoadMoreView loadMoreView) {
        this.mLoadMoreView = loadMoreView;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void loadMoreComplete(boolean loadMoreEnable) {
        LoadMoreView loadMoreView;
        this.isLoadingMore = false;
        this.isLoadMoreEnable = loadMoreEnable;
        if (loadMoreEnable && (loadMoreView = this.mLoadMoreView) != null) {
            loadMoreView.showNormal();
        } else {
            setNoMoreData();
        }
    }

    protected void OnClickLoadMore() {
        if (!this.isLoadMoreEnable || isLoadingMore()) {
            return;
        }
        LogUtil.d(TAG, "OnClickLoadMore isLoadingMore = " + isLoadingMore());
        loadMore();
    }

    protected void onScrollBottom() {
        if (!this.isLoadMoreEnable || isLoadingMore()) {
            return;
        }
        LogUtil.d(TAG, "onScrollBottom isLoadingMore = " + isLoadingMore());
        loadMore();
    }

    public void loadMore() {
        LoadMoreView loadMoreView = this.mLoadMoreView;
        if (loadMoreView == null || this.mOnLoadMoreListener == null) {
            return;
        }
        this.isLoadingMore = true;
        loadMoreView.showLoading();
        this.mOnLoadMoreListener.loadMore();
    }

    public void setNoMoreData() {
        LoadMoreView loadMoreView = this.mLoadMoreView;
        if (loadMoreView != null) {
            loadMoreView.showNoMore();
        }
    }

    public boolean isLoadingMore() {
        return this.isLoadingMore;
    }

    public void setOnCallbackScrollListener(OnCallbackScrollListener callbackScrollListener) {
        this.mOnCallbackScrollListener = callbackScrollListener;
    }
}