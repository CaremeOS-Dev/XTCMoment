package com.xtc.ui.widget.slideitemlistview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Scroller;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.slideitemlistview.interfaces.RemoveListener;

/** 支持左滑删除条目的 ListView。 */
public class SlideItemListView extends ListView {
    private static final int SNAP_VELOCITY = 600;
    private static final String TAG = "SlideItemListView";
    private int downX;
    private int downY;
    private boolean isLeftScroll;
    private boolean isSlide;
    private View itemView;
    private RemoveListener removeListener;
    private boolean result;
    private int screenWidth;
    private Scroller scroller;
    private int slidePosition;
    private String tag;
    private int touchSlop;
    private VelocityTracker velocityTracker;

    public SlideItemListView(Context context) {
        this(context, null);
    }

    public SlideItemListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideItemListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isSlide = false;
        this.isLeftScroll = false;
        this.result = false;
        this.tag = null;
        this.screenWidth = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
        this.scroller = new Scroller(context);
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            addVelocityTracker(event);
            if (!this.scroller.isFinished()) {
                return super.dispatchTouchEvent(event);
            }
            this.downX = (int) event.getX();
            this.downY = (int) event.getY();
            this.slidePosition = pointToPosition(this.downX, this.downY);
            if (this.slidePosition == -1) {
                return super.dispatchTouchEvent(event);
            }
        } else if (action == 1) {
            recycleVelocityTracker();
        } else if (action == 2) {
            judgeIsSlide(event);
        } else if (action == 3) {
            LogUtil.d(TAG, "MotionEvent.ACTION_CANCEL");
        }
        return super.dispatchTouchEvent(event);
    }

    private void judgeIsSlide(MotionEvent event) {
        this.itemView = getChildAt(this.slidePosition - getFirstVisiblePosition());
        View child = this.itemView;
        if (child != null && (child.getTag() instanceof String)) {
            this.tag = (String) this.itemView.getTag();
            String currentTag = this.tag;
            if (currentTag != null && currentTag.equals("headerView") && this.tag.equals("headerView")) {
                return;
            }
        }
        if (Math.abs(getScrollVelocity()) > 600
                || (this.downX - event.getX() > this.touchSlop && Math.abs(event.getY() - this.downY) < this.touchSlop)) {
            this.isSlide = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isSlide && this.slidePosition != -1) {
            addVelocityTracker(event);
            int action = event.getAction();
            int x = (int) event.getX();
            if (action == 1) {
                int velocity = getScrollVelocity();
                if (velocity > 600) {
                    scrollRight();
                } else if (velocity < -600) {
                    scrollLeft();
                } else {
                    scrollByDistanceX();
                }
                recycleVelocityTracker();
                this.isSlide = false;
            } else if (action == 2) {
                int deltaX = this.downX - x;
                this.downX = x;
                if (Math.abs(deltaX) > this.touchSlop) {
                    this.result = true;
                    event.setAction(3);
                }
                this.itemView.scrollBy(deltaX, 0);
            }
            LogUtil.d(TAG, "result-->>" + this.result);
        }
        return super.onTouchEvent(event);
    }

    private void scrollRight() {
        this.isLeftScroll = false;
        int scrollX = this.screenWidth + this.itemView.getScrollX();
        this.scroller.startScroll(this.itemView.getScrollX(), 0, -scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    private void scrollLeft() {
        this.isLeftScroll = true;
        int scrollX = this.screenWidth - this.itemView.getScrollX();
        this.scroller.startScroll(this.itemView.getScrollX(), 0, scrollX, 0, Math.abs(scrollX));
        postInvalidate();
    }

    private void scrollByDistanceX() {
        if (this.itemView.getScrollX() >= this.screenWidth / 3) {
            scrollLeft();
        } else if (this.itemView.getScrollX() <= (-this.screenWidth) / 3) {
            scrollRight();
        } else {
            this.itemView.scrollTo(0, 0);
        }
    }

    @Override
    public void computeScroll() {
        if (this.scroller.computeScrollOffset()) {
            this.itemView.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            postInvalidate();
            if (this.scroller.isFinished()) {
                RemoveListener listener = this.removeListener;
                if (listener != null && this.isLeftScroll) {
                    listener.removeItem(this.slidePosition);
                }
                this.isLeftScroll = false;
                this.itemView.scrollTo(0, 0);
            }
        }
    }

    private void addVelocityTracker(MotionEvent event) {
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        VelocityTracker tracker = this.velocityTracker;
        if (tracker != null) {
            tracker.recycle();
            this.velocityTracker = null;
        }
    }

    private int getScrollVelocity() {
        this.velocityTracker.computeCurrentVelocity(1000);
        return (int) this.velocityTracker.getXVelocity();
    }
}