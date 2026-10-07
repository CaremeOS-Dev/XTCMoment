package com.xtc.ui.widget.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.xtc.moment.R;

/** RecyclerView 分割线装饰。 */
public class DividerDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST = 0;
    public static final int VERTICAL_LIST = 1;
    Drawable mDivider;
    int mOrientation;

    public DividerDecoration(Context context, int orientation) {
        this.mDivider = context.getResources().getDrawable(R.drawable.base_divider);
        this.mOrientation = orientation;
    }

    public DividerDecoration(Context context, int orientation, int dividerResId) {
        this.mDivider = context.getResources().getDrawable(dividerResId);
        if (this.mDivider == null) {
            this.mDivider = context.getResources().getDrawable(R.drawable.base_divider);
        }
        this.mOrientation = orientation;
    }

    public DividerDecoration(Context context, int orientation, Drawable divider) {
        this.mDivider = divider == null ? context.getResources().getDrawable(R.drawable.base_divider) : divider;
        this.mOrientation = orientation;
    }

    public void setDivider(Drawable divider) {
        if (divider != null) {
            this.mDivider = divider;
        }
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent) {
        if (this.mOrientation == 1) {
            drawVertical(canvas, parent);
        } else {
            drawHorizontal(canvas, parent);
        }
    }

    public void drawVertical(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = parent.getChildAt(index);
            int bottom = child.getBottom() + ((RecyclerView.LayoutParams) child.getLayoutParams()).bottomMargin
                    + Math.round(ViewCompat.getTranslationY(child));
            this.mDivider.setBounds(left, bottom, right, this.mDivider.getIntrinsicHeight() + bottom);
            this.mDivider.draw(canvas);
        }
    }

    public void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = parent.getChildAt(index);
            int right = child.getRight() + ((RecyclerView.LayoutParams) child.getLayoutParams()).rightMargin
                    + Math.round(ViewCompat.getTranslationX(child));
            this.mDivider.setBounds(right, top, this.mDivider.getIntrinsicHeight() + right, bottom);
            this.mDivider.draw(canvas);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (this.mOrientation == 1) {
            outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
        }
    }
}