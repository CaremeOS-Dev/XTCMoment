package com.xtc.web.core.data.resp;

/** 语音识别结果。 */
public class RespVoiceResult {

    public interface Code {
        String FAIL = "000002";
        String NOT_PERMISSION = "000003";
        String SUCCESS = "000001";
    }

    private String code;
    private String content;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RespVoiceResult{code='" + this.code + "', content='" + this.content + "'}";
    }
}