package com.xtc.moment.module.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import com.xtc.log.LogUtil;

/**
 * Nested scroll view used inside {@link ScrollLayout}.
 *
 * <p>Besides forwarding scroll callbacks it detects when the user starts dragging downwards from
 * the very top of the list and notifies the {@link OnScrollStatusListener} so that the parent
 * drawer can take over the gesture.
 */
public class ContentScrollView extends NestedScrollView {

    private static final String TAG = "ContentScrollView";

    private static final int CODE_SCROLL_LISTENER = 1;
    /** Delay after the last scroll change before the stop callback is delivered. */
    private static final long DELAY_TIME = 400L;
    /** Minimum vertical travel from the top edge that triggers the "pull down" callback. */
    private static final float PULL_DOWN_SLOP = 30.0f;

    private final Handler scrollHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == CODE_SCROLL_LISTENER && onScrollStatusListener != null) {
                onScrollStatusListener.onScrollStop();
            }
        }
    };

    private OnScrollChangedListener listener;
    private OnScrollStatusListener onScrollStatusListener;
    private boolean isScrollEnable = true;
    private boolean isTouching;
    private float startY = -1.0f;

    /** Receives the raw scroll position changes. */
    public interface OnScrollChangedListener {
        void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    /** Receives the high level scroll state of the view. */
    public interface OnScrollStatusListener {
        void onFromTopScrollDown();

        void onScrollStop();

        void onScrolling();
    }

    public ContentScrollView(Context context) {
        super(context);
    }

    public ContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollChangeListener(OnScrollChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        OnScrollChangedListener changedListener = this.listener;
        if (changedListener != null) {
            changedListener.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        }
        OnScrollStatusListener statusListener = this.onScrollStatusListener;
        if (statusListener != null) {
            statusListener.onScrolling();
            if (this.isTouching) {
                return;
            }
            stopDelay();
        }
    }

    private void stopDelay() {
        this.scrollHandler.removeCallbacksAndMessages(null);
        this.scrollHandler.sendEmptyMessageDelayed(CODE_SCROLL_LISTENER, DELAY_TIME);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof ScrollLayout) {
                ((ScrollLayout) parent).setAssociatedScrollView(this);
                return;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.startY = getScrollY() == 0 ? event.getY() : -1.0f;
            this.isTouching = true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if ((parent instanceof ScrollLayout) && ((ScrollLayout) parent).getCurrentStatus() == ScrollLayout.Status.OPENED) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            LogUtil.d(TAG, "onTouchEvent() called with: getScrollY = [" + getScrollY() + "]");
            this.startY = getScrollY() == 0 ? event.getY() : -1.0f;
            this.isTouching = true;
        } else if (action == MotionEvent.ACTION_UP) {
            this.isTouching = false;
            stopDelay();
            if (getScrollY() == 0 && this.startY != -1.0f) {
                float offset = Math.abs(event.getY() - this.startY);
                LogUtil.d(TAG, "onTouchEvent() called with: scrollY = [" + offset + "]");
                if (offset > PULL_DOWN_SLOP && this.onScrollStatusListener != null) {
                    this.onScrollStatusListener.onFromTopScrollDown();
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (getScrollY() != 0) {
                this.startY = -1.0f;
            }
            this.isTouching = true;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            this.isTouching = false;
            stopDelay();
            if (getScrollY() == 0) {
                float offset = Math.abs(event.getY() - this.startY);
                LogUtil.d(TAG, "onTouchEvent() called with: scrollY = [" + offset + "]");
                if (offset > PULL_DOWN_SLOP) {
                    this.onScrollStatusListener.onFromTopScrollDown();
                }
            }
        }
        if (this.isScrollEnable) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public boolean isScrollEnable() {
        return this.isScrollEnable;
    }

    public void setScrollEnable(boolean scrollEnable) {
        this.isScrollEnable = scrollEnable;
    }

    public void setOnScrollStatusListener(OnScrollStatusListener listener) {
        this.onScrollStatusListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.scrollHandler.removeCallbacksAndMessages(null);
    }
}