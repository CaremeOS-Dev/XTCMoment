package com.xtc.ui.widget.ptrrefresh.footer.LoadMoreHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.ptrrefresh.footer.FootViewAdder;
import com.xtc.ui.widget.ptrrefresh.footer.LoadMoreView;

/** ListView 的加载更多辅助实现。 */
public class ListViewLoadMoreHelper extends LoadMoreHelper {
    private static final String TAG = "ListViewLoadMoreHelper";
    private View mFooter;
    private ListView mListView;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getLastVisiblePosition() + 1 == parent.getCount()) {
                ListViewLoadMoreHelper.this.onScrollBottom();
            }
        }
    };
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 0 && view.getLastVisiblePosition() + 1 == view.getCount()) {
                LogUtil.d(ListViewLoadMoreHelper.TAG, "scrollState == SCROLL_STATE_IDLE");
                ListViewLoadMoreHelper.this.onScrollBottom();
            }
            if (ListViewLoadMoreHelper.this.mOnCallbackScrollListener != null) {
                ListViewLoadMoreHelper.this.mOnCallbackScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (ListViewLoadMoreHelper.this.mOnCallbackScrollListener != null) {
                ListViewLoadMoreHelper.this.mOnCallbackScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    };
    protected View.OnClickListener mOnClickLoadMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ListViewLoadMoreHelper.this.OnClickLoadMore();
        }
    };

    @Override
    public void attachToView(View view, LoadMoreView loadMoreView) {
        if (view != null || loadMoreView == null) {
            this.mListView = (ListView) view;
            final Context applicationContext = this.mListView.getContext().getApplicationContext();
            loadMoreView.init(new FootViewAdder() {
                @Override
                public View addFootView(int layoutResId) {
                    ListViewLoadMoreHelper.this.mFooter = LayoutInflater.from(applicationContext)
                            .inflate(layoutResId, (ViewGroup) ListViewLoadMoreHelper.this.mListView, false);
                    return addFootView(ListViewLoadMoreHelper.this.mFooter);
                }

                @Override
                public View addFootView(View footView) {
                    ListViewLoadMoreHelper.this.mListView.addFooterView(footView);
                    return footView;
                }
            }, this.mOnClickLoadMoreListener);
            this.mListView.setOnScrollListener(this.mOnScrollListener);
            this.mListView.setOnItemSelectedListener(this.mOnItemSelectedListener);
            setLoadMoreView(loadMoreView);
            setLoadMoreEnable(true);
        }
    }

    @Override
    public void removeFooter() {
        ListView listView;
        if (this.mFooter == null || (listView = this.mListView) == null || listView.getFooterViewsCount() <= 0) {
            return;
        }
        this.mListView.removeFooterView(this.mFooter);
    }

    @Override
    public void addFooter() {
        ListView listView;
        if (this.mFooter == null || (listView = this.mListView) == null || listView.getFooterViewsCount() > 0) {
            return;
        }
        this.mListView.addFooterView(this.mFooter);
    }
}