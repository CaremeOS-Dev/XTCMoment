package com.xtc.aitext.bean;

/**
 * AI 文案创作状态。
 */
public class AICreatStatusBean {

    private int status;
    private Integer id;
    private long waitingTime;
    private String AIBackContent;
    private int remainTimes;

    public AICreatStatusBean() {
        this.id = 0;
    }

    public AICreatStatusBean(int status, long waitingTime) {
        this.id = 0;
        this.status = status;
        this.waitingTime = waitingTime;
    }

    public AICreatStatusBean(int status, long waitingTime, Integer id) {
        this.id = 0;
        this.status = status;
        this.waitingTime = waitingTime;
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setAIBackContent(String aiBackContent) {
        this.AIBackContent = aiBackContent;
    }

    public String getAIBackContent() {
        return AIBackContent;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setRemainTimes(int remainTimes) {
        this.remainTimes = remainTimes;
    }

    public int getRemainTimes() {
        return remainTimes;
    }

    @Override
    public String toString() {
        return "AICreatStatusBean{status=" + status + "id=" + id + ", waitingTime=" + waitingTime
                + ", AIBackContent='" + AIBackContent + "', remainTimes='" + remainTimes + "'}";
    }
}