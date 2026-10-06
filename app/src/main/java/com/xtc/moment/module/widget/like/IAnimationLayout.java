package com.xtc.moment.module.widget.like;

import java.util.List;

/**
 * 点赞动画布局接口。
 */
public interface IAnimationLayout {
    void addLikeImage(int resId);

    void addLikeImages(List<Integer> resIds);

    void addLikeImages(Integer... resIds);

    void addFavor();
}