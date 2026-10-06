package com.xtc.ui.widget.refreshview.interfaces;

import android.widget.AbsListView;

/** AbsListView 滚动回调。 */
public interface OnScrollCallBackListener {
    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

    void onScrollStateChanged(AbsListView view, int scrollState);
}