package com.xtc.moment.module.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Draws the curved "pull up" handle at the bottom edge of the screen and translates vertical drags
 * near that edge into a dismiss gesture.
 *
 * <p>The painter is not a view itself; the owning view forwards touch events and calls
 * {@link #onDraw(Canvas)} so the curve and arrow are painted on top of the content.
 */
public class EdgePainter {

    private static final String TAG = EdgePainter.class.getSimpleName();

    /** Divisor applied to the screen height to obtain the height of the active edge area. */
    private static final int ACTIVE_AREA_DIVISOR = 8;
    /** Divisor applied to the arrow area to obtain the arrow size. */
    private static final int ARROW_SIZE_DIVISOR = 3;
    /** Alpha of the curve fill colour. */
    private static final int CURVE_COLOR = 0x80000000 | 0x00333333;
    /** Milliseconds the hint animation lasts. */
    private static final long TIP_ANIMATION_DURATION = 3000L;
    /** Milliseconds to wait before the hint animation starts. */
    private static final long TIP_ANIMATION_DELAY = 800L;

    private final View parentView;
    private final GestureDetector gestureDetector;

    private Paint curvePaint;;
    private Path curvePath;;
    private Paint arrowPaint;;
    private Path arrowPath;;

    private int viewWidth;
    private int viewHeight;
    private int activeWidth;
    private int startWidth;
    private int arrowSize;
    private float maxMoveSize;
    private float radius;

    private PointF startP;
    private PointF endP;
    private PointF moveP;

    private float downX;
    private float downY;
    private long downTime;
    private boolean interceptEvent;
    private boolean inEdgeArea;
    private boolean callbackAfterAnimation;

    private GestureListener listener;

    private final ValueAnimator moveAnimator;
    private final ValueAnimator startAnimator;

    /** Called when the user dismisses the page by pulling the edge handle upwards. */
    public interface GestureListener {
        void onScrollUp();
    }

    /** Touch states tracked across the gesture. */
    private final GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {

        private float scroll;
        private float maxScroll;
        private boolean dismissed;

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            resetPoint(e);
            handleDown(e);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            setMovePoint(e2, distanceX, distanceY);
            handleScroll(e1, e2, distanceX, distanceY);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                handleUp(e, true);
            } else if (e.getAction() == MotionEvent.ACTION_CANCEL) {
                handleUp(e, false);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            handleFling(velocityX, velocityY, e2.getRawX(), e2.getRawY());
            return true;
        }

        private void handleDown(MotionEvent event) {
            downX = event.getRawX();
            downY = event.getRawY();
            this.scroll = 0.0f;
            this.maxScroll = 0.0f;
            this.dismissed = false;
            callbackAfterAnimation = false;
        }

        private void handleScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            this.scroll += distanceY;
            if (this.scroll > this.maxScroll) {
                this.maxScroll = this.scroll;
            }
            this.dismissed = this.maxScroll - this.scroll > 10.0f;
        }

        private void handleUp(MotionEvent event, boolean fromUp) {
            float rawY = event.getRawY();
            if (moveP.y == viewHeight) {
                moveP.y = viewHeight - (activeWidth / 1.5f);
            }
            startAnimation(200L, moveP.y, viewHeight);
            if (fromUp && !this.dismissed && Math.abs(rawY - downY) > activeWidth) {
                this.dismissed = true;
                callbackAfterAnimation = true;
            }
        }

        private void handleFling(float velocityX, float velocityY, float rawX, float rawY) {
            float upwardVelocity = -velocityY;
            startAnimation(200L, moveP.y, viewHeight);
            if (this.dismissed || upwardVelocity <= 100.0f || Math.abs(rawY - downY) <= activeWidth) {
                return;
            }
            this.dismissed = true;
            callbackAfterAnimation = true;
        }
    };

    private final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            moveP.y = ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }
    };

    private final Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            resetAfterAnimation();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            resetAfterAnimation();
        }
    };

    private final Animator.AnimatorListener callbackListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (listener != null && callbackAfterAnimation) {
                listener.onScrollUp();
            }
            callbackAfterAnimation = false;
            moveAnimator.removeListener(this);
            resetAfterAnimation();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            moveAnimator.removeListener(this);
            resetAfterAnimation();
        }
    };

    public EdgePainter(Context context, View parentView) {
        this.parentView = parentView;
        initViewParams(context);
        this.gestureDetector = new GestureDetector(context, this.gestureListener);
        initInternalData(this.viewWidth, this.viewHeight);
        this.moveAnimator = new ValueAnimator();
        this.moveAnimator.addUpdateListener(this.updateListener);
        this.moveAnimator.addListener(this.animatorListener);
        this.startAnimator = new ValueAnimator();
        this.startAnimator.addUpdateListener(this.updateListener);
        this.startAnimator.addListener(this.animatorListener);
    }

    /** Plays the looping hint animation that shows the handle moving up and down. */
    public void showTipAnimation() {
        this.startAnimator.setDuration(TIP_ANIMATION_DURATION);
        this.startAnimator.setFloatValues(this.viewHeight, this.startWidth, this.viewHeight,
                this.viewHeight - (this.activeWidth / 2), this.viewHeight);
        this.startAnimator.setStartDelay(TIP_ANIMATION_DELAY);
        this.startAnimator.start();
    }

    public void onDetachedFromWindow() {
        this.startAnimator.cancel();
        this.moveAnimator.cancel();
    }

    public void setGestureListener(GestureListener listener) {
        this.listener = listener;
    }

    public int getActiveWidth() {
        return this.activeWidth;
    }

    private void initViewParams(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point(320, 360);
        windowManager.getDefaultDisplay().getRealSize(size);
        this.viewWidth = size.x;
        this.viewHeight = size.y;
        this.activeWidth = this.viewHeight / ACTIVE_AREA_DIVISOR;
        this.startWidth = this.viewHeight - this.activeWidth;
        this.arrowSize = this.activeWidth / ARROW_SIZE_DIVISOR;
        this.maxMoveSize = this.activeWidth;
    }

    public void onSizeChange(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
    }

    private void invalidate() {
        this.parentView.invalidate();
    }

    private void initInternalData(int width, int height) {
        this.radius = width / 3;
        float centerX = width / 2;
        float bottomY = height;
        this.startP = new PointF(centerX - this.radius, bottomY);
        this.endP = new PointF(this.radius + centerX, bottomY);
        this.moveP = new PointF(centerX, bottomY);
        this.curvePaint = new Paint();
        this.curvePaint.setAntiAlias(true);
        this.curvePaint.setColor(CURVE_COLOR);
        this.curvePaint.setStyle(Paint.Style.FILL);
        this.curvePath = new Path();
        this.arrowPaint = new Paint();
        this.arrowPaint.setAntiAlias(true);
        this.arrowPaint.setStrokeWidth(4.0f);
        this.arrowPaint.setStyle(Paint.Style.STROKE);
        this.arrowPaint.setColor(-1);
        this.arrowPath = new Path();
    }

    private void setMovePoint(MotionEvent event, float distanceX, float distanceY) {
        float newY = this.moveP.y - (distanceY / 4.0f);
        if (newY > this.viewHeight - this.maxMoveSize && newY < this.viewHeight) {
            this.moveP.y = newY;
        }
        invalidate();
    }

    private void resetPoint(MotionEvent event) {
        event.getRawX();
        float centerX = this.viewWidth / 2;
        this.startP.x = centerX - this.radius;
        this.endP.x = this.radius + centerX;
        this.moveP.x = centerX;
        this.moveP.y = this.viewHeight;
    }

    private void startAnimation(long duration, float... values) {
        this.moveAnimator.setRepeatCount(0);
        if (this.moveAnimator.isRunning()) {
            this.moveAnimator.cancel();
        }
        this.moveAnimator.setFloatValues(values);
        this.moveAnimator.setDuration(duration);
        this.moveAnimator.addListener(this.callbackListener);
        this.moveAnimator.start();
    }

    void resetAfterAnimation() {
        this.moveP.y = this.viewHeight;
        invalidate();
    }

    public boolean interceptTouchEvent(MotionEvent event) {
        if (!inEdgeArea(event)) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.gestureDetector.onTouchEvent(event);
            this.interceptEvent = false;
            this.downTime = System.currentTimeMillis();
        } else if (action == MotionEvent.ACTION_MOVE && !this.interceptEvent) {
            float deltaX = event.getRawX() - this.downX;
            float deltaY = event.getRawY() - this.downY;
            float absY = Math.abs(deltaY);
            boolean quickGesture = System.currentTimeMillis() - this.downTime < 250;
            if (absY > Math.abs(deltaX) && absY > this.activeWidth / 2 && quickGesture
                    && !canScroll(this.parentView, false, deltaY, event.getX(), event.getY())) {
                this.interceptEvent = true;
            }
        }
        setInEdgeArea(event);
        return this.interceptEvent;
    }

    protected boolean canScroll(View view, boolean checkScroll, float deltaY, float x, float y) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                View child = group.getChildAt(i);
                float childY = y + scrollY;
                if (childY >= child.getLeft() && childY < child.getRight()) {
                    float childX = x + scrollX;
                    if (childX >= child.getTop() && childX < child.getBottom()
                            && canScroll(child, true, deltaY, childY - child.getLeft(), childX - child.getTop())) {
                        return true;
                    }
                }
            }
        }
        return checkScroll && view.canScrollHorizontally((int) (-deltaY));
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!inEdgeArea(event)) {
            return false;
        }
        boolean handled = this.gestureDetector.onTouchEvent(event);
        this.gestureListener.onLongPress(event);
        setInEdgeArea(event);
        return handled;
    }

    public boolean inEdgeArea(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getRawY() > this.startWidth) {
            if (this.startAnimator.isStarted()) {
                this.startAnimator.cancel();
            }
            this.inEdgeArea = true;
        }
        return this.inEdgeArea;
    }

    private void setInEdgeArea(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            this.inEdgeArea = false;
        }
    }

    public void onDraw(Canvas canvas) {
        if (this.startP == null) {
            return;
        }
        drawCurve(this.curvePath, this.curvePaint, canvas);
        drawArrow(this.arrowPath, this.arrowPaint, canvas);
    }

    private void drawCurve(Path path, Paint paint, Canvas canvas) {
        path.reset();
        path.moveTo(this.startP.x, this.startP.y);
        float controlY = this.viewHeight - ((this.viewHeight - this.moveP.y) / 4.0f);
        float handleOffset = this.radius / 3.0f;
        path.cubicTo(this.moveP.x - (this.radius / 2.0f), controlY, this.moveP.x - handleOffset,
                this.moveP.y, this.moveP.x, this.moveP.y);
        path.cubicTo(this.moveP.x + handleOffset, this.moveP.y, this.moveP.x + (this.radius / 2.0f),
                controlY, this.endP.x, this.endP.y);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawArrow(Path path, Paint paint, Canvas canvas) {
        float unit = this.maxMoveSize / 4.0f;
        path.reset();
        float arrowRange = 3.0f * unit;
        if (this.moveP.y <= this.startWidth + arrowRange) {
            float progress = ((this.viewHeight - this.moveP.y) - unit) / arrowRange;
            float halfHeight = ((double) progress) < 0.5d ? this.arrowSize / 2 : this.arrowSize * progress;
            float centerY = ((this.viewHeight + this.moveP.y) + halfHeight) / 2.0f;
            paint.setAlpha((int) (progress * 255.0f));
            path.moveTo(this.moveP.x - this.arrowSize, centerY);
            path.lineTo(this.moveP.x, centerY - halfHeight);
            path.lineTo(this.moveP.x + this.arrowSize, centerY);
        }
        canvas.drawPath(path, paint);
    }
}