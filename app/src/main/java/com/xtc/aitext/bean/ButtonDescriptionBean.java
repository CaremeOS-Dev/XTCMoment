package com.xtc.aitext.bean;

/**
 * 权益按钮描述。
 */
public class ButtonDescriptionBean {

    private String obtainDescription;
    private int obtainStatus;
    private int defaultCall;
    private String obtainedTips;
    private WatchBoxContent watchBoxContent;

    public String getObtainDescription() {
        return obtainDescription;
    }

    public void setObtainDescription(String obtainDescription) {
        this.obtainDescription = obtainDescription;
    }

    public int getObtainStatus() {
        return obtainStatus;
    }

    public void setObtainStatus(int obtainStatus) {
        this.obtainStatus = obtainStatus;
    }

    public int getDefaultCall() {
        return defaultCall;
    }

    public void setDefaultCall(int defaultCall) {
        this.defaultCall = defaultCall;
    }

    public WatchBoxContent getWatchBoxContent() {
        return watchBoxContent;
    }

    public void setWatchBoxContent(WatchBoxContent watchBoxContent) {
        this.watchBoxContent = watchBoxContent;
    }

    public String getObtainedTips() {
        return obtainedTips;
    }

    public void setObtainedTips(String obtainedTips) {
        this.obtainedTips = obtainedTips;
    }

    @Override
    public String toString() {
        return "ButtonDescriptionBean{obtainDescription='" + obtainDescription + "', obtainStatus=" + obtainStatus
                + ", defaultCall=" + defaultCall + ", obtainedTips='" + obtainedTips + "', watchBoxContent="
                + watchBoxContent + '}';
    }
}