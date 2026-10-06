package com.xtc.web.core.data.resp;

/** JS 调用 native 的统一返回体。 */
public class RespJsBase {

    public interface Code {
        String CALL_FAIL = "000005";
        String NO_ANNOTATION = "000004";
        String NO_API = "000002";
        String NO_METHOD = "000003";
        String SUCCESS = "000001";
    }

    private String code;
    private Object data;
    private String desc;

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public Object getData() {
        return this.data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespJsBase{code='" + this.code + "', desc='" + this.desc + "', data=" + this.data + '}';
    }
}