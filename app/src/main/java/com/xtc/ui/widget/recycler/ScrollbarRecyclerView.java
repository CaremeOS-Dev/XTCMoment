package com.xtc.ui.widget.recycler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.PathInterpolator;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

/** 自绘滚动条的 RecyclerView：滚动时显示并在停止后淡出。 */
public class ScrollbarRecyclerView extends RecyclerView {
    private static final int BOTTOM = -1;
    private static final int CENTER = 0;
    private static final float COEFFICIENT = 0.4f;
    private static int DEFAULT_ALPHA_BG = 102;
    private static int DEFAULT_ALPHA_THUMB = 255;
    private static final int MAX_ANIM_SCROLL = 4;
    private static final int START_DISMISS = 12;
    private static final String TAG = "ScrollbarRecyclerView";
    private static final int TOP = 1;
    private ValueAnimator alphaAnimator;
    private int alphaBg;
    private int alphaThumb;
    private boolean barVisible;
    private int bgColor;
    private float bgPadding;
    private Paint bgPaint;
    private float defaultThumbHeight;
    private int directionFlag;
    private boolean isActionUp;
    private Handler mainHandler;
    private float rightMargin;
    private float rvHeight;
    private float rvScrollY;
    private int rvWidth;
    private float scrollbarHeight;
    private float scrollbarWidth;
    private int thumbColor;
    private float thumbHeight;
    private float thumbHeightMinus;
    private Paint thumbPaint;
    private float thumbTopOffset;
    private float topMargin;

    public ScrollbarRecyclerView(Context context) {
        this(context, null);
    }

