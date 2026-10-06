package com.xtc.ui.widget.customRecyclerView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.xtc.log.LogUtil;

/** 测量总高度的线性布局管理器，用于修正滚动条长度。 */
public class FixScrBarLenLinearLayoutManager extends LinearLayoutManager {
    private static final String TAG = "FixScrBarLenLinearLayoutManager";
    private int[] tempDimension;
    private int totalHeight;

    public FixScrBarLenLinearLayoutManager(Context context) {
        super(context);
        this.tempDimension = new int[2];
        this.totalHeight = 0;
    }

    public void setTotalHeight(int totalHeight) {
        LogUtil.i(TAG, "RecyclerView totalHeight = " + totalHeight);
        this.totalHeight = totalHeight;
    }

    public int getTotalHeight() {
        return this.totalHeight;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int itemCount = getItemCount();
        int measuredTotalHeight = 0;
        for (int position = 0; position < itemCount; position++) {
            measureScrapingChild(recycler, position, itemCount,
                    View.MeasureSpec.makeMeasureSpec(position, 0), View.MeasureSpec.makeMeasureSpec(position, 0),
                    this.tempDimension);
            measuredTotalHeight += this.tempDimension[1];
        }
        setTotalHeight(measuredTotalHeight);
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    private void measureScrapingChild(RecyclerView.Recycler recycler, int position, int itemCount,
                                      int widthSpec, int heightSpec, int[] outDimension) {
        if (position >= itemCount) {
            LogUtil.e(TAG, "子view 下标越界 { " + position + " >= " + itemCount + " }");
            return;
        }
        try {
            View itemView = recycler.getViewForPosition(position);
            if (itemView != null) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                itemView.measure(ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), layoutParams.width),
                        ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height));
                outDimension[0] = itemView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                outDimension[1] = itemView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                recycler.recycleView(itemView);
            }
        } catch (Exception unused) {
        }
    }
}