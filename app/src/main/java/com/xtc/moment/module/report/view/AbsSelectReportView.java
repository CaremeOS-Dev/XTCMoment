package com.xtc.moment.module.report.view;

import android.content.Context;
import android.util.AttributeSet;

import com.xtc.moment.module.report.ReportPresenter;
import com.xtc.moment.module.report.interfaces.IJumpView;

/**
 * 需要 Presenter 参与的举报步骤页面基类。
 */
public abstract class AbsSelectReportView extends AbsBaseJumpView {

    /** 记录按钮的防抖延迟，单位毫秒。 */
    private static final long RECORD_BTN_DELAY_TIME = 400;

    protected ReportPresenter reportPresenter;
    protected boolean isScrolling;

    protected abstract void loadData();

    public AbsSelectReportView(Context context, IJumpView iJumpView) {
        super(context, iJumpView);
        this.isScrolling = false;
    }

    public AbsSelectReportView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isScrolling = false;
    }

    public void setReportPresenter(ReportPresenter reportPresenter) {
        this.reportPresenter = reportPresenter;
        loadData();
    }
}