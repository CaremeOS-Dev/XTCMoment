package com.xtc.ui.widget.refreshview.difviewhandler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.xtc.ui.widget.refreshview.interfaces.LoadMoreHandler;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollBottomListener;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollCallBackListener;
import com.xtc.ui.widget.refreshview.interfaces.PtlmUIHandler;

/** 带页眉页脚的 GridView 加载更多适配实现。 */
public class GridViewHandler implements LoadMoreHandler {
    private int direction = 0;
    private View mFooter;
    private GridViewWithHeaderAndFooter mGridView;

    @Override
    public void refreshDirection(int newDirection) {
        this.direction = newDirection;
    }

    @Override
    public boolean handleSetAdapter(View view, PtlmUIHandler.ILoadMoreView loadMoreView, View.OnClickListener onClickListener) {
        this.mGridView = (GridViewWithHeaderAndFooter) view;
        ListAdapter adapter = this.mGridView.getAdapter();
        if (loadMoreView == null) {
            return false;
        }
        final Context applicationContext = this.mGridView.getContext().getApplicationContext();
        loadMoreView.init(new PtlmUIHandler.FootViewAdder() {
            @Override
            public View addFootView(int layoutResId) {
                View footView = LayoutInflater.from(applicationContext).inflate(layoutResId, (ViewGroup) GridViewHandler.this.mGridView, false);
                GridViewHandler.this.mFooter = footView;
                return addFootView(footView);
            }

            @Override
            public View addFootView(View footView) {
                GridViewHandler.this.mGridView.addFooterView(footView);
                return footView;
            }
        }, onClickListener);
        if (adapter == null) {
            return true;
        }
        this.mGridView.setAdapter(adapter);
        return true;
    }

    @Override
    public void addFooter() {
        View footer;
        if (this.mGridView.getFooterViewCount() > 0 || (footer = this.mFooter) == null) {
            return;
        }
        this.mGridView.addFooterView(footer);
    }

    @Override
    public void removeFooter() {
        View footer;
        if (this.mGridView.getFooterViewCount() <= 0 || (footer = this.mFooter) == null) {
            return;
        }
        this.mGridView.removeFooterView(footer);
    }

    @Override
    public void setOnScrollBottomListener(View view, OnScrollBottomListener bottomListener) {
        GridViewWithHeaderAndFooter gridView = (GridViewWithHeaderAndFooter) view;
        gridView.setOnScrollListener(new GridViewOnScrollListener(bottomListener));
        gridView.setOnItemSelectedListener(new GridViewOnItemSelectedListener(bottomListener));
    }

    @Override
    public void setOnScrollBottomCallBackListener(View view, OnScrollBottomListener bottomListener, OnScrollCallBackListener scrollCallBackListener) {
        GridViewWithHeaderAndFooter gridView = (GridViewWithHeaderAndFooter) view;
        gridView.setOnScrollListener(new GridViewOnScrollListener(bottomListener, scrollCallBackListener));
        gridView.setOnItemSelectedListener(new GridViewOnItemSelectedListener(bottomListener));
    }

    /** GridView 选中项监听。 */
    private class GridViewOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private OnScrollBottomListener onScrollBottomListener;

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        public GridViewOnItemSelectedListener(OnScrollBottomListener bottomListener) {
            this.onScrollBottomListener = bottomListener;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            OnScrollBottomListener bottomListener;
            if (parent.getLastVisiblePosition() + 1 != parent.getCount() || (bottomListener = this.onScrollBottomListener) == null) {
                return;
            }
            bottomListener.onScorllBootom();
        }
    }

    /** GridView 滚动监听。 */
    private static class GridViewOnScrollListener implements AbsListView.OnScrollListener {
        private OnScrollBottomListener onScrollBottomListener;
        private OnScrollCallBackListener onScrollCallBackListener;

        public GridViewOnScrollListener(OnScrollBottomListener bottomListener) {
            this.onScrollBottomListener = bottomListener;
        }

        public GridViewOnScrollListener(OnScrollBottomListener bottomListener, OnScrollCallBackListener scrollCallBackListener) {
            this.onScrollBottomListener = bottomListener;
            this.onScrollCallBackListener = scrollCallBackListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            OnScrollBottomListener bottomListener;
            if (scrollState == 0 && view.getLastVisiblePosition() + 1 == view.getCount()
                    && (bottomListener = this.onScrollBottomListener) != null) {
                bottomListener.onScorllBootom();
            }
            OnScrollCallBackListener callBackListener = this.onScrollCallBackListener;
            if (callBackListener != null) {
                callBackListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            OnScrollCallBackListener callBackListener = this.onScrollCallBackListener;
            if (callBackListener != null) {
                callBackListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }
}