package com.xtc.moment.module.widget.coverflow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.xtc.log.LogUtil;

/**
 * Horizontal cover flow layout manager: children are laid out in a row, scaled/faded/greyed
 * according to their distance from the centre and optionally rotated in 3D.
 */
public class CoverFlowLayoutManger extends RecyclerView.LayoutManager {

    private static final String TAG = "CoverFlowLayoutManger";

    /** Number of item frames cached in advance. */
    private static final int MAX_RECT_COUNT = 100;
    /** Scroll direction: to the right. */
    private static final int SCROLL_TO_RIGHT = 1;
    /** Scroll direction: to the left. */
    private static final int SCROLL_TO_LEFT = 2;
    /** Number of extra items laid out on both sides of the centre. */
    private static final int EXTRA_LAYOUT_RANGE = 20;
    /** Duration of the snap animation, in milliseconds. */
    private static final long SNAP_DURATION_MS = 500L;
    /** Rotation applied to the side items, in degrees. */
    private static final float ITEM_3D_ROTATION = 50.0f;

    private final SparseArray<Rect> mAllItemFrames = new SparseArray<Rect>();
    private final SparseBooleanArray mHasAttachedItems = new SparseBooleanArray();

    private final boolean mIsFlatFlow;
    private final boolean mItemGradualGrey;
    private final boolean mItemGradualAlpha;
    private final boolean mIsLoop;
    private final boolean mItem3D;
    private final FlowCustomAlphaParam mFlowCustomAlphaParam;

    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;
    private int mIntervalDistance;
    private float mIntervalRatio = 0.5f;
    private int mStartX;
    private int mStartY;
    private int mOffsetAll;
    private int mSelectPosition;
    private int mLastSelectPosition;

    private RecyclerView.Recycler mRecycle;
    private RecyclerView.State mState;
    private ValueAnimator mAnimation;
    private OnSelected mSelectedListener;

    /** Notifies that the centred item changed. */
    public interface OnSelected {
        void onItemSelected(int position);
    }

