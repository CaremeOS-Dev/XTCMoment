package com.xtc.moment.module.widget.coverflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * RecyclerView using {@link CoverFlowLayoutManger} and drawing the centred item on top.
 */
public class RecyclerCoverFlow extends RecyclerView {

    private float mDownX;
    private CoverFlowLayoutManger.Builder mManagerBuilder;

    public RecyclerCoverFlow(Context context) {
        super(context);
        init();
    }

    public RecyclerCoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerCoverFlow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        createManageBuilder();
        setLayoutManager(this.mManagerBuilder.build());
        setChildrenDrawingOrderEnabled(true);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private void createManageBuilder() {
        if (this.mManagerBuilder == null) {
            this.mManagerBuilder = new CoverFlowLayoutManger.Builder();
        }
    }

    public void setFlatFlow(boolean flatFlow) {
        createManageBuilder();
        this.mManagerBuilder.setFlat(flatFlow);
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void setGreyItem(boolean greyItem) {
        createManageBuilder();
        this.mManagerBuilder.setGreyItem(greyItem);
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void setAlphaItem(boolean alphaItem) {
        createManageBuilder();
        this.mManagerBuilder.setAlphaItem(alphaItem);
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void setLoop() {
        createManageBuilder();
        this.mManagerBuilder.loop();
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void set3DItem(boolean item3D) {
        createManageBuilder();
        this.mManagerBuilder.set3DItem(item3D);
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void setIntervalRatio(float intervalRatio) {
        createManageBuilder();
        this.mManagerBuilder.setIntervalRatio(intervalRatio);
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void setFlowCustomAlphaParam(FlowCustomAlphaParam param) {
        createManageBuilder();
        this.mManagerBuilder.setFlowCustomAlphaParam(param);
        setLayoutManager(this.mManagerBuilder.build());
    }

    public void setIntervalDistance(int intervalDistance) {
        createManageBuilder();
        this.mManagerBuilder.setIntervalDistance(intervalDistance);
        setLayoutManager(this.mManagerBuilder.build());
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof CoverFlowLayoutManger)) {
            throw new IllegalArgumentException("The layout manager must be CoverFlowLayoutManger");
        }
        super.setLayoutManager(layoutManager);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int drawingPosition) {
        int relativePosition = getCoverFlowLayout().getChildActualPos(drawingPosition)
                - getCoverFlowLayout().getCenterPosition();
        if (relativePosition >= 0) {
            drawingPosition = (childCount - 1) - relativePosition;
        }
        if (drawingPosition < 0) {
            return 0;
        }
        int lastIndex = childCount - 1;
        return drawingPosition > lastIndex ? lastIndex : drawingPosition;
    }

    public CoverFlowLayoutManger getCoverFlowLayout() {
        return (CoverFlowLayoutManger) getLayoutManager();
    }

    public int getSelectedPos() {
        return getCoverFlowLayout().getSelectedPos();
    }

    public void setOnItemSelectedListener(CoverFlowLayoutManger.OnSelected listener) {
        getCoverFlowLayout().setOnSelectedListener(listener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.mDownX = event.getX();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == MotionEvent.ACTION_MOVE) {
            if ((event.getX() > this.mDownX && getCoverFlowLayout().getCenterPosition() == 0)
                    || (event.getX() < this.mDownX
                    && getCoverFlowLayout().getCenterPosition() == getCoverFlowLayout().getItemCount() - 1)) {
                getParent().requestDisallowInterceptTouchEvent(false);
            } else {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }
}