package com.xtc.ui.widget.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

/** 纵向列表的分隔线（末项不绘制）。 */
public class VerticalDividerDecoration extends DividerDecoration {

    public VerticalDividerDecoration(Context context) {
        super(context, DividerDecoration.VERTICAL_LIST);
    }

    public VerticalDividerDecoration(Context context, int widthPx) {
        super(context, DividerDecoration.VERTICAL_LIST, widthPx);
    }

    public VerticalDividerDecoration(Context context, Drawable divider) {
        super(context, DividerDecoration.VERTICAL_LIST, divider);
    }

    @Override
    public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, this.mDivider.getIntrinsicHeight(), 0);
        }
    }
}