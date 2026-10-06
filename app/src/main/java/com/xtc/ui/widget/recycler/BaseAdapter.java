package com.xtc.ui.widget.recycler;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/** 带数据列表管理的 RecyclerView 适配器基类。 */
public abstract class BaseAdapter<M, VH extends BaseHolder> extends AbsAdapter<M, VH> {
    private List<M> dataList;

    public abstract int getCustomViewType(int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    public BaseAdapter(Context context) {
        super(context);
        this.dataList = new ArrayList<>();
    }

    public BaseAdapter(Context context, List<M> dataList) {
        super(context);
        this.dataList = new ArrayList<>();
        this.dataList.addAll(dataList);
    }

    public boolean fillList(List<M> dataList) {
        this.dataList.clear();
        boolean result = this.dataList.addAll(dataList);
        notifyDataSetChanged();
        return result;
    }

    public boolean appendItem(M item) {
        boolean result = this.dataList.add(item);
        notifyDataSetChanged();
        return result;
    }

    public boolean appendList(List<M> dataList) {
        boolean result = this.dataList.addAll(dataList);
        notifyDataSetChanged();
        return result;
    }

    public void proposeItem(M item) {
        this.dataList.add(0, item);
        notifyDataSetChanged();
    }

    public void proposeList(List<M> dataList) {
        this.dataList.addAll(0, dataList);
        notifyDataSetChanged();
    }

    @Override
    public final int getItemViewType(int position) {
        if (this.headerView != null && position == 0) {
            return 1024;
        }
        if (this.footerView == null || position != this.dataList.size() + getHeaderViewCount()) {
            return getCustomViewType(position);
        }
        return 1025;
    }

    @Override
    public int getItemCount() {
        return this.dataList.size() + getExtraViewCount();
    }

    public M getItem(int position) {
        List<M> list;
        if ((this.headerView != null && position == 0) || position >= this.dataList.size() + getHeaderViewCount()) {
            return null;
        }
        if (this.headerView == null) {
            list = this.dataList;
        } else {
            list = this.dataList;
            position--;
        }
        return list.get(position);
    }

    public M getItem(VH holder) {
        return getItem(holder.getAdapterPosition());
    }

    public List<M> getAllData() {
        return this.dataList;
    }

    public void updateItem(M item) {
        int index = this.dataList.indexOf(item);
        if (index < 0) {
            return;
        }
        this.dataList.set(index, item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (this.headerView == null) {
            this.dataList.remove(position);
        } else {
            this.dataList.remove(position - 1);
        }
        notifyItemRemoved(position);
    }

    public void removeItem(M item) {
        int index = this.dataList.indexOf(item);
        if (index < 0) {
            return;
        }
        this.dataList.remove(index);
        notifyDataSetChanged();
    }

    public void clear() {
        this.dataList.clear();
        notifyDataSetChanged();
    }
}