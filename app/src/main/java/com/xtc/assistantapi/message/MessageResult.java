package com.xtc.assistantapi.message;

/**
 * 消息结果包装，携带结果码、描述与数据。
 */
public class MessageResult<T> {

    private String code;
    private String desc;
    private T data;

    public MessageResult() {
    }

    public MessageResult(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{code='" + code + "', desc='" + desc + "', data=" + data + '}';
    }
}