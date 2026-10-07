package com.xtc.ui.widget.customRecyclerView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/** 固定长度滚动条的 RecyclerView：滚动条长度按内容总高换算。 */
public class FixScrBarLenRecyclerView extends RecyclerView {
    private static final int DEFAULT_STAY_DUARATION = 300;
    private static final String TAG = "FixScrBarLenRecyclerView";
    private ValueAnimator animator;
    private boolean autoShowThisBar;
    private int barTopMargin;
    private int barTotalHeight;
    private int bgColor;
    private int bgNormalAlpha;
    private Paint bgPaint;
    private float bgRadius;
    private RectF bgRect;
    private float bgWidth;
    private float defaultBgEndX;
    private float defaultBgEndY;
    private float defaultBgStartX;
    private float defaultBgStartY;
    private float defaultThumbEndX;
    private float defaultThumbEndY;
    private float defaultThumbStartX;
    private float defaultThumbStartY;
    private Point leftTopPoint;
    private float multiple;
    private int screenHeight;
    private int screenOffset;
    private int screenWidth;
    private int stayDuration;
    private int thumbColor;
    private int thumbNormalAlpha;
    private Paint thumbPaint;
    private float thumbRadius;
    private RectF thumbRect;
    private float thumbWidth;
    private int viewTotalHeight;

    public void hideScrollBar() {
        if (this.animator.isRunning()) {
            this.animator.cancel();
        } else {
            resetPaints();
        }
    }

