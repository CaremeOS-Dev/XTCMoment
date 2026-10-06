package com.xtc.moment.module.report.helper;

import com.xtc.moment.MomentApp;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.StartReportRequest;

import java.util.ArrayList;
import java.util.Date;

/**
 * 举报流程数据管理。
 */
public class ReportDataManage {

    private final StartReportRequest startReportRequest = new StartReportRequest();
    private ReportDataBean reportDataBean;

    public StartReportRequest getStartReportRequest() {
        return this.startReportRequest;
    }

    public ReportDataBean getReportDataBean() {
        if (this.reportDataBean == null) {
            this.reportDataBean = new ReportDataBean();
        }
        return this.reportDataBean;
    }

    public void setInformType(int informType) {
        this.startReportRequest.setInformType(informType);
    }

    public void setReportData(String friendWatchId, String momentId, String commentId, int reportMomentType,
            ArrayList<StartReportRequest.Contents> contents, int informSource, String momentWatchId) {
        this.startReportRequest.setCommentId(commentId);
        this.startReportRequest.setInformTime(new Date().getTime());
        this.startReportRequest.setFriendWatchId(friendWatchId);
        this.startReportRequest.setMomentId(momentId);
        this.startReportRequest.setReportMomentType(reportMomentType);
        this.startReportRequest.setWatchId(MomentApp.getWatchId());
        this.startReportRequest.setContents(contents);
        this.startReportRequest.setInformSource(informSource);
        this.startReportRequest.setMomentWatchId(momentWatchId);
    }

    public String getFriendId() {
        return this.startReportRequest.getFriendWatchId();
    }
}