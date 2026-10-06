package com.xtc.ui.widget.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** 带子视图缓存的 RecyclerView.ViewHolder 基类。 */
public class BaseHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewArray;

    public BaseHolder(ViewGroup parent, int layoutResId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
        this.viewArray = new SparseArray<>();
    }

    public BaseHolder(View itemView) {
        super(itemView);
        this.viewArray = new SparseArray<>();
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T getView(int viewId) {
        T view = (T) this.viewArray.get(viewId);
        if (view != null) {
            return view;
        }
        T found = (T) this.itemView.findViewById(viewId);
        this.viewArray.put(viewId, found);
        return found;
    }

    protected Context getContext() {
        return this.itemView.getContext();
    }
}