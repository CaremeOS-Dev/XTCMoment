package com.xtc.moment.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.xtc.log.LogUtil;

/**
 * Frame layout used by the friend list page.
 *
 * <p>The layout intercepts every touch event so that a horizontal swipe that started inside a
 * {@link ScrollLayout} keeps being dispatched to it. A tap (movement below the touch slop
 * threshold) is forwarded to the registered click listener instead.
 */
public class FriendFrameLayout extends FrameLayout {

    private static final String TAG = "FriendFrameLayout";

    /** Maximum distance in pixels a finger may travel and still count as a click. */
    private static final float CLICK_SLOP = 10.0f;

    private View.OnClickListener clickListener;
    private ScrollLayout scrollLayout;
    private float touchDownX;
    private float touchDownY;
    private int scrollStart;
    private int scrollEnd;
    private int screenHeight;

    public FriendFrameLayout(Context context) {
        super(context);
        initView(context);
    }

    public FriendFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FriendFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setClickable(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                dispatchToScrollLayout(event);
                break;
            case MotionEvent.ACTION_UP:
                if (clickListener != null) {
                    float upX = event.getX();
                    float upY = event.getY();
                    LogUtil.d(TAG, "a = " + Math.abs(upX - touchDownX) + ", b = " + Math.abs(touchDownY - upY));
                    if (Math.abs(upX - touchDownX) < CLICK_SLOP && Math.abs(touchDownY - upY) < CLICK_SLOP) {
                        clickListener.onClick(this);
                    }
                }
                dispatchToScrollLayout(event);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                dispatchToScrollLayout(event);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /** Forwards the event to the scroll layout while it is not in its closed state. */
    private void dispatchToScrollLayout(MotionEvent event) {
        ScrollLayout layout = this.scrollLayout;
        if (layout == null || layout.getCurrentStatus() == ScrollLayout.Status.CLOSED) {
            return;
        }
        this.scrollLayout.move(event);
    }

    public void abortAnimation() {
        ScrollLayout layout = this.scrollLayout;
        if (layout == null || layout.getScroller().isFinished()) {
            return;
        }
        this.scrollLayout.getScroller().abortAnimation();
    }

    public void startScroll(int startX, int startY, int dx, int dy) {
        ScrollLayout layout = this.scrollLayout;
        if (layout != null) {
            layout.startScroll(startX, startY, dx, dy);
        }
    }

    /**
     * Computes the distance that still has to be scrolled to align the current offset with a
     * screen boundary, depending on the scroll direction.
     */
    private int checkAlignment() {
        this.scrollEnd = getScrollY();
        boolean scrollingDown = this.scrollEnd - this.scrollStart > 0;
        int remainder = this.scrollEnd % this.screenHeight;
        return scrollingDown ? remainder : -(this.screenHeight - remainder);
    }

    public void setScrollLayout(ScrollLayout scrollLayout) {
        this.scrollLayout = scrollLayout;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        this.clickListener = listener;
    }
}