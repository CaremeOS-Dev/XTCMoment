package com.xtc.moment.dress;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.xtc.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态装扮播放管理基类，维护可见的 SVGAImageView 集合。
 */
public class BaseDressManager {

    private static final String TAG = BaseDressManager.class.getSimpleName();

    private final List<SVGAImageView> visibleImageViews = new ArrayList<>();

    public void viewAttachedToWindow(SVGAImageView imageView) {
        boolean hasDrawable = imageView.getDrawable() instanceof SVGADrawable;
        if (hasDrawable) {
            addVisibleImageView(imageView);
        }
        if (!imageView.isAnimating() && hasDrawable) {
            LogUtil.i(TAG, "onViewAttachedToWindow  startAnimation");
            imageView.startAnimation();
        }
    }

    public void viewDetachedFromWindow(SVGAImageView imageView) {
        if (imageView != null && (imageView.getDrawable() instanceof SVGADrawable)) {
            removeVisibleImageView(imageView);
        }
    }

    public void clearAllVisibleImageViews() {
        this.visibleImageViews.clear();
    }

    protected void addVisibleImageView(SVGAImageView imageView) {
        if (this.visibleImageViews.contains(imageView)) {
            return;
        }
        this.visibleImageViews.add(imageView);
    }

    protected void removeVisibleImageView(SVGAImageView imageView) {
        if (this.visibleImageViews.contains(imageView)) {
            this.visibleImageViews.remove(imageView);
        }
    }

    public void notifyVisibleToWindow() {
        if (this.visibleImageViews.isEmpty()) {
            return;
        }
        LogUtil.i(TAG, "notifyVisibleToWindow  通知可见的动态装扮重新开始播放");
        for (int index = 0; index < this.visibleImageViews.size(); index++) {
            SVGAImageView imageView = this.visibleImageViews.get(index);
            if (imageView == null || imageView.isAnimating()) {
                break;
            }
            imageView.startAnimation();
        }
    }

    public void notifyInvisibleToWindow() {
        if (this.visibleImageViews.isEmpty()) {
            return;
        }
        LogUtil.i(TAG, "notifyVisibleToWindow  通知隐藏的动态装扮暂停播放");
        for (int index = 0; index < this.visibleImageViews.size(); index++) {
            SVGAImageView imageView = this.visibleImageViews.get(index);
            if (imageView == null || !imageView.isAnimating()) {
                break;
            }
            imageView.pauseAnimation();
        }
    }
}