    private CoverFlowLayoutManger(boolean flatFlow, boolean gradualGrey, boolean gradualAlpha, float intervalRatio,
                                  boolean loop, boolean item3D, FlowCustomAlphaParam customAlphaParam,
                                  int intervalDistance) {
        this.mIsFlatFlow = flatFlow;
        this.mItemGradualGrey = gradualGrey;
        this.mItemGradualAlpha = gradualAlpha;
        this.mIsLoop = loop;
        this.mItem3D = item3D;
        this.mFlowCustomAlphaParam = customAlphaParam;
        this.mIntervalDistance = intervalDistance;
        if (intervalRatio >= 0.0f) {
            this.mIntervalRatio = intervalRatio;
        } else if (this.mIsFlatFlow) {
            this.mIntervalRatio = 1.1f;
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            this.mOffsetAll = 0;
            return;
        }
        this.mAllItemFrames.clear();
        this.mHasAttachedItems.clear();

        View first = recycler.getViewForPosition(0);
        addView(first);
        measureChildWithMargins(first, 0, 0);
        this.mDecoratedChildWidth = getDecoratedMeasuredWidth(first);
        this.mDecoratedChildHeight = getDecoratedMeasuredHeight(first);
        this.mStartX = Math.round(((getHorizontalSpace() - this.mDecoratedChildWidth) * 1.0f) / 2.0f);
        this.mStartY = Math.round(((getVerticalSpace() - this.mDecoratedChildHeight) * 1.0f) / 2.0f);

        float intervalDistance = this.mStartX;
        for (int i = 0; i < getItemCount() && i < MAX_RECT_COUNT; i++) {
            Rect frame = this.mAllItemFrames.get(i);
            if (frame == null) {
                frame = new Rect();
            }
            frame.set(Math.round(intervalDistance), this.mStartY,
                    Math.round(this.mDecoratedChildWidth + intervalDistance),
                    this.mStartY + this.mDecoratedChildHeight);
            this.mAllItemFrames.put(i, frame);
            this.mHasAttachedItems.put(i, false);
            intervalDistance += getIntervalDistance();
        }

        detachAndScrapAttachedViews(recycler);
        if ((this.mRecycle == null || this.mState == null) && this.mSelectPosition != 0) {
            this.mOffsetAll = calculateOffsetForPosition(this.mSelectPosition);
            onSelectedCallBack();
        }
        layoutItems(recycler, state, SCROLL_TO_LEFT);
        this.mRecycle = recycler;
        this.mState = state;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mAnimation != null && this.mAnimation.isRunning()) {
            this.mAnimation.cancel();
        }
        int scrollDistance;
        if (this.mIsLoop) {
            scrollDistance = dx;
        } else if (this.mOffsetAll + dx < 0) {
            scrollDistance = -this.mOffsetAll;
        } else if (this.mOffsetAll + dx > getMaxOffset()) {
            scrollDistance = (int) (getMaxOffset() - this.mOffsetAll);
        } else {
            scrollDistance = dx;
        }
        this.mOffsetAll += scrollDistance;
        layoutItems(recycler, state, dx > 0 ? SCROLL_TO_LEFT : SCROLL_TO_RIGHT);
        return scrollDistance;
    }

    private void layoutItems(RecyclerView.Recycler recycler, RecyclerView.State state, int scrollDirection) {
        if (state == null || state.isPreLayout()) {
            return;
        }
        int offset = this.mOffsetAll;
        Rect visibleRect = new Rect(offset, 0, getHorizontalSpace() + offset, getVerticalSpace());

        int centerPosition = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = child.getTag() != null ? checkTag(child.getTag()).pos : getPosition(child);
            Rect frame = getFrame(position);
            if (!Rect.intersects(visibleRect, frame)) {
                removeAndRecycleView(child, recycler);
                this.mHasAttachedItems.delete(position);
            } else {
                layoutItem(child, frame);
                this.mHasAttachedItems.put(position, true);
            }
            centerPosition = position;
        }
        if (centerPosition == 0) {
            centerPosition = getCenterPosition();
        }
        int start = centerPosition - EXTRA_LAYOUT_RANGE;
        int end = centerPosition + EXTRA_LAYOUT_RANGE;
        if (!this.mIsLoop) {
            if (start < 0) {
                start = 0;
            }
            if (end > getItemCount()) {
                end = getItemCount();
            }
        }
        for (int i = start; i < end; i++) {
            Rect frame = getFrame(i);
            if (!Rect.intersects(visibleRect, frame) || this.mHasAttachedItems.get(i)) {
                continue;
            }
            int adapterPosition = i % getItemCount();
            if (adapterPosition < 0) {
                adapterPosition += getItemCount();
            }
            View child = recycler.getViewForPosition(adapterPosition);
            checkTag(child.getTag());
            child.setTag(new ItemTag(i));
            measureChildWithMargins(child, 0, 0);
            if (scrollDirection == SCROLL_TO_RIGHT || this.mIsFlatFlow) {
                addView(child, 0);
            } else {
                addView(child);
            }
            layoutItem(child, frame);
            this.mHasAttachedItems.put(i, true);
        }
    }

    /** Positions a child and applies the scale, alpha, grey and 3D effects. */
    private void layoutItem(View view, Rect rect) {
        layoutDecorated(view, rect.left - this.mOffsetAll, rect.top,
                rect.right - this.mOffsetAll, rect.bottom);
        if (!this.mIsFlatFlow) {
            view.setScaleX(computeScale(rect.left - this.mOffsetAll));
            view.setScaleY(computeScale(rect.left - this.mOffsetAll));
        }
        if (this.mItemGradualAlpha) {
            view.setAlpha(computeAlpha(rect.left - this.mOffsetAll));
        }
        if (this.mItemGradualGrey) {
            greyItem(view, rect);
        }
        if (this.mFlowCustomAlphaParam != null) {
            setCustomAlpha(view, rect);
        }
        if (this.mItem3D) {
            item3D(view, rect);
        }
    }

    private void setCustomAlpha(View view, Rect rect) {
        float alpha = computeCustomAlpha(rect.left - this.mOffsetAll, this.mFlowCustomAlphaParam.getRatio());
        View target = view.findViewById(this.mFlowCustomAlphaParam.getViewId());
        if (target == null) {
            LogUtil.d(TAG, "view is null");
        } else {
            target.setAlpha(alpha);
        }
    }

    private Rect getFrame(int position) {
        Rect frame = this.mAllItemFrames.get(position);
        if (frame != null) {
            return frame;
        }
        Rect computed = new Rect();
        float left = this.mStartX + (getIntervalDistance() * position);
        computed.set(Math.round(left), this.mStartY,
                Math.round(left + this.mDecoratedChildWidth), this.mStartY + this.mDecoratedChildHeight);
        return computed;
    }

    /** Applies a colour matrix that fades the item towards grey when it leaves the centre. */
    private void greyItem(View view, Rect rect) {
        float greyScale = computeGreyScale(rect.left - this.mOffsetAll);
        float inverted = 1.0f - greyScale;
        float offset = 120.0f * inverted;
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                greyScale, 0.0f, 0.0f, 0.0f, offset,
                0.0f, greyScale, 0.0f, 0.0f, offset,
                0.0f, 0.0f, greyScale, 0.0f, offset,
                0.0f, 0.0f, 0.0f, 1.0f, inverted * 250.0f});
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        if (greyScale >= 1.0f) {
            view.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    private void item3D(View view, Rect rect) {
        float center = ((rect.left + rect.right) - (this.mOffsetAll * 2)) / 2.0f;
        float itemCenter = this.mStartX + (this.mDecoratedChildWidth / 2.0f);
        float rotation = ITEM_3D_ROTATION * ((float) Math.sqrt(Math.abs(
                ((center - itemCenter) * 1.0f) / (getItemCount() * getIntervalDistance()))));
        view.setRotationY(center > itemCenter ? -rotation : rotation);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            fixOffsetWhenFinishScroll();
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (position < 0 || position > getItemCount() - 1) {
            return;
        }
        this.mOffsetAll = calculateOffsetForPosition(position);
        RecyclerView.Recycler recycler = this.mRecycle;
        RecyclerView.State state = this.mState;
        if (recycler == null || state == null) {
            this.mSelectPosition = position;
        } else {
            layoutItems(recycler, state, position > this.mSelectPosition ? SCROLL_TO_LEFT : SCROLL_TO_RIGHT);
            onSelectedCallBack();
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (this.mIsLoop) {
            return;
        }
        int offset = calculateOffsetForPosition(position);
        if (this.mRecycle == null || this.mState == null) {
            this.mSelectPosition = position;
        } else {
            startScroll(this.mOffsetAll, offset);
        }
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
        this.mRecycle = null;
        this.mState = null;
        this.mOffsetAll = 0;
        this.mSelectPosition = 0;
        this.mLastSelectPosition = 0;
        this.mHasAttachedItems.clear();
        this.mAllItemFrames.clear();
    }

    private int getHorizontalSpace() {
        return (getWidth() - getPaddingRight()) - getPaddingLeft();
    }

    private int getVerticalSpace() {
        return (getHeight() - getPaddingBottom()) - getPaddingTop();
    }

    private float getMaxOffset() {
        return (getItemCount() - 1) * getIntervalDistance();
    }

    private float computeScale(int offset) {
        float scale = ((1.0f - ((Math.abs(offset - this.mStartX) * 1.0f)
                / Math.abs(this.mStartX + (this.mDecoratedChildWidth / this.mIntervalRatio)))) * 0.5f) + 0.5f;
        if (scale < 0.0f) {
            scale = 0.0f;
        }
        if (scale > 1.0f) {
            return 1.0f;
        }
        return scale;
    }

    private float computeGreyScale(int offset) {
        float scale = 1.0f - ((Math.abs((offset + (this.mDecoratedChildWidth / 2))
                - (getHorizontalSpace() / 2.0f)) * 1.0f) / (getHorizontalSpace() / 2));
        if (scale < 0.1d) {
            scale = 0.1f;
        }
        if (scale > 1.0f) {
            scale = 1.0f;
        }
        return (float) Math.pow(scale, 0.8d);
    }

    private float computeCustomAlpha(int offset, float ratio) {
        float alpha = ((Math.abs(offset - this.mStartX) * 1.0f)
                / Math.abs(this.mStartX + (this.mDecoratedChildWidth / this.mIntervalRatio))) * ratio;
        float max = ratio / 2.0f;
        if (alpha > max) {
            alpha = max;
        }
        if (alpha < 0.0f) {
            return 0.0f;
        }
        return alpha;
    }

    private float computeAlpha(int offset) {
        float alpha = 1.0f - ((Math.abs(offset - this.mStartX) * 1.0f)
                / Math.abs(this.mStartX + (this.mDecoratedChildWidth / this.mIntervalRatio)));
        if (alpha < 0.3f) {
            alpha = 0.3f;
        }
        if (alpha > 1.0f) {
            return 1.0f;
        }
        return alpha;
    }

    private int calculateOffsetForPosition(int position) {
        return Math.round(getIntervalDistance() * position);
    }

    /** Snaps to the nearest item once the fling has stopped. */
    private void fixOffsetWhenFinishScroll() {
        if (getIntervalDistance() == 0) {
            return;
        }
        int itemIndex = (int) ((this.mOffsetAll * 1.0f) / getIntervalDistance());
        float remainder = this.mOffsetAll % getIntervalDistance();
        if (Math.abs(remainder) > getIntervalDistance() * 0.5d) {
            itemIndex = remainder > 0.0f ? itemIndex + 1 : itemIndex - 1;
        }
        int targetOffset = itemIndex * getIntervalDistance();
        startScroll(this.mOffsetAll, targetOffset);
        this.mSelectPosition = Math.abs(Math.round((targetOffset * 1.0f) / getIntervalDistance())) % getItemCount();
    }

    private void startScroll(int from, int to) {
        if (this.mAnimation != null && this.mAnimation.isRunning()) {
            this.mAnimation.cancel();
        }
        final int scrollDirection = from < to ? SCROLL_TO_LEFT : SCROLL_TO_RIGHT;
        this.mAnimation = ValueAnimator.ofFloat(from, to);
        this.mAnimation.setDuration(SNAP_DURATION_MS);
        this.mAnimation.setInterpolator(new DecelerateInterpolator());
        this.mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetAll = Math.round(((Float) animation.getAnimatedValue()).floatValue());
                layoutItems(mRecycle, mState, scrollDirection);
            }
        });
        this.mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onSelectedCallBack();
            }
        });
        this.mAnimation.start();
    }

    private int getIntervalDistance() {
        if (this.mIntervalDistance == 0) {
            this.mIntervalDistance = Math.round(this.mDecoratedChildWidth * this.mIntervalRatio);
        }
        return this.mIntervalDistance;
    }

    private void onSelectedCallBack() {
        this.mSelectPosition = Math.round(this.mOffsetAll / (float) getIntervalDistance());
        this.mSelectPosition = Math.abs(this.mSelectPosition % getItemCount());
        OnSelected listener = this.mSelectedListener;
        if (listener != null && this.mSelectPosition != this.mLastSelectPosition) {
            listener.onItemSelected(this.mSelectPosition);
        }
        this.mLastSelectPosition = this.mSelectPosition;
    }

    private ItemTag checkTag(Object tag) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof ItemTag) {
            return (ItemTag) tag;
        }
        throw new IllegalArgumentException(
                "You should not use View#setTag(Object tag), use View#setTag(int key, Object tag) instead!");
    }

    public int getFirstVisiblePosition() {
        int offset = this.mOffsetAll;
        Rect visibleRect = new Rect(offset, 0, getHorizontalSpace() + offset, getVerticalSpace());
        int position = getCenterPosition();
        do {
            position--;
        } while (getFrame(position).left > visibleRect.left);
        return Math.abs(position) % getItemCount();
    }

    public int getLastVisiblePosition() {
        int offset = this.mOffsetAll;
        Rect visibleRect = new Rect(offset, 0, getHorizontalSpace() + offset, getVerticalSpace());
        int position = getCenterPosition();
        do {
            position++;
        } while (getFrame(position).right < visibleRect.right);
        return Math.abs(position) % getItemCount();
    }

    int getChildActualPos(int index) {
        View child = getChildAt(index);
        if (child.getTag() != null) {
            return checkTag(child.getTag()).pos;
        }
        return getPosition(child);
    }

    public int getMaxVisibleCount() {
        return (((getHorizontalSpace() - this.mStartX) / getIntervalDistance()) * 2) + 1;
    }

    int getCenterPosition() {
        int index = this.mOffsetAll / getIntervalDistance();
        int remainder = this.mOffsetAll % getIntervalDistance();
        if (Math.abs(remainder) >= getIntervalDistance() * 0.5f) {
            return remainder >= 0 ? index + 1 : index - 1;
        }
        return index;
    }

    public void setOnSelectedListener(OnSelected listener) {
        this.mSelectedListener = listener;
    }

    public int getSelectedPos() {
        return this.mSelectPosition;
    }

    /** Adapter position stored on each attached child. */
    private class ItemTag {
        final int pos;

        ItemTag(int pos) {
            this.pos = pos;
        }
    }

    /** Builder for {@link CoverFlowLayoutManger}. */
    static class Builder {

        boolean isFlat = false;
        boolean isGreyItem = false;
        boolean isAlphaItem = false;
        float cstIntervalRatio = -1.0f;
        boolean isLoop = false;
        boolean is3DItem = false;
        FlowCustomAlphaParam flowCustomAlphaParam = null;
        int intervalDistance = 0;

        Builder setFlat(boolean flat) {
            this.isFlat = flat;
            return this;
        }

        Builder setGreyItem(boolean greyItem) {
            this.isGreyItem = greyItem;
            return this;
        }

        Builder setAlphaItem(boolean alphaItem) {
            this.isAlphaItem = alphaItem;
            return this;
        }

        Builder setIntervalRatio(float intervalRatio) {
            this.cstIntervalRatio = intervalRatio;
            return this;
        }

        Builder loop() {
            this.isLoop = true;
            return this;
        }

        Builder set3DItem(boolean item3D) {
            this.is3DItem = item3D;
            return this;
        }

        Builder setFlowCustomAlphaParam(FlowCustomAlphaParam param) {
            this.flowCustomAlphaParam = param;
            return this;
        }

        Builder setIntervalDistance(int intervalDistance) {
            this.intervalDistance = intervalDistance;
            return this;
        }

        public CoverFlowLayoutManger build() {
            return new CoverFlowLayoutManger(this.isFlat, this.isGreyItem, this.isAlphaItem,
                    this.cstIntervalRatio, this.isLoop, this.is3DItem, this.flowCustomAlphaParam,
                    this.intervalDistance);
        }
    }
}