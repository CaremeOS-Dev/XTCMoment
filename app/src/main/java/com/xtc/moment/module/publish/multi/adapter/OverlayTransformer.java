package com.xtc.moment.module.publish.multi.adapter;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Page transformer that stacks the pages behind the current one with a slight scale and offset.
 */
public class OverlayTransformer implements ViewPager.PageTransformer {

    private static final String TAG = "OverlayTransformer";

    /** Default scale offset applied per page, in pixels. */
    private static final float DEFAULT_SCALE_OFFSET = 40.0f;
    /** Default horizontal offset applied per page, in pixels. */
    private static final float DEFAULT_TRANS_OFFSET = 25.0f;
    /** Value that keeps the default when passed to the constructor. */
    private static final float KEEP_DEFAULT = -1.0f;
    /** Fraction of the offset applied to the page just behind the current one. */
    private static final float BEHIND_PAGE_FACTOR = 0.8f;

    private final int overlayCount;
    private float scaleOffset = DEFAULT_SCALE_OFFSET;
    private float transOffset = DEFAULT_TRANS_OFFSET;

    public OverlayTransformer(int overlayCount) {
        this.overlayCount = overlayCount;
    }

    public OverlayTransformer(int overlayCount, float scaleOffset, float transOffset) {
        this.overlayCount = overlayCount;
        if (Float.compare(scaleOffset, KEEP_DEFAULT) != 0) {
            this.scaleOffset = scaleOffset;
        }
        if (Float.compare(transOffset, KEEP_DEFAULT) != 0) {
            this.transOffset = transOffset;
        }
    }

    public int getOverlayCount() {
        return this.overlayCount;
    }

    @Override
    public void transformPage(View page, float position) {
        if (position <= 0.0f) {
            page.setTranslationX(0.0f);
            page.setAlpha(1.0f - (Math.abs(position) * 0.5f));
            page.setClickable(true);
        } else {
            transformOtherPage(page, position);
            page.setClickable(false);
        }
    }

    private void transformOtherPage(View page, float position) {
        if (position > this.overlayCount) {
            position -= this.overlayCount;
        }
        float scale = (page.getWidth() - (this.scaleOffset * position)) / page.getWidth();
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setAlpha(1.0f);
        if (position > this.overlayCount - 1 && position < this.overlayCount) {
            float currentOffset = this.transOffset * ((float) Math.floor(position));
            float previousOffset = this.transOffset * ((float) Math.floor(position - 1.0f));
            Math.abs(position % ((int) position));
            page.setTranslationX(((-page.getWidth()) * position) + previousOffset
                    + ((currentOffset - previousOffset) * BEHIND_PAGE_FACTOR));
            return;
        }
        if (position <= this.overlayCount - 1) {
            page.setTranslationX(((-page.getWidth()) * position) + (this.transOffset * position));
        } else {
            page.setAlpha(0.0f);
            page.setTranslationX(0.0f);
        }
    }
}