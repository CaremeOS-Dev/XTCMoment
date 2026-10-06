package com.xtc.ui.widget.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

/** 横向列表的分隔线（末项不绘制）。 */
public class HorizontalDividerDecoration extends DividerDecoration {

    public HorizontalDividerDecoration(Context context) {
        super(context, DividerDecoration.HORIZONTAL_LIST);
    }

    public HorizontalDividerDecoration(Context context, int heightPx) {
        super(context, DividerDecoration.HORIZONTAL_LIST, heightPx);
    }

    public HorizontalDividerDecoration(Context context, Drawable divider) {
        super(context, DividerDecoration.HORIZONTAL_LIST, divider);
    }

    @Override
    public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
        }
    }
}