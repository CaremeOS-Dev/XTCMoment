package com.xtc.ui.widget.ptrrefresh.footer.LoadMoreHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.ptrrefresh.content.SpecGridView;
import com.xtc.ui.widget.ptrrefresh.footer.FootViewAdder;
import com.xtc.ui.widget.ptrrefresh.footer.LoadMoreView;

/** SpecGridView 的加载更多辅助实现。 */
public class GridViewLoadMoreHelper extends LoadMoreHelper {
    private static final String TAG = "GridViewLoadMoreHelper";
    private View mFooter;
    private SpecGridView mGridView;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getLastVisiblePosition() + 1 == parent.getCount()) {
                GridViewLoadMoreHelper.this.onScrollBottom();
            }
        }
    };
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 0 && view.getLastVisiblePosition() + 1 == view.getCount()) {
                GridViewLoadMoreHelper.this.onScrollBottom();
            }
            if (GridViewLoadMoreHelper.this.mOnCallbackScrollListener != null) {
                GridViewLoadMoreHelper.this.mOnCallbackScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (GridViewLoadMoreHelper.this.mOnCallbackScrollListener != null) {
                GridViewLoadMoreHelper.this.mOnCallbackScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    };
    protected View.OnClickListener mOnClickLoadMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GridViewLoadMoreHelper.this.OnClickLoadMore();
        }
    };

    @Override
    public void attachToView(View view, LoadMoreView loadMoreView) {
        if (view == null && loadMoreView != null) {
            LogUtil.e(TAG, "contentView = null or loadMoreView = null");
            return;
        }
        this.mGridView = (SpecGridView) view;
        ListAdapter adapter = this.mGridView.getAdapter();
        final Context applicationContext = this.mGridView.getContext().getApplicationContext();
        loadMoreView.init(new FootViewAdder() {
            @Override
            public View addFootView(int layoutResId) {
                View footView = LayoutInflater.from(applicationContext)
                        .inflate(layoutResId, (ViewGroup) GridViewLoadMoreHelper.this.mGridView, false);
                GridViewLoadMoreHelper.this.mFooter = footView;
                return addFootView(footView);
            }

            @Override
            public View addFootView(View footView) {
                GridViewLoadMoreHelper.this.mGridView.addFooterView(footView);
                return footView;
            }
        }, this.mOnClickLoadMoreListener);
        if (adapter != null) {
            this.mGridView.setAdapter(adapter);
        }
        this.mGridView.setOnScrollListener(this.mOnScrollListener);
        this.mGridView.setOnItemSelectedListener(this.mOnItemSelectedListener);
        setLoadMoreView(loadMoreView);
        setLoadMoreEnable(true);
    }

    @Override
    public void addFooter() {
        SpecGridView gridView;
        if (this.mFooter == null || (gridView = this.mGridView) == null || gridView.getFooterViewCount() > 0) {
            return;
        }
        this.mGridView.addFooterView(this.mFooter);
    }

    @Override
    public void removeFooter() {
        SpecGridView gridView;
        if (this.mFooter == null || (gridView = this.mGridView) == null || gridView.getFooterViewCount() <= 0) {
            return;
        }
        this.mGridView.removeFooterView(this.mFooter);
    }
}