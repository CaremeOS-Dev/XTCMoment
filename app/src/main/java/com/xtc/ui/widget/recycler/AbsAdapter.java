package com.xtc.ui.widget.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/** 支持头部/尾部视图的 RecyclerView 适配器基类。 */
public abstract class AbsAdapter<M, VH extends BaseHolder> extends RecyclerView.Adapter<BaseHolder> {
    private static final String TAG = "AbsAdapter";
    public static final int VIEW_TYPE_FOOTER = 1025;
    public static final int VIEW_TYPE_HEADER = 1024;
    protected Context context;
    protected View footerView;
    protected View headerView;

    public abstract void bindCustomViewHolder(VH holder, int position);

    public abstract VH createCustomViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract long getItemId(int position);

    public AbsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public final BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1024) {
            return new BaseHolder(this.headerView);
        }
        if (viewType == 1025) {
            return new BaseHolder(this.footerView);
        }
        return createCustomViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(BaseHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 1024 || itemViewType == 1025) {
            return;
        }
        bindCustomViewHolder((VH) holder, position);
    }

    public void addHeaderView(View view) {
        if (view == null) {
            Log.w(TAG, "add the header view is null");
        } else {
            this.headerView = view;
            notifyDataSetChanged();
        }
    }

    public void removeHeaderView() {
        if (this.headerView != null) {
            this.headerView = null;
            notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        if (view == null) {
            Log.w(TAG, "add the footer view is null");
        } else {
            this.footerView = view;
            notifyDataSetChanged();
        }
    }

    public void removeFooterView() {
        if (this.footerView != null) {
            this.footerView = null;
            notifyDataSetChanged();
        }
    }

    public int getExtraViewCount() {
        int count = this.headerView != null ? 1 : 0;
        return this.footerView != null ? count + 1 : count;
    }

    public int getHeaderViewCount() {
        return this.headerView == null ? 0 : 1;
    }

    public int getFooterViewCount() {
        return this.footerView == null ? 0 : 1;
    }
}