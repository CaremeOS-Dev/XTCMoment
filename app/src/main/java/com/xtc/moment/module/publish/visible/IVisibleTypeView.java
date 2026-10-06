package com.xtc.moment.module.publish.visible;

import com.xtc.architecture.mvp.core.MvpView;

/**
 * 可见范围类型视图接口。
 */
public interface IVisibleTypeView extends MvpView {
    void changeResult(boolean changed);
}