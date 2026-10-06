package com.xtc.moment.module.report;

/**
 * 举报原因选项。
 */
public class ReasonsBean {

    private String reason;
    private boolean isChecked;
    private int code;

    public ReasonsBean(String reason, boolean isChecked) {
        this.reason = reason;
        this.isChecked = isChecked;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }

    @Override
    public String toString() {
        return "ReasonsBean{reason='" + this.reason + "', isChecked=" + this.isChecked + ", code='" + this.code + "'}";
    }
}