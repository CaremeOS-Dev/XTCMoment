package com.xtc.ui.widget.ptrrefresh.footer.LoadMoreHelper;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xtc.ui.widget.ptrrefresh.adpter.ClassicRecyclerAdapter;
import com.xtc.ui.widget.ptrrefresh.footer.FootViewAdder;
import com.xtc.ui.widget.ptrrefresh.footer.LoadMoreView;

/** RecyclerView 的加载更多辅助实现。 */
public class RecyclerLoadMoreHelper extends LoadMoreHelper {
    private View mFooter;
    private ClassicRecyclerAdapter mRecyclerAdapter;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0 && isScrollBottom(recyclerView)) {
                RecyclerLoadMoreHelper.this.onScrollBottom();
            }
        }

        private boolean isScrollBottom(RecyclerView recyclerView) {
            return !isCanScrollVertically(recyclerView);
        }

        private boolean isCanScrollVertically(RecyclerView recyclerView) {
            if (Build.VERSION.SDK_INT < 14) {
                return ViewCompat.canScrollVertically(recyclerView, 1) || recyclerView.getScrollY() < recyclerView.getHeight();
            }
            return ViewCompat.canScrollVertically(recyclerView, 1);
        }
    };
    protected View.OnClickListener mOnClickLoadMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerLoadMoreHelper.this.OnClickLoadMore();
        }
    };

    @Override
    public void attachToView(View view, LoadMoreView loadMoreView) {
        if (view != null || loadMoreView == null) {
            final RecyclerView recyclerView = (RecyclerView) view;
            this.mRecyclerAdapter = (ClassicRecyclerAdapter) recyclerView.getAdapter();
            final Context applicationContext = recyclerView.getContext().getApplicationContext();
            loadMoreView.init(new FootViewAdder() {
                @Override
                public View addFootView(int layoutResId) {
                    RecyclerLoadMoreHelper.this.mFooter = LayoutInflater.from(applicationContext)
                            .inflate(layoutResId, (ViewGroup) recyclerView, false);
                    return addFootView(RecyclerLoadMoreHelper.this.mFooter);
                }

                @Override
                public View addFootView(View footView) {
                    RecyclerLoadMoreHelper.this.mRecyclerAdapter.addFooter(footView);
                    return footView;
                }
            }, this.mOnClickLoadMoreListener);
            recyclerView.addOnScrollListener(this.mOnScrollListener);
            setLoadMoreView(loadMoreView);
            setLoadMoreEnable(true);
        }
    }

    @Override
    public void addFooter() {
        ClassicRecyclerAdapter adapter;
        if (this.mFooter == null || (adapter = this.mRecyclerAdapter) == null || adapter.getFootSize() > 0) {
            return;
        }
        this.mRecyclerAdapter.addFooter(this.mFooter);
    }

    @Override
    public void removeFooter() {
        ClassicRecyclerAdapter adapter;
        if (this.mFooter == null || (adapter = this.mRecyclerAdapter) == null || adapter.getFootSize() <= 0) {
            return;
        }
        this.mRecyclerAdapter.removeFooter(this.mFooter);
    }
}