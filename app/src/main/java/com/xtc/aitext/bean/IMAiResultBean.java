package com.xtc.aitext.bean;

/**
 * IM 通道回传的 AI 文案结果。
 */
public class IMAiResultBean {

    private int remainTimes;
    private boolean callResult;
    private String resultText;
    private int clientType;

    public int getRemainTimes() {
        return remainTimes;
    }

    public void setRemainTimes(int remainTimes) {
        this.remainTimes = remainTimes;
    }

    public boolean isCallResult() {
        return callResult;
    }

    public void setCallResult(boolean callResult) {
        this.callResult = callResult;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public String getResultText() {
        return resultText;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getClientType() {
        return clientType;
    }

    @Override
    public String toString() {
        return "IMAiResultBean{remainTimes=" + remainTimes + ", callResult=" + callResult + ", resultText='"
                + resultText + "', clientType='" + clientType + "'}";
    }
}