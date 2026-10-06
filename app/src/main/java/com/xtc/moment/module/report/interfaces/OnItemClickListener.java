package com.xtc.moment.module.report.interfaces;

/**
 * 通用列表项点击回调。
 */
public interface OnItemClickListener<T> {
    void onItemClick(T item, int position);
}