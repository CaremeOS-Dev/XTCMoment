package com.xtc.moment.module.report.interfaces;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.module.report.view.AbsBaseJumpView;

/**
 * 举报流程分步跳转视图接口。
 */
public interface IJumpView extends MvpView {
    void showNextView(AbsBaseJumpView view);

    void showPreviousView(AbsBaseJumpView view);

    void finishView();
}