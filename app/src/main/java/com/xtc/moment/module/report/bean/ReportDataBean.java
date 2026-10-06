package com.xtc.moment.module.report.bean;

/**
 * 举报入口状态数据。
 */
public class ReportDataBean {

    public static final int NORMAL_STATE = 0;
    public static final int NOT_QUALIFIED_STATE = 1;
    public static final int REPORT_TODAY_STATE = 2;
    public static final int REPORT_PERSON_STATE = 3;
    public static final int REPORT_PERSON_BANNER = 4;

    private int state = NORMAL_STATE;

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }
}