    public ScrollbarRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollbarRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alphaBg = DEFAULT_ALPHA_BG;
        this.alphaThumb = DEFAULT_ALPHA_THUMB;
        this.mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what != 12) {
                    return;
                }
                ScrollbarRecyclerView.this.alphaAnimator.start();
            }
        };
        this.barVisible = true;
        Resources resources = context.getResources();
        this.scrollbarWidth = resources.getDimension(R.dimen.scrollbarRv_default_bg_width);
        this.scrollbarHeight = resources.getDimension(R.dimen.scrollbarRv_default_bg_height);
        this.topMargin = resources.getDimension(R.dimen.scrollbarRv_default_bg_margin_top);
        this.rightMargin = resources.getDimension(R.dimen.scrollbarRv_default_bg_margin_right);
        this.bgPadding = resources.getDimension(R.dimen.scrollbarRv_default_bg_padding);
        this.defaultThumbHeight = resources.getDimension(R.dimen.scrollbarRv_default_thumb_height);
        this.bgColor = resources.getColor(R.color.color_ffffff);
        int defaultThumbColor = resources.getColor(R.color.color_ffffff);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ScrollbarRecyclerView);
        if (attributes != null) {
            this.thumbColor = attributes.getInt(R.styleable.ScrollbarRecyclerView_thumbColor, defaultThumbColor);
            attributes.recycle();
        } else {
            this.thumbColor = defaultThumbColor;
        }
        this.thumbHeight = this.defaultThumbHeight;
        this.directionFlag = 1;
        initAlphaAnimator();
        setVerticalScrollBarEnabled(false);
        initPaint();
        setOnScrollListener();
        LogUtil.i(TAG, "init completed --->");
    }

    private void initAlphaAnimator() {
        this.alphaAnimator = new ValueAnimator();
        this.alphaAnimator.setDuration(720L);
        this.alphaAnimator.setFloatValues(0.0f, 1.0f);
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.6f, 0.0f);
        path.lineTo(1.0f, 1.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            this.alphaAnimator.setInterpolator(new PathInterpolator(path));
        }
        this.alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = ((Float) animation.getAnimatedValue()).floatValue();
                if (fraction > 0.0f) {
                    ScrollbarRecyclerView.this.alphaBg = (int) (ScrollbarRecyclerView.DEFAULT_ALPHA_BG - (ScrollbarRecyclerView.DEFAULT_ALPHA_BG * fraction));
                    ScrollbarRecyclerView.this.alphaThumb = (int) (ScrollbarRecyclerView.DEFAULT_ALPHA_THUMB - (fraction * ScrollbarRecyclerView.DEFAULT_ALPHA_THUMB));
                    ScrollbarRecyclerView.this.invalidate();
                }
            }
        });
    }

    private void initPaint() {
        this.bgPaint = new Paint();
        this.bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.bgPaint.setAntiAlias(true);
        this.bgPaint.setColor(this.bgColor);
        this.thumbPaint = new Paint();
        this.thumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.thumbPaint.setAntiAlias(true);
        this.thumbPaint.setColor(this.thumbColor);
    }

    private void setOnScrollListener() {
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    ScrollbarRecyclerView.this.stopScrolling();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ScrollbarRecyclerView.this.rvScrollY += dy;
                if (dy != 0) {
                    ScrollbarRecyclerView.this.resetThumbUi();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void stopScrolling() {
        int scrollOffset = computeVerticalScrollOffset();
        int scrollExtent = computeVerticalScrollExtent();
        int scrollRange = computeVerticalScrollRange();
        if (scrollOffset == 0) {
            this.rvScrollY = 0.0f;
        } else if (scrollExtent + scrollOffset >= scrollRange) {
            this.rvHeight = this.rvScrollY;
        }
        if (this.isActionUp) {
            startDismiss(true);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        startDismiss(true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.barVisible) {
            drawCustomBars(canvas);
        }
    }

    public void setScrollbarVisible(boolean visible) {
        LogUtil.i(TAG, "setScrollbarVisible = " + visible);
        this.barVisible = visible;
    }

    private void drawCustomBars(Canvas canvas) {
        if (this.rvWidth == 0) {
            this.rvWidth = getWidth();
        }
        if (this.rvHeight == 0.0f) {
            this.rvHeight = computeVerticalScrollRange() - computeVerticalScrollExtent();
        }
        if (this.rvHeight <= 0.0f) {
            return;
        }
        this.bgPaint.setAlpha(this.alphaBg);
        float right = this.rvWidth - this.rightMargin;
        float barWidth = this.scrollbarWidth;
        float left = right - barWidth;
        float top = this.topMargin;
        float cornerRadius = barWidth / 2.0f;
        canvas.drawRoundRect(new RectF(left, top, right, this.scrollbarHeight + top), cornerRadius, cornerRadius, this.bgPaint);
        float thumbTrackHeight = this.scrollbarHeight - this.defaultThumbHeight;
        float padding = this.bgPadding;
        float availableHeight = thumbTrackHeight - (2.0f * padding);
        float trackTop = top + padding;
        float thumbTop = ((this.rvScrollY / this.rvHeight) * availableHeight) + trackTop;
        float trackBottom = availableHeight + trackTop;
        if (thumbTop <= trackTop) {
            this.directionFlag = 1;
        } else if (thumbTop >= trackBottom) {
            this.directionFlag = -1;
            trackTop = trackBottom;
        } else {
            trackTop = thumbTop;
        }
        float finalThumbTop = trackTop + this.thumbTopOffset;
        this.thumbHeight = this.defaultThumbHeight - this.thumbHeightMinus;
        this.thumbPaint.setAlpha(this.alphaThumb);
        float thumbPadding = this.bgPadding;
        float thumbRadius = cornerRadius - thumbPadding;
        canvas.drawRoundRect(new RectF(left + thumbPadding, finalThumbTop, right - thumbPadding, this.thumbHeight + finalThumbTop),
                thumbRadius, thumbRadius, this.thumbPaint);
    }

    @Override
    public void scrollToPosition(int position) {
        LogUtil.i(TAG, "scrollToPosition = " + position);
        super.scrollToPosition(position);
        RecyclerView.Adapter adapter = getAdapter();
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (adapter == null || layoutManager == null) {
            return;
        }
        int itemCount = adapter.getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int remainder = itemCount % spanCount;
            itemCount /= spanCount;
            if (remainder != 0) {
                itemCount++;
            }
            position /= spanCount;
        } else if (!(layoutManager instanceof LinearLayoutManager)) {
            LogUtil.w(TAG, "这个 LayoutManager 不处理 --> " + layoutManager);
            position = 0;
            itemCount = 0;
        }
        if (itemCount == 0) {
            return;
        }
        this.rvScrollY = (computeVerticalScrollRange() / itemCount) * position;
        startDismiss(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            this.isActionUp = false;
            changeThumbUi();
            return super.onTouchEvent(event);
        }
        if (action == 1) {
            this.isActionUp = true;
            resetThumbUi();
        } else if (action != 2) {
            if (action == 3) {
                this.isActionUp = true;
                resetThumbUi();
            }
        }
        return super.onTouchEvent(event);
    }

    private void changeThumbUi() {
        this.alphaAnimator.cancel();
        this.alphaBg = DEFAULT_ALPHA_BG;
        this.alphaThumb = DEFAULT_ALPHA_THUMB;
        if (this.directionFlag == 1) {
            float minus = this.thumbHeightMinus;
            if (minus < 4.0f) {
                this.thumbHeightMinus = minus + COEFFICIENT;
                return;
            }
        }
        if (this.directionFlag == -1) {
            float minus = this.thumbHeightMinus;
            if (minus < 4.0f) {
                this.thumbHeightMinus = minus + COEFFICIENT;
                this.thumbTopOffset += COEFFICIENT;
            }
        }
    }

    private void resetThumbUi() {
        this.directionFlag = 0;
        this.thumbHeightMinus = 0.0f;
        this.thumbTopOffset = 0.0f;
    }

    private void startDismiss(boolean delayed) {
        this.mainHandler.removeMessages(12);
        if (this.alphaAnimator.isRunning()) {
            LogUtil.d(TAG, "前一个 动画进行中 , return !");
        } else if (delayed) {
            this.mainHandler.sendEmptyMessageDelayed(12, 1000L);
        } else {
            this.mainHandler.sendEmptyMessage(12);
        }
    }

    public void setThumbColor(int thumbColor) {
        if (this.thumbColor == thumbColor) {
            return;
        }
        this.thumbColor = thumbColor;
        this.thumbPaint.setColor(this.thumbColor);
    }

    public int getThumbColor() {
        return this.thumbColor;
    }
}