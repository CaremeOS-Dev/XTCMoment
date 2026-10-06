package com.xtc.aitext.bean;

/**
 * AI 文案创作记录明细。
 */
public class AIRecordDetailBean {

    private String requestText;
    private String resultText;
    private String createTime;
    private String watchId;
    private int realWaitingTime;
    private int waitingTime;
    private String updateTime;
    private String aiStyleTextName;
    private Integer id;
    private int aiTextStatus;
    private int actualWaitingTime;

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getWatchId() {
        return watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getRealWaitingTime() {
        return realWaitingTime;
    }

    public void setRealWaitingTime(int realWaitingTime) {
        this.realWaitingTime = realWaitingTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAiStyleTextName() {
        return aiStyleTextName;
    }

    public void setAiStyleTextName(String aiStyleTextName) {
        this.aiStyleTextName = aiStyleTextName;
    }

    public int getAiTextStatus() {
        return aiTextStatus;
    }

    public void setAiTextStatus(int aiTextStatus) {
        this.aiTextStatus = aiTextStatus;
    }

    public Integer getActualWaitingTime() {
        return actualWaitingTime;
    }

    public void setActualWaitingTime(Integer actualWaitingTime) {
        this.actualWaitingTime = actualWaitingTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AIRecordBean{requestText='" + requestText + "', resultText='" + resultText + "', createTime='"
                + createTime + "', watchId='" + watchId + "', realWaitingTime=" + realWaitingTime
                + ", waitingTime=" + waitingTime + ", updateTime='" + updateTime + "', aiStyleTextName='"
                + aiStyleTextName + "', id='" + id + "', aiTextStatus=" + aiTextStatus + ", actualWaitingTime="
                + actualWaitingTime + '}';
    }
}