package com.xtc.moment.module.widget.coverflow;

/** View id plus ratio used to fade a child of the cover flow item. */
public class FlowCustomAlphaParam {

    private int viewId = -1;
    private float ratio = 0.0f;

    public FlowCustomAlphaParam(int viewId, float ratio) {
        this.viewId = viewId;
        this.ratio = ratio;
    }

    public int getViewId() {
        return this.viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public float getRatio() {
        return this.ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}