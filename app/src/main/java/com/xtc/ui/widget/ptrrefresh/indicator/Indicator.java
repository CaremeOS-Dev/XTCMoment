package com.xtc.ui.widget.ptrrefresh.indicator;

import android.graphics.PointF;

/** 下拉刷新位移指示器：记录拖动位置、刷新阈值与释放状态。 */
public class Indicator {
    public static final int POS_START = 0;
    private int mHeaderHeight;
    private float mOffsetX;
    private float mOffsetY;
    private int mOffsetToRefresh = 0;
    private boolean mIsUnderTouch = false;
    private int mLastPos = 0;
    private int mCurrentPos = 0;
    private int mPressedPos = 0;
    private int mRefreshCompleteY = 0;
    private float mResistance = 1.7f;
    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private int mOffsetToKeepHeaderWhileLoading = -1;
    private PointF mPtLastMove = new PointF();

    protected void onUpdatePos(int currentPos, int lastPos) {
    }

    public boolean willOverTop(int position) {
        return position < 0;
    }

    public boolean isUnderTouch() {
        return this.mIsUnderTouch;
    }

    public void onRelease() {
        this.mIsUnderTouch = false;
    }

    public float getResistance() {
        return this.mResistance;
    }

    public void setResistance(float resistance) {
        this.mResistance = resistance;
    }

    public void onUIRefreshComplete() {
        this.mRefreshCompleteY = this.mCurrentPos;
    }

    public boolean goDownCrossFinishPosition() {
        return this.mCurrentPos >= this.mRefreshCompleteY;
    }

    protected void processOnMove(float x, float y, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY / this.mResistance);
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        this.mRatioOfHeaderHeightToRefresh = ratio;
        this.mOffsetToRefresh = (int) (this.mHeaderHeight * ratio);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return this.mRatioOfHeaderHeightToRefresh;
    }

    public int getOffsetToRefresh() {
        return this.mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offsetToRefresh) {
        this.mRatioOfHeaderHeightToRefresh = this.mHeaderHeight / offsetToRefresh;
        this.mOffsetToRefresh = offsetToRefresh;
    }

    public void onPressDown(float x, float y) {
        this.mIsUnderTouch = true;
        this.mPressedPos = this.mCurrentPos;
        this.mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        processOnMove(x, y, x - this.mPtLastMove.x, y - this.mPtLastMove.y);
        this.mPtLastMove.set(x, y);
    }

    protected void setOffset(float offsetX, float offsetY) {
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
    }

    public float getOffsetX() {
        return this.mOffsetX;
    }

    public float getOffsetY() {
        return this.mOffsetY;
    }

    public int getLastPosY() {
        return this.mLastPos;
    }

    public int getCurrentPosY() {
        return this.mCurrentPos;
    }

    public final void setCurrentPos(int currentPos) {
        this.mLastPos = this.mCurrentPos;
        this.mCurrentPos = currentPos;
        onUpdatePos(currentPos, this.mLastPos);
    }

    public int getHeaderHeight() {
        return this.mHeaderHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.mHeaderHeight = headerHeight;
        updateHeight();
    }

    protected void updateHeight() {
        this.mOffsetToRefresh = (int) (this.mRatioOfHeaderHeightToRefresh * this.mHeaderHeight);
    }

    public void convertFrom(Indicator indicator) {
        this.mCurrentPos = indicator.mCurrentPos;
        this.mLastPos = indicator.mLastPos;
        this.mHeaderHeight = indicator.mHeaderHeight;
    }

    public boolean hasLeftStartPosition() {
        return this.mCurrentPos > 0;
    }

    public boolean hasJustLeftStartPosition() {
        return this.mLastPos == 0 && hasLeftStartPosition();
    }

    public boolean hasJustBackToStartPosition() {
        return this.mLastPos != 0 && isInStartPosition();
    }

    public boolean isOverOffsetToRefresh() {
        return this.mCurrentPos >= getOffsetToRefresh();
    }

    public boolean hasMovedAfterPressedDown() {
        return this.mCurrentPos != this.mPressedPos;
    }

    public boolean isInStartPosition() {
        return this.mCurrentPos == 0;
    }

    public boolean crossRefreshLineFromTopToBottom() {
        return this.mLastPos < getOffsetToRefresh() && this.mCurrentPos >= getOffsetToRefresh();
    }

    public boolean hasJustReachedHeaderHeightFromTopToBottom() {
        int lastPos = this.mLastPos;
        int headerHeight = this.mHeaderHeight;
        return lastPos < headerHeight && this.mCurrentPos >= headerHeight;
    }

    public boolean isOverOffsetToKeepHeaderWhileLoading() {
        return this.mCurrentPos > getOffsetToKeepHeaderWhileLoading();
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        this.mOffsetToKeepHeaderWhileLoading = offset;
    }

    public int getOffsetToKeepHeaderWhileLoading() {
        int offset = this.mOffsetToKeepHeaderWhileLoading;
        return offset >= 0 ? offset : this.mHeaderHeight;
    }

    public boolean isAlreadyHere(int position) {
        return this.mCurrentPos == position;
    }

    public float getLastPercent() {
        int headerHeight = this.mHeaderHeight;
        if (headerHeight == 0) {
            return 0.0f;
        }
        return (this.mLastPos * 1.0f) / headerHeight;
    }

    public float getCurrentPercent() {
        int headerHeight = this.mHeaderHeight;
        if (headerHeight == 0) {
            return 0.0f;
        }
        return (this.mCurrentPos * 1.0f) / headerHeight;
    }
}