package com.xtc.aitext.bean;

/**
 * 领取成功描述。
 */
public class SuccessDescription {

    private int action;
    private String successMsg;

    public void setAction(int action) {
        this.action = action;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}