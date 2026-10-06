package com.xtc.ui.widget.refreshview.difviewhandler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.refreshview.interfaces.LoadMoreHandler;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollBottomListener;
import com.xtc.ui.widget.refreshview.interfaces.OnScrollCallBackListener;
import com.xtc.ui.widget.refreshview.interfaces.PtlmUIHandler;

/** ListView 的加载更多适配实现。 */
public class ListViewHandler implements LoadMoreHandler {
    private static final String TAG = "ListViewHandler";
    private static int mDirection;
    private View mFooter;
    private ListView mListView;

    @Override
    public void refreshDirection(int direction) {
        LogUtil.w(TAG, "-------------------------------------------------- direction = " + direction);
        mDirection = direction;
    }

    @Override
    public boolean handleSetAdapter(View view, PtlmUIHandler.ILoadMoreView loadMoreView, View.OnClickListener onClickListener) {
        final ListView listView = (ListView) view;
        this.mListView = listView;
        if (loadMoreView == null) {
            return false;
        }
        final Context applicationContext = listView.getContext().getApplicationContext();
        loadMoreView.init(new PtlmUIHandler.FootViewAdder() {
            @Override
            public View addFootView(int layoutResId) {
                View footView = LayoutInflater.from(applicationContext).inflate(layoutResId, (ViewGroup) listView, false);
                ListViewHandler.this.mFooter = footView;
                return addFootView(footView);
            }

            @Override
            public View addFootView(View footView) {
                listView.addFooterView(footView);
                return footView;
            }
        }, onClickListener);
        return true;
    }

    @Override
    public void setOnScrollBottomListener(View view, OnScrollBottomListener bottomListener) {
        ListView listView = (ListView) view;
        listView.setOnScrollListener(new ListViewOnScrollListener(bottomListener));
        listView.setOnItemSelectedListener(new ListViewOnItemSelectedListener(bottomListener));
    }

    @Override
    public void setOnScrollBottomCallBackListener(View view, OnScrollBottomListener bottomListener, OnScrollCallBackListener scrollCallBackListener) {
        ListView listView = (ListView) view;
        listView.setOnScrollListener(new ListViewOnScrollListener(bottomListener, scrollCallBackListener));
        listView.setOnItemSelectedListener(new ListViewOnItemSelectedListener(bottomListener));
    }

    @Override
    public void removeFooter() {
        View footer;
        if (this.mListView.getFooterViewsCount() <= 0 || (footer = this.mFooter) == null) {
            return;
        }
        this.mListView.removeFooterView(footer);
    }

    @Override
    public void addFooter() {
        View footer;
        if (this.mListView.getFooterViewsCount() > 0 || (footer = this.mFooter) == null) {
            return;
        }
        this.mListView.addFooterView(footer);
    }

    /** ListView 选中项监听，用于在选中最后一项时触发加载更多。 */
    private class ListViewOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private OnScrollBottomListener onScrollBottomListener;

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        public ListViewOnItemSelectedListener(OnScrollBottomListener bottomListener) {
            this.onScrollBottomListener = bottomListener;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getLastVisiblePosition() + 1 == parent.getCount()) {
                LogUtil.w(ListViewHandler.TAG, "---------------------------------------------------onItemSelected");
                if (this.onScrollBottomListener == null || ListViewHandler.mDirection != 2) {
                    return;
                }
                this.onScrollBottomListener.onScorllBootom();
            }
        }
    }

    /** ListView 滚动监听，滚动停止且到底时触发加载更多。 */
    private static class ListViewOnScrollListener implements AbsListView.OnScrollListener {
        private OnScrollBottomListener onScrollBottomListener;
        private OnScrollCallBackListener onScrollCallBackListener;

        public ListViewOnScrollListener(OnScrollBottomListener bottomListener) {
            this.onScrollBottomListener = bottomListener;
        }

        public ListViewOnScrollListener(OnScrollBottomListener bottomListener, OnScrollCallBackListener scrollCallBackListener) {
            this.onScrollBottomListener = bottomListener;
            this.onScrollCallBackListener = scrollCallBackListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 0 && view.getLastVisiblePosition() + 1 == view.getCount()
                    && this.onScrollBottomListener != null && ListViewHandler.mDirection == 2) {
                this.onScrollBottomListener.onScorllBootom();
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