package com.xtc.ui.widget.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.xtc.log.Log;

/** 滑动相关计算工具：测量 ListView/RecyclerView 的内容高度与滚动距离。 */
public class SlideCalculationUtil {
    private static final String TAG = SlideCalculationUtil.class.getSimpleName();

    public static void setListViewHeight(ListView listView) {
        int itemsHeight = getListViewItemsHeight(listView);
        int dividerHeight = getDividesHeight(listView);
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = itemsHeight + dividerHeight;
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }

    public static int getListViewItemsHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int index = 0; index < adapter.getCount(); index++) {
            View itemView = adapter.getView(index, null, listView);
            itemView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            totalHeight += itemView.getMeasuredHeight();
        }
        return totalHeight;
    }

    public static int getListViewItemHeight(ListView listView) {
        return getListViewItemHeight(listView, 0);
    }

    public static int getListViewItemHeight(ListView listView, int position) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null || position >= adapter.getCount()) {
            return 0;
        }
        View itemView = adapter.getView(position, null, listView);
        itemView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        return itemView.getMeasuredHeight();
    }

    public static int getDividesHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return 0;
        }
        return listView.getDividerHeight() * (adapter.getCount() - 1);
    }

    public static int getListViewScrollY(ListView listView) {
        View firstChild = listView.getChildAt(0);
        if (firstChild == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        return (firstVisiblePosition * firstChild.getHeight()) - firstChild.getTop();
    }

    public static int getListViewVisibleHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastVisiblePosition == -1) {
            Log.d(TAG, "ListView has not been loaded ,Please use View.Post(new Runnable)  method");
            return 0;
        }
        int totalHeight = 0;
        for (int position = firstVisiblePosition; position <= lastVisiblePosition; position++) {
            View itemView = adapter.getView(position, null, listView);
            itemView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            totalHeight += itemView.getHeight();
        }
        return totalHeight + (listView.getDividerHeight() * (lastVisiblePosition - firstVisiblePosition));
    }

    public static int getListViewContainerHeight(ListView listView) {
        return listView.getHeight();
    }

    public int getRecyclerViewScrollY(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return 0;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        View firstVisibleView = linearLayoutManager.findViewByPosition(firstVisiblePosition);
        return (firstVisiblePosition * firstVisibleView.getHeight()) - firstVisibleView.getTop();
    }

    public int getRecyclerViewVisibleHeight(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return 0;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
        int totalHeight = 0;
        for (int position = linearLayoutManager.findFirstVisibleItemPosition(); position <= lastVisiblePosition; position++) {
            View itemView = linearLayoutManager.findViewByPosition(position);
            itemView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            totalHeight += itemView.getHeight();
        }
        return totalHeight;
    }

    public int getRecyclerViewContarinerHeight(RecyclerView recyclerView) {
        return recyclerView.getHeight();
    }
}