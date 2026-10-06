package com.xtc.ui.widget.ptrrefresh.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import com.xtc.ui.widget.ptrrefresh.header.material.StoreHouseBarItem;
import com.xtc.ui.widget.ptrrefresh.header.material.StoreHousePath;
import com.xtc.ui.widget.ptrrefresh.indicator.Indicator;
import com.xtc.ui.widget.ptrrefresh.layout.BaseFrameLayout;
import com.xtc.ui.widget.ptrrefresh.utils.LocalDisplay;
import java.util.ArrayList;

/** StoreHouse 下拉刷新头部：把文字拆成线段，下拉时逐段落下。 */
public class StoreHouseHeader extends View implements UIRefreshHandler {
    private AniController mAniController;
    private float mBarDarkAlpha;
    private int mDrawZoneHeight;
    private int mDrawZoneWidth;
    private int mDropHeight;
    private float mFromAlpha;
    private int mHorizontalRandomness;
    private float mInternalAnimationFactor;
    private boolean mIsInLoading;
    public ArrayList<StoreHouseBarItem> mItemList;
    private int mLineWidth;
    private int mLoadingAniDuration;
    private int mLoadingAniItemDuration;
    private int mLoadingAniSegDuration;
    private int mOffsetX;
    private int mOffsetY;
    private float mProgress;
    private float mScale;
    private int mTextColor;
    private float mToAlpha;
    private Transformation mTransformation;

    @Override
    public void onUIRefreshPrepare(BaseFrameLayout frameLayout) {
    }

    public StoreHouseHeader(Context context) {
        super(context);
        this.mItemList = new ArrayList<>();
        this.mLineWidth = -1;
        this.mScale = 1.0f;
        this.mDropHeight = -1;
        this.mInternalAnimationFactor = 0.7f;
        this.mHorizontalRandomness = -1;
        this.mProgress = 0.0f;
        this.mDrawZoneWidth = 0;
        this.mDrawZoneHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBarDarkAlpha = 0.4f;
        this.mFromAlpha = 1.0f;
        this.mToAlpha = 0.4f;
        this.mLoadingAniDuration = 1000;
        this.mLoadingAniSegDuration = 1000;
        this.mLoadingAniItemDuration = 400;
        this.mTransformation = new Transformation();
        this.mIsInLoading = false;
        this.mAniController = new AniController();
        this.mTextColor = -1;
        initView();
    }

