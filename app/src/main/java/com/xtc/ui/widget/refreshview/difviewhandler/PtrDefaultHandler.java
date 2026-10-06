package com.xtc.ui.widget.refreshview.difviewhandler;

import android.os.Build;
import android.view.View;
import android.widget.AbsListView;

import com.xtc.ui.widget.refreshview.PtrFrameLayout;
import com.xtc.ui.widget.refreshview.interfaces.PtrHandler;

/** 默认下拉刷新判定。 */
public abstract class PtrDefaultHandler implements PtrHandler {

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frameLayout, View contentView, View headerView) {
        return checkContentCanBePulledDown(frameLayout, contentView, headerView);
    }

    private boolean checkContentCanBePulledDown(PtrFrameLayout frameLayout, View contentView, View headerView) {
        return !canChildScrollUp(contentView);
    }

    private boolean canChildScrollUp(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!(view instanceof AbsListView)) {
                return view.getScrollY() > 0;
            }
            AbsListView listView = (AbsListView) view;
            return listView.getChildCount() > 0
                    && (listView.getFirstVisiblePosition() > 0
                    || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
        return view.canScrollVertically(-1);
    }
}