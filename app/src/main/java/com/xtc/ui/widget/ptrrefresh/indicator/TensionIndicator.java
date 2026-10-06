package com.xtc.ui.widget.ptrrefresh.indicator;

/** 带张力阻尼的下拉位移指示器：拖动越远阻尼越强。 */
public class TensionIndicator extends Indicator {
    private float mCurrentDragPercent;
    private float mDownPos;
    private float mDownY;
    private int mReleasePos;
    private float DRAG_RATE = 0.5f;
    private float mOneHeight = 0.0f;
    private float mReleasePercent = -1.0f;

    @Override
    public void onPressDown(float x, float y) {
        super.onPressDown(x, y);
        this.mDownY = y;
        this.mDownPos = getCurrentPosY();
    }

    @Override
    public void onRelease() {
        super.onRelease();
        this.mReleasePos = getCurrentPosY();
        this.mReleasePercent = this.mCurrentDragPercent;
    }

    @Override
    public void onUIRefreshComplete() {
        this.mReleasePos = getCurrentPosY();
        this.mReleasePercent = getOverDragPercent();
    }

    @Override
    public void setHeaderHeight(int headerHeight) {
        super.setHeaderHeight(headerHeight);
        this.mOneHeight = (headerHeight * 4.0f) / 5.0f;
    }

    @Override
    protected void processOnMove(float x, float y, float offsetX, float offsetY) {
        float downY = this.mDownY;
        if (y < downY) {
            super.processOnMove(x, y, offsetX, offsetY);
            return;
        }
        float dragDistance = ((y - downY) * this.DRAG_RATE) + this.mDownPos;
        float dragPercent = dragDistance / this.mOneHeight;
        if (dragPercent < 0.0f) {
            setOffset(offsetX, 0.0f);
            return;
        }
        this.mCurrentDragPercent = dragPercent;
        float clampedPercent = Math.min(1.0f, Math.abs(dragPercent));
        float oneHeight = this.mOneHeight;
        double tension = Math.max(0.0f, Math.min(dragDistance - oneHeight, oneHeight * 2.0f) / this.mOneHeight) / 4.0f;
        double tensionSquared = Math.pow(tension, 2.0d);
        float finalHeight = this.mOneHeight;
        setOffset(x, ((int) ((finalHeight * clampedPercent)
                + (((((float) (tension - tensionSquared)) * 2.0f) * finalHeight) / 2.0f))) - getCurrentPosY());
    }

    private float offsetToTarget(float target) {
        float dragPercent = target / this.mOneHeight;
        this.mCurrentDragPercent = dragPercent;
        Math.min(1.0f, Math.abs(dragPercent));
        float oneHeight = this.mOneHeight;
        Math.pow(Math.max(0.0f, Math.min(target - oneHeight, oneHeight * 2.0f) / this.mOneHeight) / 4.0f, 2.0d);
        float finalHeight = this.mOneHeight;
        return 0.0f;
    }

    @Override
    public int getOffsetToKeepHeaderWhileLoading() {
        return getOffsetToRefresh();
    }

    @Override
    public int getOffsetToRefresh() {
        return (int) this.mOneHeight;
    }

    public float getOverDragPercent() {
        if (isUnderTouch()) {
            return this.mCurrentDragPercent;
        }
        float releasePercent = this.mReleasePercent;
        if (releasePercent <= 0.0f) {
            return (getCurrentPosY() * 1.0f) / getOffsetToKeepHeaderWhileLoading();
        }
        return (releasePercent * getCurrentPosY()) / this.mReleasePos;
    }
}