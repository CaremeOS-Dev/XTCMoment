package com.xtc.aitext.bean;

/**
 * 创建 AI 文案请求体。
 */
public class CreatTextBody {

    private String aiText;
    private int clientType = 1;
    private int aiStyleId;

    public String getAiText() {
        return aiText;
    }

    public void setAiText(String aiText) {
        this.aiText = aiText;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getAiStyleId() {
        return aiStyleId;
    }

    public void setAiStyleId(int aiStyleId) {
        this.aiStyleId = aiStyleId;
    }

    @Override
    public String toString() {
        return "CreatTextBody{aiText='" + aiText + "', clientType=" + clientType + ", aiStyleId=" + aiStyleId + '}';
    }
}