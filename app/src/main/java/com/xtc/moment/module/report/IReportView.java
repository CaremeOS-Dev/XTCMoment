package com.xtc.moment.module.report;

import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.interfaces.IJumpView;

/**
 * 举报页面视图接口。
 */
public interface IReportView extends IJumpView {
    void getReportDataSuccess(ReportDataBean reportData);

    void getReportDataFail(String message);

    void reportSuccess();

    void reportFail(String message);
}