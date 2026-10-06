package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

/** 通用业务请求（命令 44）的响应。 */
@CommandValue(45)
public class ModeResponseEntity extends ResponseEntity {

    @TagValue(1)
    private int rId;

    @TagValue(2)
    private int respCode;

    @TagValue(3)
    private String respDesc;

    @TagValue(10)
    private String result;

    @Override
    public int getRID() {
        return this.rId;
    }

    @Override
    public void setRID(int rId) {
        this.rId = rId;
    }

    @Override
    public int getCode() {
        return this.respCode;
    }

    @Override
    public void setCode(int respCode) {
        this.respCode = respCode;
    }

    @Override
    public String getDesc() {
        return this.respDesc;
    }

    @Override
    public void setDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ModeResponseEntity{rId=" + this.rId + ", respCode=" + this.respCode + ", respDesc='" + this.respDesc
                + "'" + ", result='" + this.result + "'" + "}";
    }
}