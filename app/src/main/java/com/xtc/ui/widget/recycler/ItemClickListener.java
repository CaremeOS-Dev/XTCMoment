package com.xtc.ui.widget.recycler;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/** 基于手势检测的 RecyclerView 条目点击/长按监听。 */
public class ItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private OnItemClickListener clickListener;
    private GestureDetectorCompat gestureDetector;

    /** 条目点击回调。 */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public ItemClickListener(final RecyclerView recyclerView, OnItemClickListener listener) {
        this.clickListener = listener;
        this.gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                View childView = recyclerView.findChildViewUnder(event.getX(), event.getY());
                if (childView == null || ItemClickListener.this.clickListener == null
                        || !ItemClickListener.this.gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                ItemClickListener.this.clickListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                return true;
            }

            @Override
            public void onLongPress(MotionEvent event) {
                View childView = recyclerView.findChildViewUnder(event.getX(), event.getY());
                if (childView == null || ItemClickListener.this.clickListener == null) {
                    return;
                }
                ItemClickListener.this.clickListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return false;
    }
}