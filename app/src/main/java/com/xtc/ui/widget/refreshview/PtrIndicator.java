package com.xtc.ui.widget.refreshview;

import android.graphics.PointF;
import com.xtc.log.LogUtil;

/** 下拉刷新位移指示器：记录拖动位置、刷新阈值与释放状态。 */
public class PtrIndicator {
    public static final int POS_START = 0;
    private int mHeaderHeight;
    private float mOffsetX;
    private float mOffsetY;
    protected int mOffsetToRefresh = 0;
    private PointF mPtLastMove = new PointF();
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mPressedPos = 0;
    private float mRatioOfHeaderHeightToRefresh = 0.8f;
    private float mResistance = 1.7f;
    private boolean mIsUnderTouch = false;
    private int mOffsetToKeepHeaderWhileLoading = -1;
    private int mRefreshCompleteY = 0;

    protected void onUpdatePos(int currentPos, int lastPos) {
    }

    public boolean willOverTop(int position) {
        return position < 0;
    }

    public boolean isUnderTouch() {
        return this.mIsUnderTouch;
    }

    public float getResistance() {
        return this.mResistance;
    }

    public void setResistance(float resistance) {
        this.mResistance = resistance;
    }

    public void onRelease() {
        this.mIsUnderTouch = false;
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
        LogUtil.d("test", "ratio--->>" + ratio + "," + this.mHeaderHeight + "," + (this.mHeaderHeight * ratio));
        this.mOffsetToRefresh = (int) (((float) this.mHeaderHeight) * ratio);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return this.mRatioOfHeaderHeightToRefresh;
    }

    public int getOffsetToRefresh() {
        return this.mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offsetToRefresh) {
        this.mRatioOfHeaderHeightToRefresh = this.mHeaderHeight / offsetToRefresh;
        LogUtil.d("test", "mHeaderHeight--->>" + offsetToRefresh);
        this.mOffsetToRefresh = offsetToRefresh;
    }

    public void onPressDown(float x, float y) {
        this.mIsUnderTouch = true;
        this.mPressedPos = this.mCurrentPos;
        this.mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float deltaX = x - this.mPtLastMove.x;
        float deltaY = y - this.mPtLastMove.y;
        LogUtil.d("test", "onMove--->>" + x + "," + y + "," + deltaX + "," + deltaY);
        processOnMove(x, y, deltaX, deltaY);
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
        LogUtil.d("test", "mOffsetToRefresh----->>" + this.mRatioOfHeaderHeightToRefresh + "," + this.mHeaderHeight
                + "," + (this.mRatioOfHeaderHeightToRefresh * this.mHeaderHeight));
        this.mOffsetToRefresh = (int) (this.mRatioOfHeaderHeightToRefresh * ((float) this.mHeaderHeight));
    }

    public void convertFrom(PtrIndicator indicator) {
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
        LogUtil.d("test", "mCurrentPos--->>" + this.mCurrentPos + "," + getOffsetToRefresh());
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