    @Override
    @Deprecated
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        LogUtil.e(TAG, "Please use another setLayoutManager ( fixScrBarLenLinearLayoutManager ) !");
    }

    public void setLayoutManager(FixScrBarLenLinearLayoutManager layoutManager) {
        super.setLayoutManager((RecyclerView.LayoutManager) layoutManager);
    }

    @Override
    @Deprecated
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        LogUtil.e(TAG, "If you want to hide ScrollBar , Please use hideScrollBar() !");
    }

    public FixScrBarLenRecyclerView(Context context) {
        this(context, null);
    }

    public FixScrBarLenRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.autoShowThisBar = false;
        initRes(context, attrs);
        initOther();
        super.setVerticalScrollBarEnabled(false);
    }

    private void initRes(Context context, AttributeSet attrs) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;
        int defaultTopMargin = (int) ((displayMetrics.density * 6.0f) + 0.5f);
        int defaultBarLength = this.screenHeight / 6;
        int defaultThumbPadding = (int) ((displayMetrics.density * 2.0f) + 0.5f);
        int defaultThumbWidth = (int) ((displayMetrics.density * 5.0f) + 0.5f);
        int defaultBgWidth = (int) ((displayMetrics.density * 6.0f) + 0.5f);
        int defaultThumbColor = Color.parseColor("#dadada");
        int defaultBgColor = Color.parseColor("#40ffffff");
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FixScrBarLenRecyclerView, 0, 0);
        this.thumbColor = attributes.getColor(R.styleable.FixScrBarLenRecyclerView_fsblrv_thumb_color, defaultThumbColor);
        this.bgColor = attributes.getColor(R.styleable.FixScrBarLenRecyclerView_fsblrv_bg_color, defaultBgColor);
        this.thumbWidth = attributes.getDimension(R.styleable.FixScrBarLenRecyclerView_fsblrv_thumb_width, defaultThumbWidth);
        this.bgWidth = attributes.getDimension(R.styleable.FixScrBarLenRecyclerView_fsblrv_bg_width, defaultBgWidth);
        this.barTotalHeight = (int) attributes.getDimension(R.styleable.FixScrBarLenRecyclerView_fsblrv_bg_length, defaultBarLength);
        this.barTopMargin = (int) attributes.getDimension(R.styleable.FixScrBarLenRecyclerView_fsblrv_bg_top_margin, defaultTopMargin);
        this.stayDuration = attributes.getInteger(R.styleable.FixScrBarLenRecyclerView_fsblrv_stay_duration, 300);
        attributes.recycle();
        float thumbWidth = this.thumbWidth;
        this.thumbRadius = thumbWidth / 2.0f;
        float bgWidth = this.bgWidth;
        this.bgRadius = bgWidth / 2.0f;
        int screenWidth = this.screenWidth;
        this.defaultBgEndX = screenWidth;
        int barTopMargin = this.barTopMargin;
        this.defaultBgEndY = this.barTotalHeight + barTopMargin;
        this.defaultBgStartX = this.defaultBgEndX - bgWidth;
        this.defaultBgStartY = barTopMargin;
        this.defaultThumbEndX = screenWidth - ((bgWidth - thumbWidth) / 2.0f);
        float thumbPadding = defaultThumbPadding;
        this.defaultThumbEndY = this.defaultBgEndY - thumbPadding;
        this.defaultThumbStartX = this.defaultThumbEndX - thumbWidth;
        this.defaultThumbStartY = this.defaultBgStartY + thumbPadding;
        this.leftTopPoint = new Point(this.defaultThumbStartX, this.defaultThumbStartY);
        this.thumbRect = new RectF();
        this.bgRect = new RectF(this.defaultBgStartX, this.defaultBgStartY, this.defaultBgEndX, this.defaultBgEndY);
    }

    private void initOther() {
        this.bgPaint = new Paint(1);
        this.bgPaint.setColor(this.bgColor);
        this.bgPaint.setStyle(Paint.Style.FILL);
        this.bgNormalAlpha = this.bgPaint.getAlpha();
        this.thumbPaint = new Paint(1);
        this.thumbPaint.setColor(this.thumbColor);
        this.thumbPaint.setStyle(Paint.Style.FILL);
        this.thumbNormalAlpha = this.thumbPaint.getAlpha();
        this.animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator.setDuration(this.stayDuration);
        this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float remainingFraction = 1.0f - animation.getAnimatedFraction();
                FixScrBarLenRecyclerView.this.bgPaint.setAlpha((int) (FixScrBarLenRecyclerView.this.bgNormalAlpha * remainingFraction));
                FixScrBarLenRecyclerView.this.thumbPaint.setAlpha((int) (FixScrBarLenRecyclerView.this.thumbNormalAlpha * remainingFraction));
                FixScrBarLenRecyclerView.this.invalidate();
            }
        });
        this.animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FixScrBarLenRecyclerView.this.resetPaints();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                FixScrBarLenRecyclerView.this.resetPaints();
            }
        });
    }

    private void resetPaints() {
        this.bgPaint.setAlpha(this.bgNormalAlpha);
        this.thumbPaint.setAlpha(this.thumbNormalAlpha);
        this.autoShowThisBar = false;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        FixScrBarLenLinearLayoutManager layoutManager = (FixScrBarLenLinearLayoutManager) getLayoutManager();
        if (layoutManager != null) {
            this.viewTotalHeight = layoutManager.getTotalHeight();
            this.multiple = this.viewTotalHeight / (this.defaultThumbEndY - this.defaultThumbStartY);
        }
    }

    private int getThumbHeight() {
        return (int) (this.screenHeight / this.multiple);
    }

    private int getThumbMaxY() {
        return (int) (this.defaultThumbEndY - getThumbHeight());
    }

    private int getScreenMaxOffset() {
        return Math.max(0, this.viewTotalHeight - this.screenHeight);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        RecyclerView.Adapter adapter;
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && (adapter = getAdapter()) != null && adapter.getItemCount() != 0 && this.viewTotalHeight == 0) {
            LogUtil.i(TAG, "onWindowFocusChanged 且 viewTotalHeight = 0 ---> requestLayout()");
            requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 2) {
            checkScrollToEdge();
        }
        return super.onTouchEvent(event);
    }

    private void checkScrollToEdge() {
        if (scrollToTopEdge()) {
            showBar();
            this.screenOffset = 0;
            this.leftTopPoint.y = this.defaultThumbStartY;
            invalidate();
            return;
        }
        if (scrollToBottomEdge()) {
            showBar();
            this.screenOffset = getScreenMaxOffset();
            this.leftTopPoint.y = getThumbMaxY();
            invalidate();
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
        hideScrollBar();
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (dy == 0) {
            return;
        }
        showBar();
        computeThumbPointY(dy);
        invalidate();
    }

    private void computeThumbPointY(int dy) {
        this.screenOffset += dy;
        if (scrollToTopEdge()) {
            this.screenOffset = 0;
            this.leftTopPoint.y = this.defaultThumbStartY;
            return;
        }
        if (scrollToBottomEdge()) {
            this.screenOffset = getScreenMaxOffset();
            this.leftTopPoint.y = getThumbMaxY();
            return;
        }
        this.leftTopPoint.y = this.defaultThumbStartY + (this.screenOffset / this.multiple);
        if (this.leftTopPoint.y > getThumbMaxY()) {
            this.leftTopPoint.y = getThumbMaxY();
        }
        float thumbY = this.leftTopPoint.y;
        float minY = this.defaultThumbStartY;
        if (thumbY < minY) {
            this.leftTopPoint.y = minY;
        }
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        if (newState == 0) {
            hideBar();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawScrollBar(canvas);
    }

    private void drawScrollBar(Canvas canvas) {
        if (this.autoShowThisBar) {
            drawBg(canvas);
            drawThumb(canvas);
        }
    }

    private void drawBg(Canvas canvas) {
        RectF rectF = this.bgRect;
        float radius = this.bgRadius;
        canvas.drawRoundRect(rectF, radius, radius, this.bgPaint);
    }

    private void drawThumb(Canvas canvas) {
        RectF rectF = this.thumbRect;
        rectF.left = this.defaultThumbStartX;
        rectF.top = (int) this.leftTopPoint.y;
        RectF thumbRect = this.thumbRect;
        thumbRect.right = this.defaultThumbEndX;
        thumbRect.bottom = ((int) this.leftTopPoint.y) + getThumbHeight();
        if (this.viewTotalHeight <= this.screenHeight) {
            RectF fullThumbRect = this.thumbRect;
            fullThumbRect.top = this.defaultThumbStartY;
            fullThumbRect.bottom = this.defaultThumbEndY;
        }
        RectF finalRect = this.thumbRect;
        float radius = this.thumbRadius;
        canvas.drawRoundRect(finalRect, radius, radius, this.thumbPaint);
    }

    public void showBar() {
        this.animator.cancel();
        this.autoShowThisBar = true;
    }

    private void hideBar() {
        this.animator.cancel();
        this.animator.start();
    }

    private boolean scrollToTopEdge() {
        return !canScrollVertically(-1);
    }

    private boolean scrollToBottomEdge() {
        return true ^ canScrollVertically(1);
    }

    /** 滚动条滑块左上角坐标。 */
    private class Point {
        private float x;
        private float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{ x = " + this.x + ", y = " + this.y + " }";
        }
    }
}