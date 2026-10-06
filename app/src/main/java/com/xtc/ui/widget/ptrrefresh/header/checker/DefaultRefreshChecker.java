package com.xtc.ui.widget.ptrrefresh.header.checker;

import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;

import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;

/** 默认刷新判定：内容未滚动到顶部时允许下拉刷新。 */
public abstract class DefaultRefreshChecker implements RefreshChecker {

    public static boolean canChildScrollUp(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!(view instanceof AbsListView)) {
                return ViewCompat.canScrollVertically(view, -1) || view.getScrollY() > 0;
            }
            AbsListView listView = (AbsListView) view;
            return listView.getChildCount() > 0
                    && (listView.getFirstVisiblePosition() > 0
                    || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
        return view.canScrollVertically(-1);
    }

    public static boolean checkContentCanBePulledDown(BaseFrameLayout frameLayout, View contentView,
            View headerView) {
        return !canChildScrollUp(contentView);
    }

    @Override
    public boolean checkCanDoRefresh(BaseFrameLayout frameLayout, View contentView, View headerView) {
        return checkContentCanBePulledDown(frameLayout, contentView, headerView);
    }
}