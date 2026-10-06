package com.xtc.aitext.bean;

/**
 * 领取次数的响应。
 */
public class ObtainTimeBean {

    private String accessDescription;
    private int obtainResult;
    private int successTime;
    private String failedMsg;
    private SuccessDescription successDescription;
    private ButtonDescriptionBean buttonDescription;

    public String getAccessDescription() {
        return accessDescription;
    }

    public void setAccessDescription(String accessDescription) {
        this.accessDescription = accessDescription;
    }

    public int getObtainResult() {
        return obtainResult;
    }

    public void setObtainResult(int obtainResult) {
        this.obtainResult = obtainResult;
    }

    public int getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(int successTime) {
        this.successTime = successTime;
    }

    public String getFailedMsg() {
        return failedMsg;
    }

    public void setFailedMsg(String failedMsg) {
        this.failedMsg = failedMsg;
    }

    public ButtonDescriptionBean getButtonDescription() {
        return buttonDescription;
    }

    public void setButtonDescription(ButtonDescriptionBean buttonDescription) {
        this.buttonDescription = buttonDescription;
    }

    public void setSuccessDescription(SuccessDescription successDescription) {
        this.successDescription = successDescription;
    }

    public SuccessDescription getSuccessDescription() {
        return successDescription;
    }

    @Override
    public String toString() {
        return "ObtainTimeBean{accessDescription='" + accessDescription + "', obtainResult=" + obtainResult
                + ", successTime=" + successTime + ", failedMsg='" + failedMsg + "', successDescription="
                + successDescription + ", buttonDescriptionBean=" + buttonDescription + '}';
    }
}