    public StoreHouseHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mItemList = new ArrayList<>();
        this.mLineWidth = -1;
        this.mScale = 1.0f;
        this.mDropHeight = -1;
        this.mInternalAnimationFactor = 0.7f;
        this.mHorizontalRandomness = -1;
        this.mProgress = 0.0f;
        this.mDrawZoneWidth = 0;
        this.mDrawZoneHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBarDarkAlpha = 0.4f;
        this.mFromAlpha = 1.0f;
        this.mToAlpha = 0.4f;
        this.mLoadingAniDuration = 1000;
        this.mLoadingAniSegDuration = 1000;
        this.mLoadingAniItemDuration = 400;
        this.mTransformation = new Transformation();
        this.mIsInLoading = false;
        this.mAniController = new AniController();
        this.mTextColor = -1;
        initView();
    }

    public StoreHouseHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mItemList = new ArrayList<>();
        this.mLineWidth = -1;
        this.mScale = 1.0f;
        this.mDropHeight = -1;
        this.mInternalAnimationFactor = 0.7f;
        this.mHorizontalRandomness = -1;
        this.mProgress = 0.0f;
        this.mDrawZoneWidth = 0;
        this.mDrawZoneHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBarDarkAlpha = 0.4f;
        this.mFromAlpha = 1.0f;
        this.mToAlpha = 0.4f;
        this.mLoadingAniDuration = 1000;
        this.mLoadingAniSegDuration = 1000;
        this.mLoadingAniItemDuration = 400;
        this.mTransformation = new Transformation();
        this.mIsInLoading = false;
        this.mAniController = new AniController();
        this.mTextColor = -1;
        initView();
    }

    private void initView() {
        LocalDisplay.init(getContext());
        this.mLineWidth = LocalDisplay.dp2px(1.0f);
        this.mDropHeight = LocalDisplay.dp2px(40.0f);
        this.mHorizontalRandomness = LocalDisplay.SCREEN_WIDTH_PIXELS / 2;
    }

    private void setProgress(float progress) {
        this.mProgress = progress;
    }

    public int getLoadingAniDuration() {
        return this.mLoadingAniDuration;
    }

    public void setLoadingAniDuration(int duration) {
        this.mLoadingAniDuration = duration;
        this.mLoadingAniSegDuration = duration;
    }

    public StoreHouseHeader setLineWidth(int lineWidth) {
        this.mLineWidth = lineWidth;
        for (int index = 0; index < this.mItemList.size(); index++) {
            this.mItemList.get(index).setLineWidth(lineWidth);
        }
        return this;
    }

    public StoreHouseHeader setTextColor(int color) {
        this.mTextColor = color;
        for (int index = 0; index < this.mItemList.size(); index++) {
            this.mItemList.get(index).setColor(color);
        }
        return this;
    }

    public StoreHouseHeader setDropHeight(int dropHeight) {
        this.mDropHeight = dropHeight;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
                getTopOffset() + this.mDrawZoneHeight + getBottomOffset(), 1073741824));
        this.mOffsetX = (getMeasuredWidth() - this.mDrawZoneWidth) / 2;
        this.mOffsetY = getTopOffset();
        this.mDropHeight = getTopOffset();
    }

    private int getTopOffset() {
        return getPaddingTop() + LocalDisplay.dp2px(10.0f);
    }

    private int getBottomOffset() {
        return getPaddingBottom() + LocalDisplay.dp2px(10.0f);
    }

    public void initWithString(String text) {
        initWithString(text, 25);
    }

    public void initWithString(String text, int scalePercent) {
        initWithPointList(StoreHousePath.getPath(text, scalePercent * 0.01f, 14));
    }

    public void initWithStringArray(int resId) {
        String[] stringArray = getResources().getStringArray(resId);
        ArrayList<float[]> pointList = new ArrayList<>();
        for (String line : stringArray) {
            String[] parts = line.split(",");
            float[] points = new float[4];
            for (int index = 0; index < 4; index++) {
                points[index] = Float.parseFloat(parts[index]);
            }
            pointList.add(points);
        }
        initWithPointList(pointList);
    }

    public float getScale() {
        return this.mScale;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void initWithPointList(ArrayList<float[]> pointList) {
        boolean hadItems = this.mItemList.size() > 0;
        this.mItemList.clear();
        int index = 0;
        float maxX = 0.0f;
        float maxY = 0.0f;
        while (index < pointList.size()) {
            float[] points = pointList.get(index);
            PointF startPoint = new PointF(LocalDisplay.dp2px(points[0]) * this.mScale, LocalDisplay.dp2px(points[1]) * this.mScale);
            PointF endPoint = new PointF(LocalDisplay.dp2px(points[2]) * this.mScale, LocalDisplay.dp2px(points[3]) * this.mScale);
            float newMaxX = Math.max(Math.max(maxX, startPoint.x), endPoint.x);
            float newMaxY = Math.max(Math.max(maxY, startPoint.y), endPoint.y);
            StoreHouseBarItem barItem = new StoreHouseBarItem(index, startPoint, endPoint, this.mTextColor, this.mLineWidth);
            barItem.resetPosition(this.mHorizontalRandomness);
            this.mItemList.add(barItem);
            index++;
            maxX = newMaxX;
            maxY = newMaxY;
        }
        this.mDrawZoneWidth = (int) Math.ceil(maxX);
        this.mDrawZoneHeight = (int) Math.ceil(maxY);
        if (hadItems) {
            requestLayout();
        }
    }

    private void beginLoading() {
        this.mIsInLoading = true;
        this.mAniController.start();
        invalidate();
    }

    private void loadFinish() {
        this.mIsInLoading = false;
        this.mAniController.stop();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = this.mProgress;
        int saveCount = canvas.save();
        int size = this.mItemList.size();
        for (int index = 0; index < size; index++) {
            canvas.save();
            StoreHouseBarItem barItem = this.mItemList.get(index);
            float centerX = this.mOffsetX + barItem.midPoint.x;
            float centerY = this.mOffsetY + barItem.midPoint.y;
            if (this.mIsInLoading) {
                barItem.getTransformation(getDrawingTime(), this.mTransformation);
                canvas.translate(centerX, centerY);
            } else if (progress == 0.0f) {
                barItem.resetPosition(this.mHorizontalRandomness);
            } else {
                float factor = this.mInternalAnimationFactor;
                float startFraction = ((1.0f - factor) * index) / size;
                float itemDuration = (1.0f - factor) - startFraction;
                if (progress == 1.0f || progress >= 1.0f - itemDuration) {
                    canvas.translate(centerX, centerY);
                    barItem.setAlpha(this.mBarDarkAlpha);
                } else {
                    float itemProgress = progress > startFraction ? Math.min(1.0f, (progress - startFraction) / factor) : 0.0f;
                    float remaining = 1.0f - itemProgress;
                    float translateX = centerX + (barItem.translationX * remaining);
                    float translateY = centerY + ((-this.mDropHeight) * remaining);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(360.0f * itemProgress);
                    matrix.postScale(itemProgress, itemProgress);
                    matrix.postTranslate(translateX, translateY);
                    barItem.setAlpha(this.mBarDarkAlpha * itemProgress);
                    canvas.concat(matrix);
                }
            }
            barItem.draw(canvas);
            canvas.restore();
        }
        if (this.mIsInLoading) {
            invalidate();
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void onUIReset(BaseFrameLayout frameLayout) {
        loadFinish();
        for (int index = 0; index < this.mItemList.size(); index++) {
            this.mItemList.get(index).resetPosition(this.mHorizontalRandomness);
        }
    }

    @Override
    public void onUIRefreshBegin(BaseFrameLayout frameLayout) {
        beginLoading();
    }

    @Override
    public void onUIRefreshComplete(BaseFrameLayout frameLayout, boolean isSuccess) {
        loadFinish();
    }

    @Override
    public void onUIPositionChange(BaseFrameLayout frameLayout, boolean isUnderTouch, byte status, Indicator indicator) {
        setProgress(Math.min(1.0f, indicator.getCurrentPercent()));
        invalidate();
    }

    /** 加载动画的定时控制器。 */
    private class AniController implements Runnable {
        private int mCountPerSeg;
        private int mInterval;
        private boolean mRunning;
        private int mSegCount;
        private int mTick;

        private AniController() {
            this.mTick = 0;
            this.mCountPerSeg = 0;
            this.mSegCount = 0;
            this.mInterval = 0;
            this.mRunning = true;
        }

        private void start() {
            this.mRunning = true;
            this.mTick = 0;
            this.mInterval = StoreHouseHeader.this.mLoadingAniDuration / StoreHouseHeader.this.mItemList.size();
            this.mCountPerSeg = StoreHouseHeader.this.mLoadingAniSegDuration / this.mInterval;
            this.mSegCount = (StoreHouseHeader.this.mItemList.size() / this.mCountPerSeg) + 1;
            run();
        }

        @Override
        public void run() {
            int offset = this.mTick % this.mCountPerSeg;
            for (int segment = 0; segment < this.mSegCount; segment++) {
                int itemIndex = (this.mCountPerSeg * segment) + offset;
                if (itemIndex <= this.mTick) {
                    StoreHouseBarItem barItem = StoreHouseHeader.this.mItemList.get(itemIndex % StoreHouseHeader.this.mItemList.size());
                    barItem.setFillAfter(false);
                    barItem.setFillEnabled(true);
                    barItem.setFillBefore(false);
                    barItem.setDuration(StoreHouseHeader.this.mLoadingAniItemDuration);
                    barItem.start(StoreHouseHeader.this.mFromAlpha, StoreHouseHeader.this.mToAlpha);
                }
            }
            this.mTick++;
            if (this.mRunning) {
                StoreHouseHeader.this.postDelayed(this, this.mInterval);
            }
        }

        private void stop() {
            this.mRunning = false;
            StoreHouseHeader.this.removeCallbacks(this);
        }
    }
}